package client.model;

import java.util.ArrayList;
import java.util.List;

/** Represents the current state of the Duolatro's game at any moment. */
public class GameState {
  private List<PlayerState> players;
  private int currentScore;
  private int round;
  private int level;
  private int currentPlayerIndex;
  private int actionsThisRound;
  private BossState boss;
  private DeckState deck;
  private boolean gameOver;
  private boolean bossDefeated;

  /** Creates and initializes a new GameState. */
  public GameState() {
    this.players = new ArrayList<>();
    this.currentScore = 0;
    this.round = 1;
    this.level = 1;
    this.currentPlayerIndex = 0;
    this.actionsThisRound = 0;
    this.boss = new BossState(500);
    this.deck = new DeckState();
    this.gameOver = false;
    this.bossDefeated = false;
  }

  /**
   * Creates a GameState from a Game object.
   *
   * @param game The game to create state from
   */
  public GameState(Game game) {
    this();
    updateFromGame(game);
  }

  // getters, setters are not needed for state representation
  public List<PlayerState> getPlayers() {
    return players;
  }

  public int getCurrentScore() {
    return currentScore;
  }

  public int getRound() {
    return round;
  }

  public int getLevel() {
    return level;
  }

  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  public int getActionsThisRound() {
    return actionsThisRound;
  }

  public BossState getBoss() {
    return boss;
  }

  public DeckState getDeck() {
    return deck;
  }

  public boolean isGameOver() {
    return gameOver;
  }

  public boolean isBossDefeated() {
    return bossDefeated;
  }

  /**
   * Gets the current player using the current player index.
   *
   * @return The current player, or null if there are no players
   */
  public PlayerState getCurrentPlayer() {
    if (players.isEmpty() || currentPlayerIndex < 0 || currentPlayerIndex >= players.size()) {
      return null;
    }
    return players.get(currentPlayerIndex);
  }

  /**
   * Checks if the round is over based on current game state.
   *
   * @return true if the round is over, false otherwise
   */
  public boolean isRoundOver() {
    return actionsThisRound >= players.size() || deck.isEmpty() || bossDefeated;
  }

  /**
   * Updates the game state from the given Game object.
   *
   * @param game The game to sync state from
   */
  public void updateFromGame(Game game) {
    this.currentScore = game.getCurrentScore();
    this.round = game.getRound();
    this.level = game.getLevel();
    this.actionsThisRound = game.getActionsThisRound();
    this.boss = new BossState(game.getBoss().getTargetScore());
    this.deck = new DeckState(game.getDeck().size());
    this.gameOver = game.isGameOver();
    this.bossDefeated = game.isBossDefeated();

    // Update the players
    this.players.clear();
    for (Player player : game.getPlayers()) {
      this.players.add(new PlayerState(player));
    }

    this.currentPlayerIndex = game.getPlayers().indexOf(game.getCurrentPlayer());
  }

  /** Represents the state of a player. */
  public static class PlayerState {
    private String id;
    private String name;
    private List<Card> hand;
    private int cash;
    private int remainingHands;
    private int remainingDiscards;
    private int score;
    private int lastActionCardCount;

    public PlayerState(Player player) {
      this.id = player.getId();
      this.name = player.getName();
      this.hand = new ArrayList<>(player.getHand());
      this.cash = player.getCash();
      this.remainingHands = player.getRemainingHands();
      this.remainingDiscards = player.getRemainingDiscards();
      this.score = player.getScore();
      this.lastActionCardCount = player.getLastActionCardCount();
    }

    // getters, setters are not needed for state representation
    public String getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public List<Card> getHand() {
      return hand;
    }

    public int getCash() {
      return cash;
    }

    public int getRemainingHands() {
      return remainingHands;
    }

    public int getRemainingDiscards() {
      return remainingDiscards;
    }

    public int getScore() {
      return score;
    }

    public int getLastActionCardCount() {
      return lastActionCardCount;
    }
  }

  /** Represents the state of the boss. */
  public static class BossState {
    private int targetScore;

    public BossState(int targetScore) {
      this.targetScore = targetScore;
    }

    public int getTargetScore() {
      return targetScore;
    }
  }

  /** Represents the state of the deck. */
  public static class DeckState {
    private int remainingCards;

    public DeckState() {
      this.remainingCards = 104;
    }

    public DeckState(int remainingCards) {
      this.remainingCards = remainingCards;
    }

    public int getRemainingCards() {
      return remainingCards;
    }

    public boolean isEmpty() {
      return remainingCards <= 0;
    }
  }
}
