package ru.gb.server;

import ru.gb.server.error.UserNotFoundException;
import ru.gb.server.error.WrongCredentialsException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClientHandler {

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private Thread handlerThread;
    private JulyChatServer server;
    private String currentUser;

    public ChatClientHandler(Socket socket, JulyChatServer server) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.server = server;
            System.out.println("Handler created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handle() {
        handlerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted() && socket.isConnected()) {
                autorize();
                try {
                    String message = in.readUTF();
                    server.broadcast(message);
                    System.out.printf("Client #%s: %s\n", this.currentUser, message);
                    server.broadcast(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        handlerThread.start();
    }


    // auth: lllll ppppp
    public void autorize(){
        while (true){
            try {
                String message = in.readUTF();
                if (message.startsWith("auth:")){
                    String [] credentials = message.substring(6).split("\\s");
                    try {
                        this.currentUser = server.getAuthService().getNicknameByLoginAndPassword(credentials[0],credentials[1]);
                        server.addAutrizedClientToList(this);
                        sendMessage("autok : " + this.currentUser);
                        break;
                    } catch (WrongCredentialsException e) {
                        sendMessage("Wrong credentials");
                    }catch (UserNotFoundException e){
                        sendMessage("");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            this.out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


