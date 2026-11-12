package client.controller;

import client.NetworkClient;
import client.ScreenManager;
import client.model.Message;
import client.view.Lobby;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public class LobbyController {
    private final NetworkClient networkClient;
    private final ScreenManager screenManager;
    private final Lobby lobbyView;

    private String myName = null;
    private boolean isReady = false;

    public LobbyController(NetworkClient networkClient, ScreenManager screenManager, Lobby lobbyView) {
        this.networkClient = networkClient;
        this.screenManager = screenManager;
        this.lobbyView = lobbyView;
        this.networkClient.setMessageListener(this::onServerMessage);

        // Get our player name from network client/session if needed
        this.myName = networkClient.getPlayerName();

        // Set up button handlers
        lobbyView.setReadyButtonHandler(e -> sendReady());
        lobbyView.setCancelReadyButtonHandler(e -> sendNotReady());
    }

    private void onServerMessage(String msg) {
        Gson gson = new Gson();
        Message message = gson.fromJson(msg, Message.class);

        switch (message.getType()) {
            case "LOBBY_UPDATE":
                List<String> players = gson.fromJson(
                        gson.toJson(message.getPayload()),
                        new TypeToken<List<String>>(){}.getType()
                );
                javafx.application.Platform.runLater(() -> updateLobby(players));
                break;
            case "LOBBY_READY_STATUS":
                List<Map<String, Object>> statuses = gson.fromJson(
                        gson.toJson(message.getPayload()),
                        new TypeToken<List<Map<String, Object>>>(){}.getType()
                );
                javafx.application.Platform.runLater(() -> updateLobbyReady(statuses));
                break;
            case "COUNTDOWN_START":
                int seconds = ((Double) message.getPayload()).intValue(); // Gson often deserializes to Double
                javafx.application.Platform.runLater(() -> lobbyView.startCountdown(seconds));
                break;
            case "COUNTDOWN_CANCEL":
                javafx.application.Platform.runLater(() -> lobbyView.cancelCountdown());
                break;
            case "GAME_START":
                javafx.application.Platform.runLater(() -> screenManager.showGameScreen());
                break;
        }
    }

    // Old update, only sets names
    private void updateLobby(List<String> players) {
        String p1 = players.size() > 0 ? players.get(0) : null;
        String p2 = players.size() > 1 ? players.get(1) : null;
        lobbyView.setPlayerSlot(0, p1, p1 != null);
        lobbyView.setPlayerSlot(1, p2, p2 != null);

        // Show/hide ready buttons based on 2 players
        if (players.size() == 2) {
            lobbyView.showReadyControls(true);
        } else {
            lobbyView.showReadyControls(false);
        }
    }

    // New: update lobby slots and ready states
    private void updateLobbyReady(List<Map<String, Object>> statuses) {
        for (int i = 0; i < statuses.size(); i++) {
            String name = (String) statuses.get(i).get("name");
            boolean ready = (boolean) statuses.get(i).get("ready");
            lobbyView.setPlayerSlotReady(i, name, ready);
            if (myName != null && myName.equals(name)) {
                this.isReady = ready;
                lobbyView.setReadyButtonState(ready); // shows "Ready" or "Cancel"
            }
        }
    }

    private void sendReady() {
        Message msg = new Message("PLAYER_READY", null);
        networkClient.sendMessage(new Gson().toJson(msg));
    }

    private void sendNotReady() {
        Message msg = new Message("PLAYER_NOT_READY", null);
        networkClient.sendMessage(new Gson().toJson(msg));
    }
}
