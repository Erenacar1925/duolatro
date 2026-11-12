package client.model;

public class Boss {
  private final int targetScore;

  public Boss(int targetScore) {
    this.targetScore = targetScore;
  }

  public int getTargetScore() {
    return targetScore;
  }

  public boolean isWin(Player player) {
    return player.getScore() >= targetScore;
  }

  public boolean isFail(Player player) {
    return false;
    // return player.playHand();
  }
}
