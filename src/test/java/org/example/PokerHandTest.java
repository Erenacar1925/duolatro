package org.example;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import client.model.Card;
import server.PokerHand;
import org.junit.jupiter.api.Test;

public class PokerHandTest {
  private Card card(Card.Rank rank, Card.Suit suit) {
    return new Card(suit, rank);
  }

  @Test
  void testHighCard() {
    // Arrange
    List<Card> hand =
        List.of(
            card(Card.Rank.TWO, Card.Suit.HEARTS),
            card(Card.Rank.FIVE, Card.Suit.CLUBS),
            card(Card.Rank.NINE, Card.Suit.DIAMONDS),
            card(Card.Rank.JACK, Card.Suit.SPADES),
            card(Card.Rank.KING, Card.Suit.HEARTS));
    Card highCard = PokerHand.getHighCard(hand);

    // Assert
    assertEquals(PokerHand.PokerHandType.HIGH_CARD, PokerHand.evaluate(hand));
    assertEquals(Card.Rank.KING, highCard.getRank());
    assertEquals(Card.Suit.HEARTS, highCard.getSuit());
  }

  @Test
  void testOnePair() {
    // Arrange
    List<Card> hand =
        List.of(
            card(Card.Rank.TWO, Card.Suit.HEARTS),
            card(Card.Rank.TWO, Card.Suit.CLUBS),
            card(Card.Rank.NINE, Card.Suit.DIAMONDS),
            card(Card.Rank.JACK, Card.Suit.SPADES),
            card(Card.Rank.KING, Card.Suit.HEARTS));
    // Assert
    assertEquals(PokerHand.PokerHandType.ONE_PAIR, PokerHand.evaluate(hand));
  }

  @Test
  void testTwoPair() {
    // Arrange
    List<Card> hand =
        List.of(
            card(Card.Rank.TWO, Card.Suit.HEARTS),
            card(Card.Rank.TWO, Card.Suit.CLUBS),
            card(Card.Rank.NINE, Card.Suit.DIAMONDS),
            card(Card.Rank.NINE, Card.Suit.SPADES),
            card(Card.Rank.KING, Card.Suit.HEARTS));
    // Assert
    assertEquals(PokerHand.PokerHandType.TWO_PAIR, PokerHand.evaluate(hand));
  }

  @Test
  void testThreeOfAKind() {
    // Arrange
    List<Card> hand =
        List.of(
            card(Card.Rank.KING, Card.Suit.HEARTS),
            card(Card.Rank.KING, Card.Suit.CLUBS),
            card(Card.Rank.NINE, Card.Suit.DIAMONDS),
            card(Card.Rank.JACK, Card.Suit.SPADES),
            card(Card.Rank.KING, Card.Suit.HEARTS));
    // Assert
    assertEquals(PokerHand.PokerHandType.THREE_OF_A_KIND, PokerHand.evaluate(hand));
  }

  @Test
  void testStraight() {
    // Arrange
    List<Card> hand =
        List.of(
            card(Card.Rank.FIVE, Card.Suit.HEARTS),
            card(Card.Rank.SIX, Card.Suit.CLUBS),
            card(Card.Rank.SEVEN, Card.Suit.DIAMONDS),
            card(Card.Rank.EIGHT, Card.Suit.SPADES),
            card(Card.Rank.NINE, Card.Suit.HEARTS));
    // Assert
    assertEquals(PokerHand.PokerHandType.STRAIGHT, PokerHand.evaluate(hand));
  }

  @Test
  void testAceLowStraight() {
    // Arrange
    List<Card> hand =
        List.of(
            card(Card.Rank.ACE, Card.Suit.HEARTS),
            card(Card.Rank.TWO, Card.Suit.SPADES),
            card(Card.Rank.THREE, Card.Suit.CLUBS),
            card(Card.Rank.FOUR, Card.Suit.HEARTS),
            card(Card.Rank.FIVE, Card.Suit.DIAMONDS));
    // Assert
    assertEquals(PokerHand.PokerHandType.STRAIGHT, PokerHand.evaluate(hand));
  }

