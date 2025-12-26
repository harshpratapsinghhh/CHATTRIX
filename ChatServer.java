import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Set<ClientHandler> s1 = new HashSet<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Server started on port 5000...");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("New client connected: " + socket);
            ClientHandler handler = new ClientHandler(socket);
            s1.add(handler);
            handler.start();
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String userName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // here we read data form user for username
                userName = in.readLine();
                System.out.println(userName + " joined the chat.");
                broadcast(userName + " joined the chat!", this);

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("exit")) {
                        break;
                    }
                    System.out.println(userName + ": " + message);
                    broadcast(userName + ": " + message, this);
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {}
                s1.remove(this);
                broadcast(userName + " left the chat.", this);
                System.out.println(userName + " disconnected.");
            }
        }

        private void broadcast(String message, ClientHandler excludeUser) {
            for (ClientHandler client : s1) {
                if (client != excludeUser) {
                    client.out.println(message);
                }
            }
        }
    }
}
