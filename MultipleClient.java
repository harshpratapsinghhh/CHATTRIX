
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class MultipleClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 5000); // connect to server
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        Scanner sc = new Scanner(System.in);

        System.out.println("Connected to server. Type messages (type 'exit' to quit):");

        while (true) {
            System.out.print("You: ");
            String msg = sc.nextLine();
            if (msg.equalsIgnoreCase("exit")) break;

            out.println(msg);               // send message to server
            String response = in.readLine(); // read server response
            System.out.println("Server: " + response);
        }

        socket.close();
        sc.close();
        System.out.println("Disconnected from server.");
    }
}
