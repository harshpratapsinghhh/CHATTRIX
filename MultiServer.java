
import java.io.*;
import java.net.*;

class ClientHandler extends Thread {
    Socket socket;
    ClientHandler(Socket socket) { this.socket = socket; }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println("Client: " + msg);
                out.println("Echo: " + msg);
            }
        } catch(IOException e) {
            System.out.println("Client disconnected.");
        }
    }
}

public class MultiServer {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(5000);
        System.out.println("Multi-client server started...");

        while (true) {
            Socket socket = server.accept();
            System.out.println("New client connected");
            new ClientHandler(socket).start();
        }
    }
}
