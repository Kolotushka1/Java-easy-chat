package org.example;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);

        try {
            System.out.println("Connected to chat server");
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Thread messageReader = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = reader.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    System.out.println("Error reading from server: " + e.getMessage());
                }
            });
            messageReader.start();

            while (true) {
                String userInput = scanner.nextLine();
                if (userInput.equalsIgnoreCase("quit")) {
                    break;
                }
                out.println(userInput);
            }
        } finally {
            socket.close();
            System.out.println("Disconnected from chat server");
        }
    }
}
