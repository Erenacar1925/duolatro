package server;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket.getInetAddress());
            ClientHandler handler = new ClientHandler(clientSocket); // Only pass the socket!
            new Thread(handler).start();
        }
    }
}