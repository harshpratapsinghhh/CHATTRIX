import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import storage.ChatHistoryStore;

public class Client3 extends JFrame {

    private JList<String> userList;
    private DefaultListModel<String> userListModel;

    private JPanel chatPanel;
    private JScrollPane chatScrollPane;
    private JTextField inputField;
    private JButton sendButton;

    private PrintWriter out;
    private BufferedReader in;

    private String userName;
    private String activeUser = null;

    // chat memory per user
    private Map<String, java.util.List<String>> chatMap = new HashMap<>();

    public Client3(String serverAddress, int port) {

        setTitle("CHATTRIX");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // LEFT: USER LIST
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane userScroll = new JScrollPane(userList);
        userScroll.setPreferredSize(new Dimension(180, 0));
        add(userScroll, BorderLayout.WEST);

        // RIGHT: CHAT PANEL
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatScrollPane = new JScrollPane(chatPanel);
        add(chatScrollPane, BorderLayout.CENTER);

        // BOTTOM INPUT
        JPanel bottom = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");
        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(sendButton, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        // USERNAME
        userName = JOptionPane.showInputDialog(this, "Enter username");
        if (userName == null || userName.isEmpty())
            userName = "User" + new Random().nextInt(1000);

        try {
            Socket socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(userName);

            // LISTEN FOR SERVER
            new Thread(this::listen).start();

            // SEND MESSAGE
            ActionListener sendAction = e -> sendMessage();
            sendButton.addActionListener(sendAction);
            inputField.addActionListener(sendAction);

            // USER SELECT
            userList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    activeUser = userList.getSelectedValue();
                    loadChat(activeUser);
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Server not reachable");
        }
    }

    private void listen() {
        try {
            String line;
            while ((line = in.readLine()) != null) {

                // UPDATE USER LIST
                if (line.startsWith("USER_LIST:")) {
                    String[] users = line.substring(10).split(",");
                    SwingUtilities.invokeLater(() -> {
                        userListModel.clear();
                        for (String u : users)
                            if (!u.equals(userName))
                                userListModel.addElement(u);
                    });
                    continue;
                }

                // PRIVATE MESSAGE FORMAT sender: message
                if (!line.contains(":")) continue;

                String sender = line.split(":", 2)[0].trim();
                String msg = line.split(":", 2)[1].trim();

                chatMap.putIfAbsent(sender, new ArrayList<>());
                chatMap.get(sender).add(sender + ": " + msg);

                if (sender.equals(activeUser)) {
                    SwingUtilities.invokeLater(() -> addMessage(sender + ": " + msg, false));
                }
            }
        } catch (IOException ignored) {}
    }

    private void sendMessage() {
        if (activeUser == null) {
            JOptionPane.showMessageDialog(this, "Select a user");
            return;
        }

        String msg = inputField.getText().trim();
        if (msg.isEmpty()) return;

        out.println("TO:" + activeUser + "|MSG:" + msg);

        chatMap.putIfAbsent(activeUser, new ArrayList<>());
        chatMap.get(activeUser).add("Me: " + msg);

        addMessage("Me: " + msg, true);
        inputField.setText("");
    }

    private void loadChat(String user) {

        chatPanel.removeAll();

        // LOAD FROM FILE (OLD CHAT)
        java.util.List<String> fileMessages = storage.ChatHistoryStore.load(userName, user);

        if (fileMessages != null) {
            for (String msg : fileMessages) {
                boolean isSender = msg.contains(userName + ":");
                addMessage(msg, isSender);
            }
        }

        // LOAD FROM CURRENT SESSION (MEMORY)
        java.util.List<String> sessionMessages = chatMap.get(user);
        if (sessionMessages != null) {
            for (String msg : sessionMessages) {
                boolean isSender = msg.startsWith("Me:");
                addMessage(msg, isSender);
            }
        }

        chatPanel.revalidate();
        chatPanel.repaint();
    }


    private void addMessage(String message, boolean isSender) {
        JPanel messagePanel = new JPanel(new FlowLayout(isSender ? FlowLayout.RIGHT : FlowLayout.LEFT));
        JLabel msgLabel = new JLabel("<html><p style='width:200px;'>" + message + "</p></html>");
        msgLabel.setOpaque(true);
        msgLabel.setBackground(isSender ? Color.CYAN : Color.LIGHT_GRAY);
        msgLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        messagePanel.add(msgLabel);
        chatPanel.add(messagePanel);
        chatPanel.revalidate();
        chatPanel.repaint();

        SwingUtilities.invokeLater(() -> chatScrollPane.getVerticalScrollBar().setValue(chatScrollPane.getVerticalScrollBar().getMaximum()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new ChatClient("localhost", 5000).setVisible(true)
        );
    }
}
