package osemes.javapro.dz26;

import java.io.*;
import java.net.Socket;
//2
public class ChatClientSecond {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8000);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream());
             BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in))) {

            // Read the welcome message from the server
            String serverMessage = reader.readLine();
            System.out.println(serverMessage);

            // Read user's name and send it to the server
            String name = userInputReader.readLine();
            writer.println(name);
            writer.flush();

            // Start a separate thread to receive messages from the server
            new Thread(() -> {
                try {
                    String message;
                    while ((message = reader.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Handle user input
            String userInput;
            while ((userInput = userInputReader.readLine()) != null) {
                writer.println(userInput);
                writer.flush();

                if ("-exit".equals(userInput)) {
                    // The user entered the command to exit, so we close the socket and break the loop
                    socket.close();
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
