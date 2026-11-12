package client.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
  private List<Player> players;
  private Deck deck;
  private List<Boss> bosses;
  private int currentScore;
  private int round;
  private int level;
  private int currentPlayerIndex = 0;
  public int actionsThisRound = 0;

  public Game() {
    this.players = new ArrayList<>();
    this.deck = new Deck();
    this.bosses = new ArrayList<>();
    bosses.add(new Boss(500)); // Level 1
    bosses.add(new Boss(1000)); // Level 2
    this.currentScore = 0;
    this.round = 1;
    this.level = 1;
  }

  /** Initial game: add 2 players, shuffle, give handcards */
  public void startGame() {
    // add players
    players.clear();
    players.add(new Player());
    players.add(new Player());

    // shuffle
    deck.shuffle();

    // handcards
    for (Player player : players) {
      player.dealInitialHand(deck);
      player.resetForNewRound(4, 4); // each round 4 hands 4 discards
    }

    currentScore = 0;
    round = 1;
    currentPlayerIndex = 0;
  }

  /** count score */
  public int calculateScore() {
    int total = 0;
    for (Player player : players) {
      total += player.getScore();
    }
    this.currentScore = total;
    return total;
  }

  /** is boss defeated */
  public boolean isBossDefeated() {
    return currentScore >= getBoss().getTargetScore();
  }

  /** is round over */
  public boolean isRoundOver() {
    return actionsThisRound >= players.size() || deck.isEmpty() || isBossDefeated();
  }

  /**
   * Checks if all players have used all their remaining hands.
   *
   * @return true if all players have 0 remaining hands, false otherwise
   */
  public boolean isGameOver() {
    for (Player player : players) {
      if (player.getRemainingHands() > 0) {
        return false;
      }
    }
    return true;
  }

  /** new round */
  public void nextRound() {
    round++;
    for (Player player : players) {
      int toDraw = player.getLastActionCardCount();
      for (int i = 0; i < toDraw && !deck.isEmpty(); i++) {
        Card card = deck.draw();
        if (card != null) {
          player.addCardToHand(card);
        }
      }
      player.resetForNewRound(4, 4);
      player.setLastActionCardCount(0); // reset and wait for next round
    }
    currentPlayerIndex = 0;
    actionsThisRound = 0;
  }

  /** get current player */
  public Player getCurrentPlayer() {
    return players.get(currentPlayerIndex);
  }

  /** switch to next player */
  public void nextPlayer() {
    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
  }

  public void nextLevel() {
    level++;
    startGame();
  }

  public List<Player> getPlayers() {
    return players;
  }

  public Deck getDeck() {
    return deck;
  }

  public Boss getBoss() {
    return bosses.get(level - 1);
  }

  public int getCurrentScore() {
    return currentScore;
  }

  public void setCurrentScore(int currentScore) {
    this.currentScore = currentScore;
  }

  public int getRound() {
    return round;
  }

  public int getLevel() {
    return level;
  }

  public int getActionsThisRound() {
    return actionsThisRound;
  }

  public void setActionsThisRound(int actionsThisRound) {
    this.actionsThisRound = actionsThisRound;
  }
}
