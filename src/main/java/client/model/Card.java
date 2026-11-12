package client.model;

public class Card {

  private final Suit suit;
  private final Rank rank;
  private boolean discarded = false;  // New field

  public Card(Suit suit, Rank rank) {
    this.suit = suit;
    this.rank = rank;
  }
  public boolean isDiscarded() { return discarded; }
  public void setDiscarded(boolean discarded) { this.discarded = discarded; }
  public enum Suit {
    HEARTS,
    DIAMONDS,
    CLUBS,
    SPADES
  }

  public enum Rank {
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    JACK(10),
    QUEEN(10),
    KING(10),
    ACE(11);

    private final int chipValue;

    Rank(int chipValue) {
      this.chipValue = chipValue;
    }

    public int getChipValue() {
      return chipValue;
    }
  }

  public Suit getSuit() {
    return suit;
  }

  public Rank getRank() {
    return rank;
  }

  public int getChipValue() {
    return rank.getChipValue();
  }

  @Override
  public String toString() {
    return rank + " of " + suit;
  }

  /**
   * Converts the card to a card code string used by the GUI. Examples: "AS" for Ace of Spades, "KH"
   * for King of Hearts, "10D" for Ten of Diamonds
   *
   * @return the card code string
   */
  public String toCardCode() {
    String rankCode;
    switch (rank) {
      case TWO:
        rankCode = "2";
        break;
      case THREE:
        rankCode = "3";
        break;
      case FOUR:
        rankCode = "4";
        break;
      case FIVE:
        rankCode = "5";
        break;
      case SIX:
        rankCode = "6";
        break;
      case SEVEN:
        rankCode = "7";
        break;
      case EIGHT:
        rankCode = "8";
        break;
      case NINE:
        rankCode = "9";
        break;
      case TEN:
        rankCode = "10";
        break;
      case JACK:
        rankCode = "J";
        break;
      case QUEEN:
        rankCode = "Q";
        break;
      case KING:
        rankCode = "K";
        break;
      case ACE:
        rankCode = "A";
        break;
      default:
        rankCode = "?";
    }

    String suitCode;
    switch (suit) {
      case HEARTS:
        suitCode = "H";
        break;
      case DIAMONDS:
        suitCode = "D";
        break;
      case CLUBS:
        suitCode = "C";
        break;
      case SPADES:
        suitCode = "S";
        break;
      default:
        suitCode = "?";
    }

    return rankCode + suitCode;
  }

  /**
   * Erstellt eine Card aus einem Card-Code wie "AS", "10D", etc.
   *
   * @param cardCode der Karten-Code
   * @return Card-Objekt oder null bei ungültigem Code
   */
  public static Card fromCardCode(String cardCode) {
    if (cardCode == null || cardCode.length() < 2) {
      return null;
    }
    Rank rank = null;
    Suit suit = null;
    // Handle "10" cards (3 characters: "10" + suit)
    if (cardCode.startsWith("10") && cardCode.length() == 3) {
      rank = Rank.TEN;
      char suitChar = cardCode.charAt(2);
      switch (suitChar) {
        case 'H':
          suit = Suit.HEARTS;
          break;
        case 'D':
          suit = Suit.DIAMONDS;
          break;
        case 'C':
          suit = Suit.CLUBS;
          break;
        case 'S':
          suit = Suit.SPADES;
          break;
      }
    } else if (cardCode.length() == 2) {
      char rankChar = cardCode.charAt(0);
      char suitChar = cardCode.charAt(1);
      switch (rankChar) {
        case '2':
          rank = Rank.TWO;
          break;
        case '3':
          rank = Rank.THREE;
          break;
        case '4':
          rank = Rank.FOUR;
          break;
        case '5':
          rank = Rank.FIVE;
          break;
        case '6':
          rank = Rank.SIX;
          break;
        case '7':
          rank = Rank.SEVEN;
          break;
        case '8':
          rank = Rank.EIGHT;
          break;
        case '9':
          rank = Rank.NINE;
          break;
        case 'J':
          rank = Rank.JACK;
          break;
        case 'Q':
          rank = Rank.QUEEN;
          break;
        case 'K':
          rank = Rank.KING;
          break;
        case 'A':
          rank = Rank.ACE;
          break;
      }
      switch (suitChar) {
        case 'H':
          suit = Suit.HEARTS;
          break;
        case 'D':
          suit = Suit.DIAMONDS;
          break;
        case 'C':
          suit = Suit.CLUBS;
          break;
        case 'S':
          suit = Suit.SPADES;
          break;
      }
    }
    if (rank != null && suit != null) {
      return new Card(suit, rank);
    }
    return null;
  }
}
