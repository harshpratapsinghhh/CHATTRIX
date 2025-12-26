package bin;

// ClientHandler.java

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private ArrayList<ClientHandler> clients;
    private String clientName;

    public ClientHandler(Socket socket, ArrayList<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            writer.println("Enter your name: ");
            clientName = reader.readLine();
            System.out.println(clientName + " joined the chat.");
            Server.broadcast("💬 " + clientName + " joined the chat!", this);

            String msg;
            while ((msg = reader.readLine()) != null) {
                System.out.println(clientName + ": " + msg);
                Server.broadcast(clientName + ": " + msg, this);
            }

        } catch (IOException e) {
            System.out.println("❌ Client connection lost.");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Server.removeClient(this);
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }
}
