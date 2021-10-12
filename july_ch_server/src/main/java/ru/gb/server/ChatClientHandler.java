package ru.gb.server;

import ru.gb.server.error.UserNotFoundException;
import ru.gb.server.error.WrongCredentialsException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;

public class ChatClientHandler {
    public static final String REGEX = "%&%";
    private static final long AUTH_TIMEOUT = 12_000;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private Thread handlerThread;
    private JulyChatServer server;
    private String currentUser;
    private ExecutorService executorService;

    public ChatClientHandler(Socket socket, JulyChatServer server) {
        try {
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Handler created");
            this.server = server;
            this.executorService = server.getExecutorService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handle() {
       executorService.execute(() ->{
 //      handlerThread = new Thread(() -> {
            authorize();
            try {
                while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                    String message = in.readUTF();
                    handleMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                server.removeAuthorizedClientFromList(this);
            }
        });
 //       handlerThread.start();
    }

    //auth: lllll ppppp
    private void authorize() {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        if (currentUser == null) {
                            sendMessage("ERROR:" + REGEX + "Authentication timeout!\nPlease, try again later!");
                            Thread.sleep(50);
                            socket.close();
                        }
                    }
                } catch (InterruptedException | IOException e) {
                    e.getStackTrace();
                }
            }
        }, AUTH_TIMEOUT);
        while (!socket.isClosed()) {
            try {
                String message = in.readUTF();
                if (message.startsWith("/auth") || message.startsWith("/register")) {
                    if (handleMessage(message)) break;
                }
            } catch (SocketException e) {
                System.out.println("Socket closed with timeout");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean handleMessage(String message) {
        try {
            String[] parsed = message.split(REGEX);
            switch (parsed[0]) {
                case "/w":
                    server.sendPrivateMessage(this.currentUser, parsed[1], parsed[2], this);
                    break;
                case "/ALL":
                    server.broadcastMessage(this.currentUser, parsed[1]);
                    break;
                case "/change_nick":
                    String nick = server.getAuthService().changeNickname(this.currentUser, parsed[1]);
                    server.removeAuthorizedClientFromList(this);
                    this.currentUser = nick;
                    server.addAuthorizedClientToList(this);
                    sendMessage("/change_nick_ok");
                    break;
                case "/change_pass":
                    server.getAuthService().changePassword(this.currentUser, parsed[1], parsed[2]);
                    sendMessage("/change_pass_ok");
                    break;
                case "/remove":
                    server.getAuthService().deleteUser(this.currentUser);
                    this.socket.close();
                    break;
                case "/register":
                    server.getAuthService().createNewUser(parsed[1], parsed[2], parsed[3]);
                    sendMessage("register_ok:");
                    break;
                case "/auth":
                    try {
                        String username = server.getAuthService().getNicknameByLoginAndPassword(parsed[1], parsed[2]);
                        if (server.isNicknameBusy(username)) {
                            sendMessage("ERROR:" + REGEX + "U're clone!");
                        } else {
                            this.currentUser = username;
                            this.server.addAuthorizedClientToList(this);
                            sendMessage("authok:" + REGEX + this.currentUser);
                            return true;
                        }
                    } catch (UserNotFoundException e) {
                        System.out.println("Auth error");
                        sendMessage("ERROR:" + REGEX + "Wrong username or pass");
                    }
                    break;
                default:
                    sendMessage("ERROR:" + REGEX + "command not found!");
            }
        } catch (Exception e) {
            sendMessage("ERROR:" + REGEX + e.getMessage());
        }
        return false;
    }



    public String getCurrentUser() {
        return currentUser;
    }

    public void sendMessage(String message) {
        try {
            this.out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



