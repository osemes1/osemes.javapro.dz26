package osemes.javapro.dz26;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
//2
public class ChatConnection {
    private final Socket socket;
    private final ChatConnectionHandler connectionHandler;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private String name;

    public ChatConnection(Socket socket, ChatConnectionHandler connectionHandler) throws IOException {
        this.socket = socket;
        this.connectionHandler = connectionHandler;

        InputStream inputStream = socket.getInputStream();
        reader = new BufferedReader(new InputStreamReader(inputStream));

        OutputStream outputStream = socket.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream));

        new Thread(() -> {
            try(socket) {
                writer.println("Please enter your name: ");
                writer.flush();
                name = reader.readLine();

                connectionHandler.onConnect(this);

                while (!socket.isClosed()) {

                    String message = reader.readLine();
                    if (message == null) {
                        break;
                    }

                    connectionHandler.onMessage(this, message);


                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                connectionHandler.onDisconnect(this);
            }


        }).start();
    }

    public String getName() {
        return name;
    }

    public void sendMessage(String message) {
        writer.println(message);
        writer.flush();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatConnection that = (ChatConnection) o;
        return Objects.equals(socket, that.socket) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(socket, name);
    }

    @Override
    public String toString() {
        return "ChatConnection{" +
                "socket=" + socket +
                ", connectionHandler=" + connectionHandler +
                ", reader=" + reader +
                ", writer=" + writer +
                ", name='" + name + '\'' +
                '}';
    }
}