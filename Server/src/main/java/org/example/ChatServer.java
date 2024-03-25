package org.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static Set<String> userNames = new HashSet<>();
    private static Set<PrintWriter> writers = new HashSet<>();

    private static LinkedList<String> messageHistory = new LinkedList<>();

    public static void main(String[] args) throws Exception {
        System.out.println("Chat Server is running on port " + PORT);
        ServerSocket listener = new ServerSocket(PORT);

        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    private static class Handler extends Thread {
        private String name;
        private Socket socket;
        private Scanner in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    out.println("SUBMITNAME");
                    name = in.nextLine();
                    if (name == null || name.isEmpty() || userNames.contains(name)) {
                        out.println("NAMEALREADYEXISTS");
                    } else {
                        userNames.add(name);
                        break;
                    }
                }

                out.println("NAMEACCEPTED");
                writers.add(out);

                synchronized (messageHistory) {
                    for (String message : messageHistory) {
                        out.println(message);
                    }
                }

                while (true) {
                    String input = in.nextLine();
                    if (input == null) {
                        return;
                    }

                    synchronized (messageHistory) {
                        if (messageHistory.size() == 50) {
                            messageHistory.removeFirst();
                        }
                        messageHistory.addLast("MESSAGE " + name + ": " + input);
                    }

                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE " + name + ": " + input);
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (name != null) {
                    userNames.remove(name);
                }
                if (out != null) {
                    writers.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
