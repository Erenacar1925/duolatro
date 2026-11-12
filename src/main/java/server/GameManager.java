package server;

import client.model.Card;
import client.model.Deck;
import client.model.Message;
import com.google.gson.Gson;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GameManager {
    private final Map<String, List<Card>> hands = new ConcurrentHashMap<>(); // playerName -> hand
    private final Map<String, ClientHandler> playerHandlers = new ConcurrentHashMap<>();
    private Deck deck;
    private int teamScore = 0;
    private final int WINNING_SCORE = 500;
    private int handsLeft = 5;
    private int discardsLeft = 5;
    private List<String> playerOrder = new ArrayList<>();
    private int currentPlayerTurnIndex = 0;
    private final Map<String, List<Card>> pendingDiscards = new ConcurrentHashMap<>();

    // Call from LobbyManager at game start
    public void startGame(List<String> playerNames) {
        deck = new Deck();
        deck.shuffle();
        teamScore = 0;
        handsLeft = 5;
        discardsLeft = 5;
        hands.clear();

        playerOrder.clear();
        playerOrder.addAll(playerNames);
        currentPlayerTurnIndex = 0;

        for (String playerName : playerNames) {
            List<Card> hand = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                hand.add(deck.draw());
            }
            hands.put(playerName, hand);
        }
    }
    public boolean isPlayerTurn(String playerName) {
        return playerName.equals(playerOrder.get(currentPlayerTurnIndex));
    }

    public void sendTurnUpdate() {
        String currentPlayer = playerOrder.get(currentPlayerTurnIndex);
        for (Map.Entry<String, ClientHandler> entry : playerHandlers.entrySet()) {
            String playerName = entry.getKey();
            ClientHandler handler = entry.getValue();
            boolean isYourTurn = playerName.equals(currentPlayer);

            Map<String, Object> payload = new HashMap<>();
            payload.put("yourTurn", isYourTurn);
            payload.put("playerName", currentPlayer);

            Message msg = new Message("TURN_UPDATE", payload);
            handler.send(new Gson().toJson(msg));
        }
    }
    public void handlePokerHandPreviewRequest(String playerName, Object payload) {
        if (payload instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> codes = (List<String>) payload;
            List<Card> cards = codes.stream().map(Card::fromCardCode).collect(Collectors.toList());

            PokerHand.PokerHandType handType = PokerHand.evaluate(cards);
            int base = PokerHand.baseScore(handType);
            int mult = PokerHand.multiplier(handType);
            int score = PokerHand.getPointsForHandType(handType);

            Map<String, Object> previewPayload = new HashMap<>();
            previewPayload.put("handType", handType.toString());
            previewPayload.put("baseScore", base);
            previewPayload.put("multiplier", mult);
            previewPayload.put("score", score);

            ClientHandler handler = playerHandlers.get(playerName);
            if (handler != null) {
                Message previewMsg = new Message("POKER_HAND_PREVIEW", previewPayload);
                handler.send(new Gson().toJson(previewMsg));
            }
        }
    }

    public void sendInitialHandsToPlayers() {
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}

        for (Map.Entry<String, List<Card>> entry : hands.entrySet()) {
            String playerName = entry.getKey();
            List<Card> hand = entry.getValue();
            ClientHandler handler = playerHandlers.get(playerName);

            if (handler != null) {
                // Send player's own hand
                Message handMsg = new Message(
                        "YOUR_HAND",
                        hand.stream().map(card -> {
                            Map<String, Object> m = new HashMap<>();
                            m.put("code", card.toCardCode());
                            m.put("discarded", card.isDiscarded());
                            return m;
                        }).collect(Collectors.toList())
                );
                handler.send(new Gson().toJson(handMsg));

                // Send opponent's hand
                String opponentName = hands.keySet().stream()
                        .filter(name -> !name.equals(playerName))
                        .findFirst().orElse(null);

                if (opponentName != null) {
                    List<Card> opponentHand = hands.get(opponentName);
                    Message oppMsg = new Message(
                            "OPPONENT_HAND",
                            opponentHand.stream().map(card -> {
                                Map<String, Object> m = new HashMap<>();
                                m.put("code", card.toCardCode());
                                m.put("discarded", card.isDiscarded());
                                return m;
                            }).collect(Collectors.toList())
                    );
                    handler.send(new Gson().toJson(oppMsg));
                }
            }
        }
    }


    public void registerPlayer(String playerName, ClientHandler handler) {
        playerHandlers.put(playerName, handler);
    }

    public void removePlayer(String playerName) {
        playerHandlers.remove(playerName);
        hands.remove(playerName); // Optional: also remove their hand if present
    }

    // Register handlers at start (call from LobbyManager after addPlayer or on game start)
    public void registerHandler(String playerName, ClientHandler handler) {
        playerHandlers.put(playerName, handler);
    }

    // Helper for LobbyManager to find handler by player name
    public ClientHandler getHandlerByPlayerName(String playerName) {
        return playerHandlers.get(playerName);
    }

    public void playCard(String playerName, Object payload) {
        if (payload instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> playedCardCodes = (List<String>) payload;
            List<Card> hand = hands.get(playerName);

            if (hand != null) {
                // Remove played cards from hand
                List<Card> playedCards = new ArrayList<>();
                for (String code : playedCardCodes) {
                    for (Card card : hand) {
                        if (card.toCardCode().equals(code)) {
                            playedCards.add(card);
                            break;
                        }
                    }
                }
                hand.removeAll(playedCards);

                if (handsLeft <= 0) return; // Already no hands left

                handsLeft--;
                // Draw same number of new cards
                for (int i = 0; i < playedCards.size(); i++) {
                    if (!deck.isEmpty()) {
                        hand.add(deck.draw());
                    }
                }

                // (Your scoring logic here as before)
                PokerHand.PokerHandType handType = PokerHand.evaluate(playedCards);
                int score = PokerHand.getPointsForHandType(handType);
                teamScore += score;

                // Broadcast team score update to all players
                Map<String, Object> teamScorePayload = new HashMap<>();
                teamScorePayload.put("lastPlayer", playerName);
                teamScorePayload.put("handType", handType.toString());
                teamScorePayload.put("score", score);
                teamScorePayload.put("teamScore", teamScore);

                Message teamScoreMsg = new Message("TEAM_SCORE_UPDATE", teamScorePayload);

                Gson gson = new Gson();
                String json = gson.toJson(teamScoreMsg);

                for (ClientHandler h : playerHandlers.values()) {
                    h.send(json);
                }

                // Update hands visually for both players
                updateBothPlayersHands();
                broadcastHandsAndDiscardsLeft();
                currentPlayerTurnIndex = (currentPlayerTurnIndex + 1) % playerOrder.size();
                sendTurnUpdate();

                if (handsLeft == 0 && teamScore < WINNING_SCORE) {
                    // Game lost
                    Message loseMsg = new Message("GAME_OVER", Map.of(
                            "result", "DEFEAT",
                            "finalScore", teamScore,
                            "targetScore", WINNING_SCORE
                    ));
                    String loseJson = new Gson().toJson(loseMsg);
                    for (ClientHandler h : playerHandlers.values()) {
                        h.send(loseJson);
                    }
                }
                // Check win
                if (teamScore >= WINNING_SCORE) {
                    Message winMsg = new Message("GAME_OVER", Map.of(
                            "result", "VICTORY",
                            "finalScore", teamScore,
                            "targetScore", WINNING_SCORE
                    ));
                    String winJson = gson.toJson(winMsg);
                    for (ClientHandler h : playerHandlers.values()) {
                        h.send(winJson);
                    }
                }
            }
        }
    }
    private void broadcastHandsAndDiscardsLeft() {
        Message handsDiscardsMsg = new Message("HANDS_DISCARDS_UPDATE", Map.of(
                "handsLeft", handsLeft,
                "discardsLeft", discardsLeft
        ));
        String json = new Gson().toJson(handsDiscardsMsg);
        for (ClientHandler h : playerHandlers.values()) {
            h.send(json);
        }
    }
    public void discard(String playerName, Object payload) {
        if (discardsLeft <= 0) return;

        discardsLeft--;

        List<String> discardCodes = (List<String>) payload;
        List<Card> hand = hands.get(playerName);

        // Find the other player
        String otherPlayer = playerOrder.stream()
                .filter(name -> !name.equals(playerName)).findFirst().orElse(null);

        if (hand != null && otherPlayer != null) {
            List<Card> pendingForThisPlayer = pendingDiscards.getOrDefault(playerName, new ArrayList<>());
            List<Card> otherHand = hands.get(otherPlayer);

            // 1. Mark selected cards as discarded in the player's hand
            List<Card> selectedCards = new ArrayList<>();
            for (Card card : hand) {
                if (discardCodes.contains(card.toCardCode()) && !card.isDiscarded()) {
                    card.setDiscarded(true);
                    selectedCards.add(card);
                }
            }

            // 2. Find intersection by *card code* where both cards are flagged as discarded in both hands
            Set<String> selectedCodes = selectedCards.stream().map(Card::toCardCode).collect(Collectors.toSet());
            Set<String> pendingCodes = pendingForThisPlayer.stream()
                    .filter(Card::isDiscarded)
                    .map(Card::toCardCode)
                    .collect(Collectors.toSet());

            Set<String> intersection = new HashSet<>(selectedCodes);
            intersection.retainAll(pendingCodes);

            // 3. Remove intersection cards (flagged as discarded) from both hands
            List<Card> toRemove = hand.stream()
                    .filter(c -> intersection.contains(c.toCardCode()) && c.isDiscarded())
                    .collect(Collectors.toList());
            hand.removeAll(toRemove);

            List<Card> toRemoveOther = otherHand.stream()
                    .filter(c -> intersection.contains(c.toCardCode()) && c.isDiscarded())
                    .collect(Collectors.toList());
            otherHand.removeAll(toRemoveOther);

            // Draw new cards for both for intersection
            for (int i = 0; i < toRemove.size(); i++) {
                if (!deck.isEmpty()) hand.add(deck.draw());
            }
            for (int i = 0; i < toRemoveOther.size(); i++) {
                if (!deck.isEmpty()) otherHand.add(deck.draw());
            }

            // 4. Difference: cards only this player selected (should be sent to the other player)
            Set<String> difference = new HashSet<>(selectedCodes);
            difference.removeAll(pendingCodes);

            List<Card> toSend = hand.stream()
                    .filter(c -> difference.contains(c.toCardCode()) && c.isDiscarded())
                    .collect(Collectors.toList());

            // Remove the difference cards from the discarding player's hand
            hand.removeAll(toSend);

            // Add them to the other player's hand and mark as pending for that player
            otherHand.addAll(toSend);

            // Append these cards to the other player's pending list
            List<Card> previousPending = pendingDiscards.getOrDefault(otherPlayer, new ArrayList<>());
            previousPending.addAll(toSend);
            pendingDiscards.put(otherPlayer, previousPending);

            // Remove all pending from this player (they're all resolved)
            pendingDiscards.put(playerName, new ArrayList<>());

            // Notify other player if cards are pending for discard
            if (!toSend.isEmpty()) {
                ClientHandler handler = playerHandlers.get(otherPlayer);
                if (handler != null) {
                    List<String> codes = toSend.stream().map(Card::toCardCode).collect(Collectors.toList());
                    Message pendingMsg = new Message("PENDING_DISCARDS", codes);
                    handler.send(new Gson().toJson(pendingMsg));
                }
            }

            updateBothPlayersHands();
            currentPlayerTurnIndex = (currentPlayerTurnIndex + 1) % playerOrder.size();
            sendTurnUpdate();
            broadcastHandsAndDiscardsLeft();
        }
    }




    // Utility: Sends YOUR_HAND and OPPONENT_HAND to both players
    private void updateBothPlayersHands() {
        if (hands.size() != 2) return;
        Iterator<Map.Entry<String, List<Card>>> it = hands.entrySet().iterator();
        Map.Entry<String, List<Card>> entry1 = it.next();
        Map.Entry<String, List<Card>> entry2 = it.next();

        String player1 = entry1.getKey();
        String player2 = entry2.getKey();
        List<Card> hand1 = entry1.getValue();
        List<Card> hand2 = entry2.getValue();

        // >>>> DRAW UP TO 8 <<<<
        while (hand1.size() < 8 && !deck.isEmpty()) hand1.add(deck.draw());
        while (hand2.size() < 8 && !deck.isEmpty()) hand2.add(deck.draw());

        ClientHandler handler1 = playerHandlers.get(player1);
        ClientHandler handler2 = playerHandlers.get(player2);

        Gson gson = new Gson();

        // Helper: Convert List<Card> to List<Map<String,Object>>
        List<Map<String, Object>> hand1List = hand1.stream().map(card -> {
            Map<String, Object> m = new HashMap<>();
            m.put("code", card.toCardCode());
            m.put("discarded", card.isDiscarded());
            return m;
        }).collect(Collectors.toList());

        List<Map<String, Object>> hand2List = hand2.stream().map(card -> {
            Map<String, Object> m = new HashMap<>();
            m.put("code", card.toCardCode());
            m.put("discarded", card.isDiscarded());
            return m;
        }).collect(Collectors.toList());

        // Send both code and discarded flag
        if (handler1 != null) {
            handler1.send(gson.toJson(new Message("YOUR_HAND", hand1List)));
            handler1.send(gson.toJson(new Message("OPPONENT_HAND", hand2List)));
        }
        if (handler2 != null) {
            handler2.send(gson.toJson(new Message("YOUR_HAND", hand2List)));
            handler2.send(gson.toJson(new Message("OPPONENT_HAND", hand1List)));
        }
    }
}
