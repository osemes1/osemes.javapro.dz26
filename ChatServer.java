package osemes.javapro.dz26;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChatServer implements ChatConnectionHandler, AutoCloseable {
    private final static int DEFAULT_PORT = 8000;
    private final ServerSocket serverSocket;
    private final List<ChatConnection> connections;
//2
    public ChatServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        connections = new ArrayList<>();
    }

    public ChatServer() throws IOException {
        this(DEFAULT_PORT);
    }

    public void start() {

        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();

                ChatConnection chatConnection = new ChatConnection(socket, this);

            } catch (IOException e) {
                // Handle IOException gracefully
                e.printStackTrace();
            }
        }
    }

    public synchronized void stop() {
        try {
            serverSocket.close();
            System.out.println("Chat server stopped.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void onConnect(ChatConnection connection) {
        String formattedTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String formattedNotification = String.format("[%s][%s]: %s entered the chat%n", "SERVER", formattedTime, connection.getName());
        connections.add(connection);
        connections.forEach(c -> c.sendMessage(formattedNotification));
    }

    @Override
    public synchronized void onMessage(ChatConnection connection, String message) {
        String formattedTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String formattedMessage = String.format("[%s][%s]: %s", connection.getName(), formattedTime, message);

        for (ChatConnection chatConnection : connections) {
            if (!chatConnection.equals(connection)) {
                chatConnection.sendMessage(formattedMessage);
            }
        }
    }

    @Override
    public synchronized void onDisconnect(ChatConnection connection) {
        connections.remove(connection);
        String formattedTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String formattedNotification = String.format("[%s][%s]: %s left the chat%n", "SERVER", formattedTime, connection.getName());
        System.out.println(formattedNotification);
        connections.forEach(c -> c.sendMessage(formattedNotification));
    }

    @Override
    public void close() throws Exception {
        stop();
    }

    public static void main(String[] args) {
        try (ChatServer chatServer = new ChatServer()) {
            chatServer.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
