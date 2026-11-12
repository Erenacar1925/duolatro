package client;
import java.io.*;
import java.net.Socket;

public class NetworkClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread listenerThread;
    private boolean connected = false;
    private String playerName;

    public void setPlayerName(String name) { this.playerName = name; }
    public String getPlayerName() { return this.playerName; }
    public interface MessageListener {
        void onMessage(String message);
    }

    private MessageListener messageListener;
    private final String host;
    private final int port;

    public NetworkClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        if (connected) return;
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        connected = true;

        listenerThread = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    if (messageListener != null) {
                        messageListener.onMessage(line);
                    }
                }
            } catch (IOException e) {
                connected = false;
                // Optionally notify UI
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public boolean isConnected() {
        return connected && socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    public void sendMessage(String jsonMessage) {
        if (isConnected()) {
            out.println(jsonMessage);
        } else {
            System.out.println("Not connected. Cannot send message.");
        }
    }

    public void close() {
        try {
            if (socket != null) socket.close();
            connected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


