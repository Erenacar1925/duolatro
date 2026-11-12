package server;

import client.model.Card;

import java.util.*;

/**
 * This class provides methods to evaluate poker hands and calculate scores based on the hand's
 * type.
 */
public class PokerHand {

  /** Enumeration of all possible poker hand types. */
  public enum PokerHandType {
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIR,
    THREE_OF_A_KIND,
    STRAIGHT,
    FLUSH,
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    STRAIGHT_FLUSH,
    INVALID
  }

  /**
   * Evaluates a given poker hand and returns its type. A hand must contain no more than 5 cards.
   *
   * @param hand The list of cards to evaluate.
   * @return The recognized {@link PokerHandType}.
   */
  public static PokerHandType evaluate(List<Card> hand) {
    if (hand.size() > 5) {
      return PokerHandType.INVALID;
    }

    boolean isFlush = isFlush(hand);
    boolean isStraight = isStraight(hand);
    Map<Card.Rank, Integer> rankCounts = getRankCounts(hand);
    Collection<Integer> counts = rankCounts.values();

    if (isFlush && isStraight) {
      return PokerHandType.STRAIGHT_FLUSH;
    }
    if (counts.contains(4)) {
      return PokerHandType.FOUR_OF_A_KIND;
    }
    if (counts.contains(3) && counts.contains(2)) {
      return PokerHandType.FULL_HOUSE;
    }
    if (isFlush) {
      return PokerHandType.FLUSH;
    }
    if (isStraight) {
      return PokerHandType.STRAIGHT;
    }
    if (counts.contains(3)) {
      return PokerHandType.THREE_OF_A_KIND;
    }

    int pairCount = 0;
    for (int count : counts) {
      if (count == 2) {
        pairCount++;
      }
    }
    if (pairCount == 2) {
      return PokerHandType.TWO_PAIR;
    }
    if (pairCount == 1) {
      return PokerHandType.ONE_PAIR;
    } else {
      Card highCard = getHighCard(hand);
      return PokerHandType.HIGH_CARD;
    }
  }

  /**
   * Checks if all cards in the hand have the same suit.
   *
   * @param hand The list of card to check.
   * @return true if all cards are of the same suit, false otherwise.
   */
  private static boolean isFlush(List<Card> hand) {
    if (hand.size() != 5) {
      return false;
    }

    Card.Suit suit = hand.get(0).getSuit();
    for (Card card : hand) {
      if (card.getSuit() != suit) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks whether the cards form a straight
   *
   * @param hand The list of cards to check.
   * @return true if the hand is a straight, false otherwise.
   */
  private static boolean isStraight(List<Card> hand) {
    if (hand.size() != 5) {
      return false;
    }

    List<Integer> values = new ArrayList<>();
    for (Card card : hand) {
      values.add(card.getRank().ordinal());
    }
    Collections.sort(values);

    boolean straight = true;
    for (int i = 1; i < values.size(); i++) {
      if (values.get(i) != values.get(i - 1) + 1) {
        straight = false;
        break;
      }
    }
    // Special case: Ace can be low in a 5-high straight (A-2-3-4-5)
    boolean aceLowStraight = values.equals(List.of(0, 1, 2, 3, 12));

    return straight || aceLowStraight;
  }

  /**
   * Returns the highest card in the hand.
   *
   * @param hand The list of cards.
   * @return The card with the highest rank.
   */
  public static Card getHighCard(List<Card> hand) {
    return hand.stream()
        .max(Comparator.comparingInt(card -> card.getRank().ordinal()))
        .orElse(null);
  }

  /**
   * Counts the occurrences of each rank in the hand.
   *
   * @param hand The list of cards.
   * @return A map with card ranks as keys and their counts as values.
   */
  private static Map<Card.Rank, Integer> getRankCounts(List<Card> hand) {
    Map<Card.Rank, Integer> counts = new HashMap<>();
    for (Card card : hand) {
      Card.Rank rank = card.getRank();
      counts.put(rank, counts.getOrDefault(rank, 0) + 1);
    }
    return counts;
  }

  /**
   * Returns the base amount of chips for a given PokerHandType.
   *
   * @param type The PokerHandType
   * @return The base amount of chips for this hand type
   */
  public static int baseScore(PokerHandType type) {
    switch (type) {
      case HIGH_CARD:
        return 10;
      case ONE_PAIR:
        return 10;
      case TWO_PAIR:
        return 20;
      case THREE_OF_A_KIND:
        return 30;
      case STRAIGHT:
        return 30;
      case FLUSH:
        return 35;
      case FULL_HOUSE:
        return 40;
      case FOUR_OF_A_KIND:
        return 60;
      case STRAIGHT_FLUSH:
        return 100;
      case INVALID:
      default:
        return 0;
    }
  }

  /**
   * Returns the multiplier for a given PokerHandType.
   *
   * @param type The PokerHandType
   * @return The multiplier for this hand type
   */
  public static int multiplier(PokerHandType type) {
    switch (type) {
      case HIGH_CARD:
        return 1;
      case ONE_PAIR:
        return 2;
      case TWO_PAIR:
        return 2;
      case THREE_OF_A_KIND:
        return 3;
      case STRAIGHT:
        return 4;
      case FLUSH:
        return 4;
      case FULL_HOUSE:
        return 4;
      case FOUR_OF_A_KIND:
        return 7;
      case STRAIGHT_FLUSH:
        return 8;
      case INVALID:
      default:
        return 0;
    }
  }

  /**
   * Returns the total point value for a given hand type.
   *
   * @param type The poker hand type.
   * @return The calculated point score.
   */
  public static int getPointsForHandType(PokerHandType type) {
    return baseScore(type) * multiplier(type);
  }
}
