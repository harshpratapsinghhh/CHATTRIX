package storage;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatHistoryStore {

    private static final String BASE_DIR = "data/chats/";

    static {
        new File(BASE_DIR).mkdirs();
    }

    private static String getFileName(String u1, String u2) {
        if (u1.compareToIgnoreCase(u2) < 0)
            return u1 + "_" + u2 + ".txt";
        else
            return u2 + "_" + u1 + ".txt";
    }

    public static synchronized void save(String sender, String receiver, String msg) {
        String file = BASE_DIR + getFileName(sender, receiver);
        try (FileWriter fw = new FileWriter(file, true)) {
            String time = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("HH:mm"));
            fw.write("[" + time + "] " + sender + ": " + msg + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Updated load method to return chat as String
    public static synchronized java.util.List<String> load(String u1, String u2) {

        java.util.List<String> list = new java.util.ArrayList<>();
        String file = BASE_DIR + getFileName(u1, u2);

        File f = new File(file);
        if (!f.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }
}
