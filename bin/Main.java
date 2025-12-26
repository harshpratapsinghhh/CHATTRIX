package bin;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=======================================");
        System.out.println("      Welcome to Java Chat App");
        System.out.println("=======================================");
        System.out.println("1️⃣  Run as Server");
        System.out.println("2️⃣  Run as Client");
        System.out.print("Enter choice: ");
        int choice = sc.nextInt();
        sc.nextLine();

        try {
            if (choice == 1) {
                Server.startServer();
            } else if (choice == 2) {
                System.out.print("Enter Server IP (e.g., 127.0.0.1): ");
                String ip = sc.nextLine();
                Client.startClient(ip, 1234);
            } else {
                System.out.println("Invalid choice!");
            }
        } catch (Exception e) {
            System.out.println("Error starting application: " + e.getMessage());
        }
        sc.close();
    }
}
