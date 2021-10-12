package ru.gb.server;

import ru.gb.server.auth.AuthService;
import ru.gb.server.auth.InMemoryAuthService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JulyChatServer {
    private static final int PORT = 8555;
    private AuthService authService;
    private Map<String, ChatClientHandler> handlers;
    private ExecutorService executorService;

    public JulyChatServer() {

        this.authService = new InMemoryAuthService();
        this.handlers = new HashMap<>();
        this.executorService = Executors.newCachedThreadPool();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server start!");
            authService.start();
            while (true) {
                System.out.println("Waiting for connection......");
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");
                new ChatClientHandler(socket, this).handle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            authService.stop();
        }
    }

    public void broadcastMessage(String from, String message) {
        message = String.format("[%s]: %s", from, message);
        for (ChatClientHandler handler : handlers.values()) {
            handler.sendMessage(message);
        }
    }

    public synchronized void removeAuthorizedClientFromList(ChatClientHandler handler) {
        this.handlers.remove(handler.getCurrentUser());
        sendClientsOnline();
    }

    public synchronized void addAuthorizedClientToList(ChatClientHandler handler) {
        this.handlers.put(handler.getCurrentUser(), handler);
        sendClientsOnline();
    }

    public AuthService getAuthService() {
        return authService;
    }
    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void sendClientsOnline() {
        StringBuilder sb = new StringBuilder("/list:").append(ChatClientHandler.REGEX);
        for (ChatClientHandler handler : handlers.values()) {
            sb.append(handler.getCurrentUser()).append(ChatClientHandler.REGEX);
        }
        String message = sb.toString();
        for (ChatClientHandler handler : handlers.values()) {
            handler.sendMessage(message);
        }
    }

    public void sendPrivateMessage(String sender, String recipient, String message, ChatClientHandler senderHandler) {
        ChatClientHandler handler = handlers.get(recipient);
        if (handler == null) {
            senderHandler.sendMessage(String.format("ERROR:%s recipient not found: %s", ChatClientHandler.REGEX, recipient));
            return;
        }
        message = String.format("[%s] -> [%s]: %s", sender, recipient, message);
        handler.sendMessage(message);
        senderHandler.sendMessage(message);
    }

    public boolean isNicknameBusy(String nickname) {
        return this.handlers.containsKey(nickname);
    }
}