  @Test
  void testFlush() {
    // Arrange
    List<Card> hand =
        List.of(
            card(Card.Rank.TWO, Card.Suit.HEARTS),
            card(Card.Rank.FIVE, Card.Suit.HEARTS),
            card(Card.Rank.NINE, Card.Suit.HEARTS),
            card(Card.Rank.JACK, Card.Suit.HEARTS),
            card(Card.Rank.THREE, Card.Suit.HEARTS));
    // Assert
    assertEquals(PokerHand.PokerHandType.FLUSH, PokerHand.evaluate(hand));
  }

  @Test
  void testFullHouse() {
    // Arrange
    List<Card> hand =
        List.of(
            card(Card.Rank.QUEEN, Card.Suit.SPADES),
            card(Card.Rank.QUEEN, Card.Suit.HEARTS),
            card(Card.Rank.QUEEN, Card.Suit.CLUBS),
            card(Card.Rank.THREE, Card.Suit.HEARTS),
            card(Card.Rank.THREE, Card.Suit.DIAMONDS));
    // Assert
    assertEquals(PokerHand.PokerHandType.FULL_HOUSE, PokerHand.evaluate(hand));
  }

  @Test
  void testFourOfAKind() {
    // Arrange
    List<Card> hand =
        List.of(
            card(Card.Rank.SEVEN, Card.Suit.HEARTS),
            card(Card.Rank.SEVEN, Card.Suit.SPADES),
            card(Card.Rank.SEVEN, Card.Suit.CLUBS),
            card(Card.Rank.SEVEN, Card.Suit.DIAMONDS),
            card(Card.Rank.TWO, Card.Suit.HEARTS));
    // Assert
    assertEquals(PokerHand.PokerHandType.FOUR_OF_A_KIND, PokerHand.evaluate(hand));
  }

  @Test
  void testStraightFlush() {
    // Arrange
    List<Card> hand =
        List.of(
            card(Card.Rank.SIX, Card.Suit.CLUBS),
            card(Card.Rank.SEVEN, Card.Suit.CLUBS),
            card(Card.Rank.EIGHT, Card.Suit.CLUBS),
            card(Card.Rank.NINE, Card.Suit.CLUBS),
            card(Card.Rank.TEN, Card.Suit.CLUBS));
    // Assert
    assertEquals(PokerHand.PokerHandType.STRAIGHT_FLUSH, PokerHand.evaluate(hand));
  }

  @Test
  void testInvalidHandSize() {
    // Arrange
    List<Card> hand =
        List.of(
            card(Card.Rank.TWO, Card.Suit.HEARTS),
            card(Card.Rank.THREE, Card.Suit.CLUBS),
            card(Card.Rank.KING, Card.Suit.SPADES),
            card(Card.Rank.FIVE, Card.Suit.CLUBS),
            card(Card.Rank.TEN, Card.Suit.HEARTS),
            card(Card.Rank.TWO, Card.Suit.CLUBS));
    // Assert
    assertEquals(PokerHand.PokerHandType.INVALID, PokerHand.evaluate(hand));
  }

  @Test
  void testGetPointsForHandType() {
    assertEquals(10, PokerHand.getPointsForHandType(PokerHand.PokerHandType.HIGH_CARD));
    assertEquals(20, PokerHand.getPointsForHandType(PokerHand.PokerHandType.ONE_PAIR));
    assertEquals(40, PokerHand.getPointsForHandType(PokerHand.PokerHandType.TWO_PAIR));
    assertEquals(90, PokerHand.getPointsForHandType(PokerHand.PokerHandType.THREE_OF_A_KIND));
    assertEquals(120, PokerHand.getPointsForHandType(PokerHand.PokerHandType.STRAIGHT));
    assertEquals(140, PokerHand.getPointsForHandType(PokerHand.PokerHandType.FLUSH));
    assertEquals(160, PokerHand.getPointsForHandType(PokerHand.PokerHandType.FULL_HOUSE));
    assertEquals(420, PokerHand.getPointsForHandType(PokerHand.PokerHandType.FOUR_OF_A_KIND));
    assertEquals(800, PokerHand.getPointsForHandType(PokerHand.PokerHandType.STRAIGHT_FLUSH));
    assertEquals(0, PokerHand.getPointsForHandType(PokerHand.PokerHandType.INVALID));
  }
}
