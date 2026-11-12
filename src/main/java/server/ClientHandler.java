package server;

import client.model.Message;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String playerName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                Message message = new Gson().fromJson(line, Message.class);

                if ("JOIN".equals(message.getType())) {
                    this.playerName = LobbyManager.getInstance().addPlayer(this);
                    LobbyManager.getInstance().sendJoinSuccessAndUpdate(this, playerName);
                } else {
                    // Pass all other messages to the LobbyManager
                    if (playerName != null) {
                        LobbyManager.getInstance().handleMessage(this, message, playerName);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                if (playerName != null) {
                    LobbyManager.getInstance().removePlayer(this, playerName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String message) {
        out.println(message);
    }

    public String getPlayerName() {
        return playerName;
    }
}
