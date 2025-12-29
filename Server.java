import java.io.*;
import java.net.*;
import java.util.*;
import storage.ChatHistoryStore;

public class Server {

    private static Map<String, ClientHandler> users =
            Collections.synchronizedMap(new HashMap<>());

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(5000);
        System.out.println("Server running on port 5000...");

        while (true) {
            Socket socket = ss.accept();
            new ClientHandler(socket).start();
        }
    }

    static class ClientHandler extends Thread {

        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String userName;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // username
                userName = in.readLine();
                users.put(userName, this);
                System.out.println(userName + " connected");

                // broadcast user list
                broadcastUserList();

                String line;
                while ((line = in.readLine()) != null) {

                    if (line.startsWith("TO:")) {
                        handlePrivateMessage(line);
                    }
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            } finally {
                users.remove(userName);
                broadcastUserList();
                try { socket.close(); } catch (IOException ignored) {}
                System.out.println(userName + " disconnected");
            }
        }

        private void handlePrivateMessage(String data) {
            try {
                String[] parts = data.split("\\|");
                String receiver = parts[0].substring(3);
                String message = parts[1].substring(4);

                // save chat
                ChatHistoryStore.save(userName, receiver, message);

                // send to receiver if online
                ClientHandler rc = users.get(receiver);
                if (rc != null) {
                    rc.out.println(userName + ": " + message);
                }

                // send to self
                out.println("Me: " + message);

            } catch (Exception e) {
                out.println("Invalid message format");
            }
        }

        private void broadcastUserList() {
            StringBuilder sb = new StringBuilder();
            sb.append("USER_LIST:");
            synchronized (users) {
                for (String u : users.keySet()) {
                    sb.append(u).append(",");
                }
            }
            String listMessage = sb.toString();
            synchronized (users) {
                for (ClientHandler c : users.values()) {
                    c.out.println(listMessage);
                }
            }
        }
    }
}
