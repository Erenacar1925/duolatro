package client.controller;

import client.NetworkClient;
import client.ScreenManager;
import client.model.Message;
import com.google.gson.Gson;

import java.io.IOException;

public class StartController {
    private final NetworkClient networkClient;
    private final ScreenManager screenManager;

    public StartController(NetworkClient networkClient, ScreenManager screenManager) {
        this.networkClient = networkClient;
        this.screenManager = screenManager;
    }

    public void onPlayButtonClicked() {
        // Connect to server if not already done
        // (optional: could move connection setup to app startup)
        try {
            if (!networkClient.isConnected()) {
                networkClient.connect();
            }
            // Listen for JOIN_SUCCESS
            networkClient.setMessageListener(msg -> {
                Message message = new Gson().fromJson(msg, Message.class);
                if ("JOIN_SUCCESS".equals(message.getType())) {
                    String assignedPlayerName = message.getPayload().toString();
                    networkClient.setPlayerName(assignedPlayerName);
                    // Remove this listener after joining, OR switch to lobby immediately
                    javafx.application.Platform.runLater(() -> screenManager.showLobbyScreen());
                }
            });

            // Send JOIN message
            Message joinMsg = new Message("JOIN", "");
            String json = new Gson().toJson(joinMsg);
            networkClient.sendMessage(json);
        } catch (IOException e) {
            e.printStackTrace();
            // Show error dialog or similar
        }
    }
}

