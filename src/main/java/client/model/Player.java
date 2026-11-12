package client.model;

import server.PokerHand;

import java.util.ArrayList;
import java.util.List;

public class Player {
  private String id;
  private String name;
  private List<Card> hand;
  private int cash;
  private int remainingHands;
  private int remainingDiscards;
  private int score;
  private int lastActionCardCount = 0;

  public Player() {
    this.hand = new ArrayList<>();
    this.cash = 4;
    this.score = 0;
    this.remainingHands = 4;
    this.remainingDiscards = 4;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public int getScore() {
    return score;
  }

  public List<Card> getHand() {
    return hand;
  }

  public int getCash() {
    return cash;
  }

  public void setCash(int cash) {
    this.cash = cash;
  }

  public int getRemainingHands() {
    return remainingHands;
  }

  public void setRemainingHands(int remainingHands) {
    this.remainingHands = remainingHands;
  }

  public int getRemainingDiscards() {
    return remainingDiscards;
  }

  public void setRemainingDiscards(int remainingDiscards) {
    this.remainingDiscards = remainingDiscards;
  }

  public int getLastActionCardCount() {
    return lastActionCardCount;
  }

  public void setLastActionCardCount(int count) {
    this.lastActionCardCount = count;
  }

  public void addCardToHand(Card card) {
    hand.add(card);
  }

  public boolean discardCard(Card card) {
    if (remainingDiscards > 0 && hand.contains(card)) {
      hand.remove(card);
      remainingDiscards--;
      lastActionCardCount = 1;
      return true;
    }
    return false;
  }

  /**
   * Plays selected cards and calculates the score and card values. Removes the played cards from
   * the player's hand and decrements the remaining hands counter.
   *
   * @param selectedCards The list of cards to play (must be 5 or fewer cards)
   * @return The total points earned from playing this hand, or 0 if the hand cannot be played
   * @param selectedCards
   * @return
   */
  public int playHand(List<Card> selectedCards) {
    if (remainingHands > 0 && selectedCards != null && selectedCards.size() <= 5) {
      if (!hand.containsAll(selectedCards)) {
        throw new IllegalArgumentException("Failed to play hand: Invalid selected cards");
      }

      remainingHands--;
      lastActionCardCount = selectedCards.size();

      PokerHand.PokerHandType handType = PokerHand.evaluate(selectedCards);
      int basePoints = PokerHand.getPointsForHandType(handType);

      int cardPoints = 0;
      for (Card card : selectedCards) {
        cardPoints += card.getChipValue();
      }

      int totalPoints = basePoints + cardPoints;
      score += totalPoints;

      for (Card card : selectedCards) {
        hand.remove(card);
      }

      return totalPoints;
    }

    return 0;
  }

  public void resetForNewRound(int hands, int discards) {
    this.remainingHands = hands;
    this.remainingDiscards = discards;
  }

  public void dealInitialHand(Deck deck) {
    for (int i = 0; i < 8; i++) {
      Card card = deck.draw();
      if (card != null) {
        hand.add(card);
      }
    }
  }
}
