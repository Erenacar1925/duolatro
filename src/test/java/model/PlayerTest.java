package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import client.model.Card;
import client.model.Player;
import org.junit.jupiter.api.Test;

public class PlayerTest {

  @Test
  void testDiscardCard_success() {
    // Assemble
    Player player = new Player();
    Card card = new Card(Card.Suit.HEARTS, Card.Rank.ACE);
    player.addCardToHand(card);

    // Act
    boolean result = player.discardCard(card);

    // Assert
    assertTrue(result);
    assertFalse(player.getHand().contains(card));
    assertEquals(3, player.getRemainingDiscards());
  }

  @Test
  void testDiscardCard_noDiscardsLeft() {
    // Assemble
    Player player = new Player();
    Card card = new Card(Card.Suit.HEARTS, Card.Rank.ACE);
    player.addCardToHand(card);
    player.resetForNewRound(player.getRemainingHands(), 0);

    // Act
    boolean result = player.discardCard(card);

    // Assert
    assertFalse(result);
    assertTrue(player.getHand().contains(card));
    assertEquals(0, player.getRemainingDiscards());
  }

  @Test
  void testDiscardCard_cardNotInHand() {
    // Assemble
    Player player = new Player();
    Card card = new Card(Card.Suit.HEARTS, Card.Rank.ACE);

    // Act
    boolean result = player.discardCard(card);

    // Assert
    assertFalse(result);
    assertEquals(4, player.getRemainingDiscards());
  }

  @Test
  void testPlayHand_success() {
    // Assemble
    Player player = new Player();
    List<Card> cards =
        Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.ACE),
            new Card(Card.Suit.CLUBS, Card.Rank.KING),
            new Card(Card.Suit.DIAMONDS, Card.Rank.QUEEN),
            new Card(Card.Suit.SPADES, Card.Rank.JACK),
            new Card(Card.Suit.HEARTS, Card.Rank.TEN));
    for (Card c : cards) player.addCardToHand(c);
    int oldScore = player.getScore();
    int oldHands = player.getRemainingHands();

    // Act
    int points = player.playHand(new ArrayList<>(cards));

    // Assert
    assertEquals(oldScore + points, player.getScore());
    assertEquals(oldHands - 1, player.getRemainingHands());
    for (Card c : cards) {
      assertFalse(player.getHand().contains(c));
    }
  }

  @Test
  void testPlayHand_invalidCards_exception() {
    // Assemble
    Player player = new Player();
    List<Card> cards =
        Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.ACE),
            new Card(Card.Suit.CLUBS, Card.Rank.KING),
            new Card(Card.Suit.DIAMONDS, Card.Rank.QUEEN),
            new Card(Card.Suit.SPADES, Card.Rank.JACK),
            new Card(Card.Suit.HEARTS, Card.Rank.TEN));

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> player.playHand(new ArrayList<>(cards)));
  }

  @Test
  void testPlayHand_tooManyCards() {
    // Assemble
    Player player = new Player();
    List<Card> cards =
        Arrays.asList(
            new Card(Card.Suit.HEARTS, Card.Rank.ACE),
            new Card(Card.Suit.CLUBS, Card.Rank.KING),
            new Card(Card.Suit.DIAMONDS, Card.Rank.QUEEN),
            new Card(Card.Suit.SPADES, Card.Rank.JACK),
            new Card(Card.Suit.HEARTS, Card.Rank.TEN),
            new Card(Card.Suit.CLUBS, Card.Rank.NINE));
    for (Card c : cards) {
      player.addCardToHand(c);
    }

    // Act
    int result = player.playHand(new ArrayList<>(cards));

    // Assert
    assertEquals(0, result);
  }
}
