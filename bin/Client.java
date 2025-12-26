package bin;

// Client.java
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class Client {
    private static Socket socket;
    private static BufferedReader reader;
    private static PrintWriter writer;

    private static JFrame frame;
    private static JTextArea chatArea;
    private static JTextField messageField;
    private static JButton sendButton;

    public static void startClient(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            String name = JOptionPane.showInputDialog("Enter your name:");
            writer.println(name);

            createUI(name);
            listenForMessages();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "❌ Could not connect to server.");
        }
    }

    private static void createUI(String name) {
        frame = new JFrame("ChatApp - " + name);
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 15));
        JScrollPane scrollPane = new JScrollPane(chatArea);

        messageField = new JTextField();
        sendButton = new JButton("Send");

        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());

        frame.add(scrollPane, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private static void sendMessage() {
        String msg = messageField.getText();
        if (!msg.isEmpty()) {
            writer.println(msg);
            messageField.setText("");
        }
    }

    private static void listenForMessages() {
        Thread thread = new Thread(() -> {
            String msg;
            try {
                while ((msg = reader.readLine()) != null) {
                    chatArea.append(msg + "\n");
                }
            } catch (IOException e) {
                chatArea.append("❌ Disconnected from server.\n");
            }
        });
        thread.start();
    }
}
