package client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a deck of cards. In Duolatro, this is a shared deck composed of two standard 52-card
 * decks (104 cards total).
 */
public class Deck {

  private final List<Card> deck;

  /** Constructs a new deck of 104 cards (2 standard decks) and shuffles it. */
  public Deck() {
    this.deck = new ArrayList<>();
    for (Card.Suit suit : Card.Suit.values()) {
      for (Card.Rank rank : Card.Rank.values()) {
        deck.add(new Card(suit, rank));
        deck.add(new Card(suit, rank));
      }
    }
    shuffle();
  }

  /** Shuffles the deck randomly. */
  public void shuffle() {
    Collections.shuffle(this.deck);
  }

  /**
   * Draws a card from the top of the deck.
   *
   * @return the card drawn, or null if the deck is empty.
   */
  public Card draw() {
    if (isEmpty()) {
      return null;
    }
    return deck.remove(deck.size() - 1);
  }

  /**
   * Checks if the deck is empty.
   *
   * @return true if the deck has no cards left, false otherwise.
   */
  public boolean isEmpty() {
    return deck.isEmpty();
  }

  /**
   * Returns the number of cards remaining in the deck.
   *
   * @return the number of cards.
   */
  public int size() {
    return deck.size();
  }
}
