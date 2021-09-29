package ru.gb.server;

import ru.gb.server.auth.AuthService;
import ru.gb.server.auth.InMemoryAuthService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class JulyChatServer {

    private AuthService authService;
    private static final int PORT = 8089;
    private List<ChatClientHandler> handlers;



    public JulyChatServer() {
        this.authService = new InMemoryAuthService();
        this.handlers = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server start!");
            while (true) {
                System.out.println("Waiting for connection......");
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");
                new ChatClientHandler(socket, this);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void broadcastMessage(String message){
        for (ChatClientHandler handler : handlers){
            handler.sendMessage(message);
        }
    }

    public synchronized void addAutrizedClientToList(ChatClientHandler handler){
        this.handlers.add(handler);
    }

    public void broadcast(String message) {
        for (ChatClientHandler handler : handlers) {
            handler.send(message);
        }
    }
    public AuthService getAuthService() {
        return authService;
    }

}