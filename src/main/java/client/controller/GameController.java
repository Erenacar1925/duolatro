package client.controller;

import client.NetworkClient;
import client.ScreenManager;
import client.model.Card;
import client.model.Message;
import client.view.GameScreen;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class GameController {
    private final NetworkClient networkClient;
    private final ScreenManager screenManager;
    private final GameScreen gameScreen;
    private int previousHandSize = 0;  // Add at the top

    private List<Card> myHand = new ArrayList<>();
    private List<String> myHandCodes = new ArrayList<>();
    private List<String> opponentHandCodes = new ArrayList<>();

    public GameController(NetworkClient networkClient, ScreenManager screenManager, GameScreen gameScreen) {
        this.networkClient = networkClient;
        this.screenManager = screenManager;
        this.gameScreen = gameScreen;


        gameScreen.setOnInfoButton(e -> onInfo());
        gameScreen.setOnOptionsButton(e -> onOptions());
        gameScreen.setController(this);

        networkClient.setMessageListener(this::onServerMessage);
    }

    private void onInfo() { /* ... */ }
    private void onOptions() { /* ... */ }
    public void onCardSelected(String cardCode) {
        sendPokerHandPreviewRequest();
    }

    public void onCardDeselected(String cardCode) {
        sendPokerHandPreviewRequest();
    }

    private void sendPokerHandPreviewRequest() {
        List<String> selectedCodes = gameScreen.getSelectedPlayer1CardCodes();
        Message previewMsg = new Message("POKER_HAND_PREVIEW_REQUEST", selectedCodes);
        networkClient.sendMessage(new Gson().toJson(previewMsg));
    }
    public void onPlayHand() {
        List<String> selectedCodes = gameScreen.getSelectedPlayer1CardCodes();

        if (!selectedCodes.isEmpty()) {
            gameScreen.playSelectedCardsWithDrawAnimation(true, selectedCodes.size(), () -> {
                // 2. After animation completes, send the play to the server
                Message playMsg = new Message("PLAY_CARD", selectedCodes);
                networkClient.sendMessage(new Gson().toJson(playMsg));
                // Optionally: clear selection, update UI, etc.
            });
        }
    }
    public void onDiscard() {
        List<String> selectedCodes = gameScreen.getSelectedPlayer1CardCodes();
        if (!selectedCodes.isEmpty()) {
            Message discardMsg = new Message("DISCARD", selectedCodes);
            networkClient.sendMessage(new Gson().toJson(discardMsg));
        }
    }    public void onTurnFinished() { /* ... */ }

    // All card deals come from the server:
    public List<String> requestNewCards(int numberOfCards) {
        // Not used, client doesn't generate cards
        return new ArrayList<>();
    }

    private void onServerMessage(String msg) {
        Gson gson = new Gson();
        Message message = gson.fromJson(msg, Message.class);

        switch (message.getType()) {
            case "YOUR_HAND":
            case "GAME_START": {
                List<Map<String, Object>> handList = gson.fromJson(
                        gson.toJson(message.getPayload()),
                        new TypeToken<List<Map<String, Object>>>(){}.getType()
                );
                myHand = handList.stream().map(cardMap -> {
                    String code = (String) cardMap.get("code");
                    boolean discarded = cardMap.get("discarded") != null && (boolean) cardMap.get("discarded");
                    Card card = Card.fromCardCode(code);
                    card.setDiscarded(discarded);
                    return card;
                }).collect(Collectors.toList());
                javafx.application.Platform.runLater(() -> {
                    gameScreen.showPlayerHand(1, myHand);
                    gameScreen.highlightPendingCards();
                });
                break;
            }
            case "OPPONENT_HAND": {
                List<Map<String, Object>> handList = gson.fromJson(
                        gson.toJson(message.getPayload()),
                        new TypeToken<List<Map<String, Object>>>(){}.getType()
                );
                List<Card> oppHand = handList.stream().map(cardMap -> {
                    String code = (String) cardMap.get("code");
                    boolean discarded = cardMap.get("discarded") != null && (boolean) cardMap.get("discarded");
                    Card card = Card.fromCardCode(code);
                    card.setDiscarded(discarded);
                    return card;
                }).collect(Collectors.toList());
                javafx.application.Platform.runLater(() -> gameScreen.showPlayerHand(2, oppHand));
                break;
            }
            case "UPDATE_HAND": {
                List<String> updatedCodes = gson.fromJson(
                        gson.toJson(message.getPayload()),
                        new TypeToken<List<String>>(){}.getType()
                );
                int oldSize = myHand.size();
                int newSize = updatedCodes.size();
                myHand = updatedCodes.stream().map(Card::fromCardCode).collect(Collectors.toList());
                javafx.application.Platform.runLater(() -> {
                    gameScreen.showPlayerHand(1, myHand);

                    int drawn = newSize - oldSize;
                    if (drawn > 0) {
                        // Animate only for the number of new cards drawn
                        gameScreen.animateDrawCards(true, drawn); // Implement this method
                    }
                });
                previousHandSize = newSize;

                break;
            }
            case "TEAM_SCORE_UPDATE": {
                Map<String, Object> payload = (Map<String, Object>) message.getPayload();
                // You may need to cast to double and convert to int, depending on Gson's deserialization
                int teamScore = ((Number) payload.get("teamScore")).intValue();
                String handType = (String) payload.get("handType");
                int lastPlayScore = ((Number) payload.get("score")).intValue();
                String lastPlayer = (String) payload.get("lastPlayer");

                // Display the teamScore in your UI!
                javafx.application.Platform.runLater(() -> {
                    gameScreen.setCurrentScore(String.valueOf(teamScore));  // Assuming you have this method
                    // You can also show lastPlayScore, handType, etc. if you want
                });
                break;
            }
            case "POKER_HAND_PREVIEW": {
                Map<String, Object> payload = (Map<String, Object>) message.getPayload();
                String handType = (String) payload.get("handType");
                int baseScore = ((Number) payload.get("baseScore")).intValue();
                int multiplier = ((Number) payload.get("multiplier")).intValue();
                int score = ((Number) payload.get("score")).intValue();

                javafx.application.Platform.runLater(() -> {
                    gameScreen.setPokerHand(handType != null ? handType : "");
                    gameScreen.setPokerHandBaseScore(String.valueOf(baseScore));
                    gameScreen.setPokerHandMultiplier("x" + multiplier);
                    gameScreen.setPokerHandLevel(String.valueOf(score)); // or use a dedicated label
                });
                break;
            }
            case "HANDS_DISCARDS_UPDATE": {
                Map<String, Object> payload = (Map<String, Object>) message.getPayload();
                int handsLeft = ((Number) payload.get("handsLeft")).intValue();
                int discardsLeft = ((Number) payload.get("discardsLeft")).intValue();
                javafx.application.Platform.runLater(() -> {
                    gameScreen.setHands(String.valueOf(handsLeft));
                    gameScreen.setDiscardsCounter(String.valueOf(discardsLeft));
                    // Optionally disable buttons
                    gameScreen.setDiscardButtonDisabled(discardsLeft <= 0);
                    // If you want, also disable play button if handsLeft <= 0 (add a method in GameScreen)
                });
                break;
            }
            case "TURN_UPDATE": {
                Map<String, Object> payload = (Map<String, Object>) message.getPayload();
                boolean yourTurn = (Boolean) payload.get("yourTurn");
                String currentPlayer = (String) payload.get("playerName");

                javafx.application.Platform.runLater(() -> {
                    gameScreen.setInputEnabled(yourTurn);
                    if (!yourTurn) {
                        gameScreen.showWaitingMessage("Waiting for " + currentPlayer + "...");
                    } else {
                        gameScreen.hideWaitingMessage();
                    }
                });
                break;
            }
            case "GAME_OVER": {
                Map<String, Object> payload = (Map<String, Object>) message.getPayload();
                boolean won = "VICTORY".equals(payload.get("result"));
                int finalScore = ((Number) payload.get("finalScore")).intValue();
                int targetScore = ((Number) payload.getOrDefault("targetScore", 500)).intValue();
                int bank = ((Number) payload.getOrDefault("bank", 0)).intValue();
                int winAmount = ((Number) payload.getOrDefault("winAmount", 0)).intValue();

                javafx.application.Platform.runLater(() -> {
                    screenManager.showResultScreen(won, targetScore, finalScore, bank, winAmount);
                });
                break;
            }
            case "PENDING_DISCARDS": {
                List<String> pendingCardCodes = (List<String>) message.getPayload();
                javafx.application.Platform.runLater(() -> {
                    gameScreen.setPendingDiscards(pendingCardCodes);
                    gameScreen.showWaitingMessage("You have pending cards to discard!");
                });
                break;
            }
        }
    }
}
