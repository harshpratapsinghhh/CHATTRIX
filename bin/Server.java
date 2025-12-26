package bin;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 1234;
    private static ArrayList<ClientHandler> clients = new ArrayList<>();

    public static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("🚀 Server started on port " + PORT);
            System.out.println("Waiting for clients to connect...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("✅ New client connected: " + socket);
                ClientHandler clientHandler = new ClientHandler(socket, clients);
                clients.add(clientHandler);
                clientHandler.start();
            }

        } catch (IOException e) {
            System.out.println("❌ Server Error: " + e.getMessage());
        }
    }

    // Broadcast message to all clients
    public static synchronized void broadcast(String message, ClientHandler excludeUser) {
        for (ClientHandler client : clients) {
            if (client != excludeUser) {
                client.sendMessage(message);
            }
        }
    }

    // Remove client on disconnect
    public static synchronized void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        System.out.println("Client disconnected.");
    }
}
