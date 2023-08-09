package osemes.javapro.dz26;

public class Main {
    public static void main(String[] args) {
        try (ChatServer chatServer = new ChatServer()) {
            chatServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
