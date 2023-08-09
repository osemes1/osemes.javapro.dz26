package osemes.javapro.dz26;
//2
public interface ChatConnectionHandler {
    void onConnect(ChatConnection connection);
    void onMessage(ChatConnection connection, String message);
    void onDisconnect(ChatConnection connection);
}
