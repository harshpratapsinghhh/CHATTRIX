import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient2 extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private PrintWriter out;
    private String userName;
    private String groupName;

    public ChatClient2(String serverAddress, int port) {

        groupName = JOptionPane.showInputDialog(this, "Enter Group/Chat Room Name:", "Group Selection", JOptionPane.PLAIN_MESSAGE);
        if (groupName == null || groupName.trim().isEmpty()) {
            groupName = "General";
        }

        userName = JOptionPane.showInputDialog(this, "Enter your username:", "Login", JOptionPane.PLAIN_MESSAGE);
        if (userName == null || userName.trim().isEmpty()) {
            userName = "User" + (int) (Math.random() * 1000);
        }

        setTitle("Chat Room - " + groupName);
        setSize(500, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel topLabel = new JLabel("💬 Welcome to " + groupName + " Chat Room", JLabel.CENTER);
        topLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(topLabel, BorderLayout.NORTH);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        chatArea.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputField = new JTextField();
        inputField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sendButton = new JButton("Send");
        sendButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        try {
            Socket socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(groupName);
            out.println(userName);

            new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        chatArea.append(line + "\n");
                    }
                } catch (IOException e) {
                    chatArea.append("Connection closed.\n");
                }
            }).start();

            ActionListener sendAction = e -> {
                String msg = inputField.getText().trim();
                if (!msg.isEmpty()) {
                    out.println(msg);
                    chatArea.append("Me: " + msg + "\n");
                    inputField.setText("");
                }
            };

            inputField.addActionListener(sendAction);
            sendButton.addActionListener(sendAction);

        } catch (IOException e) {
            chatArea.append("Unable to connect to server.\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatClient client = new ChatClient("localhost", 5000);
            client.setVisible(true);
        });
    }
}
