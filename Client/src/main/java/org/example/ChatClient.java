package org.example;

import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.Scanner;

public class ChatClient {
    private static String SERVER_ADDRESS;
    private static int SERVER_PORT;

    public static void main(String[] args) throws Exception {
        loadConfiguration();
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

    private static void loadConfiguration() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            input = classLoader.getResourceAsStream("config.properties");
            if (input == null) {
                System.err.println("Unable to find config.properties");
                return;
            }

            prop.load(input);

            SERVER_ADDRESS = prop.getProperty("server.address");
            SERVER_PORT = Integer.parseInt(prop.getProperty("server.port"));
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}