package server;

import client.model.Message;
import client.model.Card;
import com.google.gson.Gson;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LobbyManager {
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final List<String> playerNames = new CopyOnWriteArrayList<>();
    private final Map<String, Boolean> playerReady = new ConcurrentHashMap<>();
    private AtomicInteger playerCounter = new AtomicInteger(1);
    private volatile boolean countdownActive = false;
    private final GameManager gameManager = new GameManager();

    public GameManager getGameManager() {
        return gameManager;
    }

    // Singleton pattern
    private static final LobbyManager instance = new LobbyManager();
    public static LobbyManager getInstance() { return instance; }

    public synchronized String addPlayer(ClientHandler handler) {
        String playerName = "Player_" + playerCounter.getAndIncrement();
        playerNames.add(playerName);
        clients.add(handler);
        playerReady.put(playerName, false);

        // Register this player/handler in the game manager
        gameManager.registerPlayer(playerName, handler);

        return playerName;
    }

    public synchronized void removePlayer(ClientHandler handler, String playerName) {
        clients.remove(handler);
        playerNames.remove(playerName);
        playerReady.remove(playerName);

        gameManager.removePlayer(playerName); // Remove from game manager
        broadcastLobbyUpdate();
    }

    public void sendJoinSuccessAndUpdate(ClientHandler handler, String playerName) {
        // 1. Send JOIN_SUCCESS
        Message successMsg = new Message("JOIN_SUCCESS", playerName);
        handler.send(new Gson().toJson(successMsg));

        // 2. Wait for client to be ready
        try { Thread.sleep(200); } catch (InterruptedException ignored) {}

        // 3. Broadcast lobby update to all
        broadcastLobbyUpdate();
    }

    public void broadcastLobbyUpdate() {
        Message lobbyUpdate = new Message("LOBBY_UPDATE", new ArrayList<>(playerNames));
        String lobbyUpdateJson = new Gson().toJson(lobbyUpdate);
        for (ClientHandler client : clients) {
            client.send(lobbyUpdateJson);
        }
    }

    public List<String> getAllPlayerNames() {
        return new ArrayList<>(playerNames);
    }

    public List<ClientHandler> getAllHandlers() {
        return new ArrayList<>(clients);
    }

    public ClientHandler getHandlerByPlayerName(String playerName) {
        int idx = playerNames.indexOf(playerName);
        if (idx >= 0 && idx < clients.size()) return clients.get(idx);
        return null;
    }

    public synchronized void setPlayerReady(String playerName, boolean ready) {
        playerReady.put(playerName, ready);
        broadcastLobbyStatus();

        // Only start countdown if both are ready and 2 players
        if (getReadyCount() == 2 && playerNames.size() == 2 && !countdownActive) {
            startCountdown();
        } else if (!ready) {
            cancelCountdown();
        }
    }

    private int getReadyCount() {
        int count = 0;
        for (String name : playerNames)
            if (playerReady.getOrDefault(name, false)) count++;
        return count;
    }

    // Send player ready status to all clients
    public void broadcastLobbyStatus() {
        List<Map<String, Object>> statuses = new ArrayList<>();
        for (String name : playerNames) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("ready", playerReady.getOrDefault(name, false));
            statuses.add(map);
        }
        Message msg = new Message("LOBBY_READY_STATUS", statuses);
        String json = new Gson().toJson(msg);
        for (ClientHandler client : clients) client.send(json);
    }

    // Countdown logic
    private void startCountdown() {
        countdownActive = true;
        new Thread(() -> {
            try {
                for (int i = 3; i > 0; i--) {
                    Message countdownMsg = new Message("COUNTDOWN_START", i);
                    String json = new Gson().toJson(countdownMsg);
                    for (ClientHandler client : clients) client.send(json);
                    Thread.sleep(1000);
                    // Abort if someone unreadies
                    if (getReadyCount() < 2 || playerNames.size() < 2) {
                        cancelCountdown();
                        return;
                    }
                }
                // Start game after countdown
                Message startMsg = new Message("GAME_START", null);
                String json = new Gson().toJson(startMsg);
                for (ClientHandler client : clients) client.send(json);

                // ACTUALLY start game logic
                gameManager.startGame(getAllPlayerNames());
                gameManager.sendInitialHandsToPlayers();
                Thread.sleep(1000);
                gameManager.sendTurnUpdate();

            } catch (InterruptedException ignored) {}
            countdownActive = false;
        }).start();
    }

    private void cancelCountdown() {
        if (!countdownActive) return;
        countdownActive = false;
        Message cancelMsg = new Message("COUNTDOWN_CANCEL", null);
        String json = new Gson().toJson(cancelMsg);
        for (ClientHandler client : clients) client.send(json);
    }

    // Main message router
    public void handleMessage(ClientHandler handler, Message message, String playerName) {
        switch (message.getType()) {
            case "PLAYER_READY":
                setPlayerReady(playerName, true);
                break;
            case "PLAYER_NOT_READY":
                setPlayerReady(playerName, false);
                break;
            // GAME_START is handled automatically after countdown!
            case "PLAY_CARD":
                if (!gameManager.isPlayerTurn(playerName)) {
                    // Optionally notify the player it's not their turn
                    handler.send(new Gson().toJson(
                            new Message("ERROR", "Not your turn!")
                    ));
                    return;
                }
                gameManager.playCard(playerName, message.getPayload());
                break;
            case "POKER_HAND_PREVIEW_REQUEST":
                if (!gameManager.isPlayerTurn(playerName)) {
                    // Optionally notify the player it's not their turn
                    handler.send(new Gson().toJson(
                            new Message("ERROR", "Not your turn!")
                    ));
                    return;
                }
                gameManager.handlePokerHandPreviewRequest(playerName, message.getPayload());
                break;
            case "DISCARD":
                if (!gameManager.isPlayerTurn(playerName)) {
                    // Optionally notify the player it's not their turn
                    handler.send(new Gson().toJson(
                            new Message("ERROR", "Not your turn!")
                    ));
                    return;
                }
                gameManager.discard(playerName, message.getPayload());
                break;
            // Add more cases as you add more message types!
        }
    }
}
