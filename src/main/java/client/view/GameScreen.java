package client.view;

import client.ScreenManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import client.model.Card;
import client.model.Deck;
import client.controller.GameController;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class GameScreen {
  private ScreenManager manager;
  private BorderPane root;
  private GameController controller;

  // fields for pop-up box with buttons
  private VBox actionBox;
  private final Set<ImageView> selectedCardsPlayer1 = new HashSet<>();
  private final Set<ImageView> selectedCardsPlayer2 = new HashSet<>();
  private List<String> discardedCardCodes = new ArrayList<>();

  // fields for card movement
  private HBox cardsPlayer1;
  private HBox cardsPlayer2;
  private BorderPane gameArea;
  private StackPane mainGameArea;
  private HBox playedCardsArea;
  private StackPane blueFrame;
  private List<String> pendingDiscardCardCodes = new ArrayList<>();

  // fields for making cards clickable for only one player
  private int currentPlayer = 1; // 1 = Player 1, 2 = Player 2

  // New fields for controller
  private Button infoButton;
  private Button optionsButton;
  private Button discardButton; // Add field for discard button
  private Label currentScore;
  private Label roundNumber;
  private Label handsCounter;
  private Label discardsCounter;
  private Label ante;
  private Label bank;
  private Label pokerHand;
  private Label pokerHandBaseScore;
  private Label pokerHandMultiplier;
  private Label bossTitle;
  private Label bossScore;
  private Label pokerHandLevel;
  private Label waitingLabel;

  public GameScreen(ScreenManager manager) {
    this.manager = manager;
    root = new BorderPane();
    root.getStyleClass().add("game-screen-background");

    BorderPane sidebar = new BorderPane(); // <-- ADD THIS LINE at the start!
    sidebar.getStyleClass().add("sidebar");
    sidebar.setPadding(new Insets(20));
    sidebar.setPrefWidth(250);
    // Boxes for Hands and Discards
    VBox boxHands = createMiniBoxWithValueLabel("Hands", "4");
    VBox boxDiscards = createMiniBoxWithValueLabel("Discards", "4");
    HBox handsDiscards = new HBox(10, boxHands, boxDiscards);
    HBox.setHgrow(boxHands, Priority.ALWAYS);
    HBox.setHgrow(boxDiscards, Priority.ALWAYS);

    // Boxes for Ante und Round
    VBox boxAnte = createMiniBoxWithValueLabel("Ante", "1 / 8");
    // Store round label as a field
    roundNumber = new Label("1");
    VBox boxRound = new VBox(new Label("Round"), roundNumber);
    boxRound.setAlignment(Pos.CENTER);
    boxRound.getStyleClass().add("mini-box");
    HBox anteRound = new HBox(10, boxAnte, boxRound);
    HBox.setHgrow(boxAnte, Priority.ALWAYS);
    HBox.setHgrow(boxRound, Priority.ALWAYS);

    // Sidebar elements at the top
    VBox sidebarContent = new VBox(10);
    sidebarContent
        .getChildren()
        .addAll(
            createBossInfoBox("Big Blind", "500"),
            createScoreInfoBox(), // Score label as a field
            createPokerHandInfoBox("Poker Hand", " "),
            handsDiscards,
            createInfoBoxWithValueLabel("Bank", "4 $"),
            anteRound);
    sidebar.setTop(sidebarContent);


    // Buttons for Info and Options (sidebar)
    HBox buttons = new HBox(10);
    buttons.setAlignment(Pos.CENTER);
    infoButton = new Button("Info");
    optionsButton = new Button("Options");
    infoButton.getStyleClass().add("sidebar-button");
    optionsButton.getStyleClass().add("sidebar-button");
    buttons.getChildren().addAll(infoButton, optionsButton);

    sidebar.setBottom(buttons);

    // Red frame for sidebar
    VBox redFrame = new VBox(sidebar);
    redFrame.getStyleClass().add("red-frame");
    VBox.setVgrow(sidebar, Priority.ALWAYS);

    // Blue frame for sidebar
    blueFrame = new StackPane(redFrame);
    blueFrame.getStyleClass().add("blue-frame");
    HBox.setMargin(blueFrame, new Insets(10));
    VBox.setVgrow(redFrame, Priority.ALWAYS);

    // Game area
    gameArea = new BorderPane();
    gameArea.setPadding(new Insets(20));

    cardsPlayer1 = new HBox(8);
    cardsPlayer1.setAlignment(Pos.CENTER);
    cardsPlayer1.setPadding(new Insets(20, 0, 20, 0));

    cardsPlayer2 = new HBox(8);
    cardsPlayer2.setAlignment(Pos.CENTER);
    cardsPlayer2.setPadding(new Insets(20, 0, 20, 0));

    ImageView drawPile = CardView.getBackSide();
    drawPile.getStyleClass().add("draw-pile");

    playedCardsArea = new HBox(10);
    playedCardsArea.setAlignment(Pos.CENTER);
    playedCardsArea.setPadding(new Insets(20));

    StackPane middleStack = new StackPane(playedCardsArea, drawPile);
    middleStack.setAlignment(drawPile, Pos.CENTER_RIGHT);
    StackPane.setMargin(drawPile, new Insets(0, 20, 0, 0));

    gameArea.setTop(cardsPlayer2);
    gameArea.setCenter(middleStack);
    gameArea.setBottom(cardsPlayer1);

    drawPile.getStyleClass().add("draw-pile");

    cardsPlayer2.getStyleClass().add("card-row");
    cardsPlayer1.getStyleClass().add("card-row");

    StackPane.setAlignment(drawPile, Pos.CENTER_RIGHT);

    // Game area wrapper
    mainGameArea = new StackPane();
    mainGameArea.setPrefWidth(800);
    mainGameArea.setPrefHeight(600);
    HBox.setHgrow(mainGameArea, Priority.ALWAYS);

    HBox centerLayout = new HBox(blueFrame, mainGameArea);
    root.setCenter(centerLayout);

    // Pop-up-box with buttons
    // Pop-up-box
    actionBox = new VBox(20);
    actionBox.setVisible(false); // not visible in the beginning
    actionBox.setAlignment(Pos.CENTER);
    actionBox.getStyleClass().add("popup-box");
    actionBox.setPadding(new Insets(20));
// In your constructor, after initializing mainGameArea:
    waitingLabel = new Label();
    waitingLabel.getStyleClass().add("waiting-label"); // Add CSS as you like
    waitingLabel.setVisible(false);
    mainGameArea.getChildren().add(waitingLabel);
    StackPane.setAlignment(waitingLabel, Pos.CENTER);
    // title
    Label popupTitle = new Label("What would you like to do?");
    popupTitle.getStyleClass().add("popup-title");

    // buttons
    Button playButton = new Button("Play Hand");
    playButton.getStyleClass().add("play-hand-button");
    ImageView playHandIcon =
        new ImageView(new Image(getClass().getResourceAsStream("/images/play-hand.png")));
    playHandIcon.setFitWidth(46);
    playHandIcon.setFitHeight(46);
    playButton.setGraphic(playHandIcon); // add icon
    playButton.setContentDisplay(ContentDisplay.BOTTOM);
    playButton.setPrefWidth(140);
    playButton.setOnAction(
        e -> {
          // Notify controller about playing the hand
          if (controller != null) {
            controller.onPlayHand();
          }

          if (!selectedCardsPlayer1.isEmpty()) {
            playSelectedCardsGeneric(
                selectedCardsPlayer1,
                cardsPlayer1,
                true,
                () -> {
                  controller.onTurnFinished();
                });
          } else if (!selectedCardsPlayer2.isEmpty()) {
            playSelectedCardsGeneric(
                selectedCardsPlayer2,
                cardsPlayer2,
                false,
                () -> {
                  controller.onTurnFinished();
                });
          }
        });

    discardButton = new Button("Discard");
    discardButton.getStyleClass().add("discard-button");
    ImageView discardIcon =
        new ImageView(new Image(getClass().getResourceAsStream("/images/discard.png")));
    discardIcon.setFitWidth(46);
    discardIcon.setFitHeight(46);
    discardButton.setGraphic(discardIcon); // add icon
    discardButton.setContentDisplay(ContentDisplay.BOTTOM);
    discardButton.setPrefWidth(140);
    discardButton.setOnAction(
        e -> {
          // Notify controller about discarding the hand
          if (controller != null) {
            controller.onDiscard();
          }

          if (!selectedCardsPlayer1.isEmpty()) {
            discardSelectedCardsGeneric(
                selectedCardsPlayer1,
                cardsPlayer1,
                true,
                () -> {
                  controller.onTurnFinished();
                });
          } else if (!selectedCardsPlayer2.isEmpty()) {
            discardSelectedCardsGeneric(
                selectedCardsPlayer2,
                cardsPlayer2,
                false,
                () -> {
                  controller.onTurnFinished();
                });
          }
        });

    HBox buttonRow = new HBox(20, playButton, discardButton);
    buttonRow.setAlignment(Pos.CENTER);

    actionBox.getChildren().addAll(popupTitle, buttonRow);

    // add pop-up-box to the mainGameArea
    mainGameArea.getChildren().addAll(gameArea, actionBox);
    StackPane.setAlignment(actionBox, Pos.CENTER);
  }

  // Add these methods:
  public void setInputEnabled(boolean enabled) {
    // Disable/enable action box, play/discard buttons, or card clicks as needed
    actionBox.setDisable(!enabled);
    // You may want to also disable selecting cards if !enabled
    // Example: just disabling the whole game area or individual controls
    // cardsPlayer1.setDisable(!enabled); // etc.
  }
  public void animateDrawCards(boolean isPlayer1, int numberOfCards) {
    // You can add some animation here or highlight new cards
    drawNewCards(isPlayer1, numberOfCards);
  }

  private String pendingDiscardsOwner = null;

  public void setPendingDiscards(List<String> codes, String owner) {
    this.pendingDiscardCardCodes = codes != null ? codes : new ArrayList<>();
    this.pendingDiscardsOwner = owner;
    highlightPendingCards();
  }


  public void setDiscardedCards(List<String> codes) {
    this.discardedCardCodes = codes;
    highlightPendingCards();
  }

  public void setPendingDiscards(List<String> codes) {
    this.pendingDiscardCardCodes = codes;
    highlightPendingCards();
  }

  public void clearPendingDiscards() {
    this.pendingDiscardCardCodes.clear();
    highlightPendingCards();
  }

  public void highlightPendingCards() {
    for (Node node : cardsPlayer1.getChildren()) {
      if (!(node instanceof ImageView)) continue;
      ImageView cardView = (ImageView) node;
      String code = (String) cardView.getUserData();

      // Remove all highlight classes first
      cardView.getStyleClass().removeAll("pending-discard-card", "discarded-card");

      if (discardedCardCodes.contains(code)) {
        cardView.getStyleClass().add("discarded-card");
        cardView.setDisable(true);
      } else if (pendingDiscardCardCodes.contains(code)) {
        cardView.getStyleClass().add("pending-discard-card");
        cardView.setDisable(false);
      } else {
        cardView.setDisable(false);
      }
    }
  }
  public void playSelectedCardsWithDrawAnimation(boolean isPlayer1, int numCards, Runnable afterAnimation) {
    Set<ImageView> selectedSet = isPlayer1 ? selectedCardsPlayer1 : selectedCardsPlayer2;
    HBox cardRow = isPlayer1 ? cardsPlayer1 : cardsPlayer2;

    playSelectedCardsGeneric(selectedSet, cardRow, isPlayer1, () -> {
      // After playing, animate drawing new cards
      drawNewCards(isPlayer1, numCards);
      if (afterAnimation != null) afterAnimation.run();
    });
  }
  public void showWaitingMessage(String msg) {
    waitingLabel.setText(msg);
    waitingLabel.setVisible(true);
  }

  public void hideWaitingMessage() {
    waitingLabel.setVisible(false);
  }
  // --- NEW: Show hand for a player (bottom = 1, top = 2) ---
  /**
   * Displays the hand for a player.
   * @param playerIndex 1 = bottom (you), 2 = top (opponent)
   * @param cards List of Card objects
   */
  public void showPlayerHand(int playerIndex, List<Card> cards) {
    HBox cardRow = (playerIndex == 1) ? cardsPlayer1 : cardsPlayer2;
    Set<ImageView> selectedSet = (playerIndex == 1) ? selectedCardsPlayer1 : selectedCardsPlayer2;

    cardRow.getChildren().clear();
    selectedSet.clear();

    boolean moveUp = playerIndex == 1;
    boolean isPlayer1 = playerIndex == 1;

    for (Card card : cards) {
      ImageView cardView = createSelectableCard(card.toCardCode(), moveUp, isPlayer1, selectedSet);
      cardRow.getChildren().add(cardView);
    }
    updateCardOpacity();
  }
  // method that makes the cards selectable by clicking on it
  private ImageView createSelectableCard(
          String code, boolean moveUp, boolean isPlayer1, Set<ImageView> targetSet) {
    ImageView card = CardView.getCardImage(code);
    card.setUserData(code);
    card.setOnMouseClicked(
        event -> {
          // Erlaube nur Aktionen, wenn Karte zum aktuellen Spieler gehört
          if ((currentPlayer == 1 && !isPlayer1) || (currentPlayer == 2 && isPlayer1)) {
            return; // falscher Spieler -> keine Aktion
          }

          double shift = 20;
          double direction = moveUp ? -shift : shift;

          if (targetSet.contains(card)) {
            card.setTranslateY(0);
            targetSet.remove(card);
            // Notify controller about card deselection
            if (controller != null) {
              controller.onCardDeselected(code);
            }
          } else {
            if (targetSet.size() < 5) {
              card.setTranslateY(direction);
              targetSet.add(card);
              // Notify controller about card selection
              if (controller != null) {
                controller.onCardSelected(code);
              }
            }
          }

          actionBox.setVisible(!selectedCardsPlayer1.isEmpty() || !selectedCardsPlayer2.isEmpty());
        });

    return card;
  }

  // functionality for button "Play Hand"
  private void playSelectedCardsGeneric(
      Set<ImageView> selectedCards, HBox cardRow, boolean isPlayer1, Runnable onFinishedCallback) {
    actionBox.setVisible(false);

    double verticalShift = isPlayer1 ? -250 : 250;

    List<TranslateTransition> shiftToMiddle = new ArrayList<>();

    for (ImageView card : selectedCards) {
      TranslateTransition shift = new TranslateTransition(Duration.millis(300), card);
      shift.setByY(verticalShift);
      shiftToMiddle.add(shift);
      shift.play();
    }

    // pause so that the cards stay in the middle for a little while
    PauseTransition pauseBeforeFlyOut = new PauseTransition(Duration.millis(800));

    pauseBeforeFlyOut.setOnFinished(
        event -> {
          Bounds sidebarBounds = blueFrame.localToScene(blueFrame.getBoundsInLocal());
          double targetX = sidebarBounds.getMaxX() + 10; // target point right next to the sideboard

          for (ImageView card : selectedCards) {
            Bounds cardBounds = card.localToScene(card.getBoundsInLocal());
            double currentX = cardBounds.getMinX();
            double deltaX = targetX - currentX;

            TranslateTransition flyOut = new TranslateTransition(Duration.millis(400), card);
            flyOut.setByX(deltaX);
            flyOut.setOnFinished(e -> card.setVisible(false));
            flyOut.play();
          }

          // cleanup and draw new cards
          PauseTransition cleanup = new PauseTransition(Duration.millis(450));
          cleanup.setOnFinished(
              e -> {
                // Get the number of cards that were played
                int numberOfPlayedCards = selectedCards.size();

                // Remove the played cards from the GUI
                cardRow.getChildren().removeAll(selectedCards);
                selectedCards.clear();

                // Draw new cards to replace the played ones
                if (numberOfPlayedCards > 0) {
                  drawNewCards(isPlayer1, numberOfPlayedCards);
                }

                if (onFinishedCallback != null) {
                  onFinishedCallback.run();
                }
              });
          cleanup.play();
        });

    pauseBeforeFlyOut.play();
  }

  // functionality for button "Discard"
  private void discardSelectedCardsGeneric(
      Set<ImageView> selectedCards, HBox cardRow, boolean isPlayer1, Runnable onFinishedCallback) {
    actionBox.setVisible(false);

    double verticalShift = isPlayer1 ? -250 : 250;

    List<TranslateTransition> shiftToMiddle = new ArrayList<>();

    for (ImageView card : selectedCards) {
      TranslateTransition shift = new TranslateTransition(Duration.millis(300), card);
      shift.setByY(verticalShift);
      shiftToMiddle.add(shift);
      shift.play();
    }

    // pause so that the cards stay in the middle for a little while
    PauseTransition pauseBeforeFlyOut = new PauseTransition(Duration.millis(800));

    pauseBeforeFlyOut.setOnFinished(
        event -> {
          Bounds gameAreaBounds = mainGameArea.localToScene(mainGameArea.getBoundsInLocal());
          double targetX = gameAreaBounds.getMaxX();

          for (ImageView card : selectedCards) {
            Bounds cardBounds = card.localToScene(card.getBoundsInLocal());
            double currentX = cardBounds.getMinX() + 210;
            double deltaX = targetX - currentX;

            TranslateTransition flyOut = new TranslateTransition(Duration.millis(400), card);
            flyOut.setByX(deltaX);
            flyOut.setOnFinished(e -> card.setVisible(false));
            flyOut.play();
          }

          // cleanup and draw new cards
          PauseTransition cleanup = new PauseTransition(Duration.millis(450));
          cleanup.setOnFinished(
              e -> {
                // Get the number of cards that were discarded
                int numberOfDiscardedCards = selectedCards.size();

                // Remove the discarded cards from the GUI
                cardRow.getChildren().removeAll(selectedCards);
                selectedCards.clear();

                // Draw new cards to replace the discarded ones
                if (numberOfDiscardedCards > 0) {
                  drawNewCards(isPlayer1, numberOfDiscardedCards);
                }

                if (onFinishedCallback != null) {
                  onFinishedCallback.run();
                }
              });
          cleanup.play();
        });

    pauseBeforeFlyOut.play();
  }

  public List<String> getSelectedPlayer1CardCodes() {
    List<String> codes = new ArrayList<>();
    for (ImageView cardView : selectedCardsPlayer1) {
      // get the code from the cardView's image name (or better, setUserData as below)
      Object code = cardView.getUserData();
      if (code instanceof String) codes.add((String) code);
    }
    return codes;
  }

  // method that makes the cards of the inactive player grey
  private void updateCardOpacity() {
    for (Node card : cardsPlayer1.getChildren()) {
      card.setOpacity(currentPlayer == 1 ? 1.0 : 0.4); // 1.0 = volle Sichtbarkeit, 0.4 = ausgegraut
    }
    for (Node card : cardsPlayer2.getChildren()) {
      card.setOpacity(currentPlayer == 2 ? 1.0 : 0.4);
    }
  }

  // Score label as a field
  private VBox createScoreInfoBox() {
    Label titleLabel = new Label("Current score");
    titleLabel.getStyleClass().add("info-title");
    currentScore = new Label("0");
    currentScore.getStyleClass().add("info-value");
    VBox box = new VBox(4, titleLabel, currentScore);
    box.setAlignment(Pos.CENTER_LEFT);
    box.getStyleClass().add("info-box");
    return box;
  }

  private VBox createInfoBox(String title, String value) {
    Label titleLabel = new Label(title);
    titleLabel.getStyleClass().add("info-title");

    Label valueLabel = new Label(value);
    valueLabel.getStyleClass().add("info-value");

    VBox box = new VBox(4, titleLabel, valueLabel);
    box.setAlignment(Pos.CENTER_LEFT);
    box.getStyleClass().add("info-box");
    return box;
  }

  private VBox createMiniBox(String title, String value) {
    Label titleLabel = new Label(title);
    Label valueLabel = new Label(value);
    VBox box = new VBox(titleLabel, valueLabel);
    box.setAlignment(Pos.CENTER);
    box.getStyleClass().add("mini-box");
    return box;
  }

  // Helper method that stores the value label as a field
  private VBox createMiniBoxWithValueLabel(String title, String value) {
    Label titleLabel = new Label(title);
    Label valueLabel = new Label(value);
    VBox box = new VBox(titleLabel, valueLabel);
    box.setAlignment(Pos.CENTER);
    box.getStyleClass().add("mini-box");
    switch (title) {
      case "Hands":
        handsCounter = valueLabel;
        break;
      case "Discards":
        discardsCounter = valueLabel;
        break;
      case "Ante":
        ante = valueLabel;
        break;
    }
    return box;
  }

  // Helper method for Boss (title and value are both dynamic)
  private VBox createBossInfoBox(String title, String value) {
    bossTitle = new Label(title);
    bossTitle.getStyleClass().add("info-title");
    bossScore = new Label(value);
    bossScore.getStyleClass().add("info-value");
    VBox box = new VBox(4, bossTitle, bossScore);
    box.setAlignment(Pos.CENTER_LEFT);
    box.getStyleClass().add("info-box");
    return box;
  }

  // Helper method that stores the value label as a field for info boxes
  private VBox createInfoBoxWithValueLabel(String title, String value) {
    Label titleLabel = new Label(title);
    titleLabel.getStyleClass().add("info-title");
    Label valueLabel = new Label(value);
    valueLabel.getStyleClass().add("info-value");
    VBox box = new VBox(4, titleLabel, valueLabel);
    box.setAlignment(Pos.CENTER_LEFT);
    box.getStyleClass().add("info-box");
    // Store the reference depending on the title
    switch (title) {
      case "Poker Hand":
        pokerHand = titleLabel;
        pokerHandLevel = valueLabel;
        break;
      case "Bank":
        bank = valueLabel;
        break;
    }
    return box;
  }

  // Helper method for Poker Hand with base score and multiplier
  private VBox createPokerHandInfoBox(String title, String value) {
    Label titleLabel = new Label(title);
    titleLabel.getStyleClass().add("info-title");
    pokerHand = titleLabel;
    pokerHandLevel = new Label(value);
    pokerHandLevel.getStyleClass().add("info-value");
    HBox titleRow = new HBox(2);
    titleRow.setAlignment(Pos.CENTER);
    pokerHand.setAlignment(Pos.CENTER);
    pokerHandLevel.setAlignment(Pos.CENTER);
    titleRow.getChildren().addAll(pokerHand, pokerHandLevel);

    // baseScore, multiplier
    pokerHandBaseScore = new Label("0");
    pokerHandBaseScore.getStyleClass().addAll("info-value");
    pokerHandMultiplier = new Label("x 0");
    pokerHandMultiplier.getStyleClass().addAll("info-value");
    HBox scoreRow = new HBox(2);
    scoreRow.setAlignment(Pos.CENTER);
    pokerHandBaseScore.setAlignment(Pos.CENTER);
    pokerHandMultiplier.setAlignment(Pos.CENTER);
    scoreRow.getChildren().addAll(pokerHandBaseScore, pokerHandMultiplier);

    VBox box = new VBox(4, titleRow, scoreRow);
    box.setAlignment(Pos.CENTER_LEFT);
    box.getStyleClass().add("info-box");
    return box;
  }

  public Pane getView() {
    return root;
  }

  // Getters for controller
  public Button getInfoButton() {
    return infoButton;
  }

  public Button getOptionsButton() {
    return optionsButton;
  }

  public Button getDiscardButton() {
    return discardButton;
  }

  public Label getCurrentScore() {
    return currentScore;
  }

  public Label getRoundNumber() {
    return roundNumber;
  }

  public Label getHandsCounter() {
    return handsCounter;
  }

  public Label getDiscardsCounter() {
    return discardsCounter;
  }

  public Label getAnte() {
    return ante;
  }

  public Label getBank() {
    return bank;
  }

  public Label getPokerHand() {
    return pokerHand;
  }

  public Label getPokerHandLevel() {
    return pokerHandLevel;
  }

  public Label getPokerHandBaseScore() {
    return pokerHandBaseScore;
  }

  public Label getPokerHandMultiplier() {
    return pokerHandMultiplier;
  }

  public Label getBossScore() {
    return bossScore;
  }

  public Label getBossTitle() {
    return bossTitle;
  }

  // Setters for values
  public void setCurrentScore(String score) {
    if (currentScore != null) currentScore.setText(score);
  }

  public void setRoundNumber(String roundNumber) {
    if (this.roundNumber != null) this.roundNumber.setText(roundNumber);
  }

  public void setHands(String hands) {
    if (handsCounter != null) handsCounter.setText(hands);
  }

  public void setDiscardsCounter(String discardsCounter) {
    if (this.discardsCounter != null) this.discardsCounter.setText(discardsCounter);
  }

  public void setAnte(String ante) {
    if (this.ante != null) this.ante.setText(ante);
  }

  public void setBank(String bank) {
    if (this.bank != null) this.bank.setText(bank);
  }

  public void setPokerHand(String pokerHand) {
    if (this.pokerHand != null) this.pokerHand.setText(pokerHand);
  }

  public void setPokerHandBaseScore(String baseScore) {
    if (pokerHandBaseScore != null) pokerHandBaseScore.setText(baseScore);
  }

  public void setPokerHandMultiplier(String multiplier) {
    if (pokerHandMultiplier != null) pokerHandMultiplier.setText(multiplier);
  }

  public void setPokerHandLevel(String level) {
    if (pokerHandLevel != null) pokerHandLevel.setText(level);
  }

  public void setBoss(String title, String value) {
    if (bossTitle != null) bossTitle.setText(title);
    if (bossScore != null) bossScore.setText(value);
  }

  /**
   * Sets the discard button to disabled or enabled state.
   *
   * @param disabled true to disable the button, false to enable it
   */
  public void setDiscardButtonDisabled(boolean disabled) {
    if (discardButton != null) {
      discardButton.setDisable(disabled);
    }
  }

  // Event handler setters
  public void setOnInfoButton(javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
    if (infoButton != null) infoButton.setOnAction(handler);
  }

  public void setOnOptionsButton(javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
    if (optionsButton != null) optionsButton.setOnAction(handler);
  }

  public void setController(GameController controller) {
    this.controller = controller;
  }

  /**
   * Draws new cards from the deck and adds them to the specified player's hand in the GUI.
   *
   * @param isPlayer1 true if drawing for player 1, false for player 2
   * @param numberOfCards number of cards to draw
   */
  public void drawNewCards(boolean isPlayer1, int numberOfCards) {
    HBox targetCardRow = isPlayer1 ? cardsPlayer1 : cardsPlayer2;
    Set<ImageView> targetSelectedSet = isPlayer1 ? selectedCardsPlayer1 : selectedCardsPlayer2;

    // Request new cards from the controller
    if (controller != null) {
      List<String> newCardCodes = controller.requestNewCards(numberOfCards);
      boolean moveUp = isPlayer1;

      // Add new cards to the GUI
      for (String cardCode : newCardCodes) {
        ImageView newCard = createSelectableCard(cardCode, moveUp, isPlayer1, targetSelectedSet);
        targetCardRow.getChildren().add(newCard);
      }

      updateCardOpacity();
    }
  }

  /**
   * Replaces specific cards in the GUI with new cards from the deck.
   *
   * @param isPlayer1 true if replacing for player 1, false for player 2
   * @param cardCodesToReplace list of card codes to replace
   */
  public void replaceCards(boolean isPlayer1, List<String> cardCodesToReplace) {
    HBox targetCardRow = isPlayer1 ? cardsPlayer1 : cardsPlayer2;
    Set<ImageView> targetSelectedSet = isPlayer1 ? selectedCardsPlayer1 : selectedCardsPlayer2;

    // Request new cards from the controller
    if (controller != null) {
      List<String> newCardCodes = controller.requestNewCards(cardCodesToReplace.size());

      // Find and replace the old cards with new ones
      for (int i = 0; i < cardCodesToReplace.size() && i < newCardCodes.size(); i++) {
        String oldCardCode = cardCodesToReplace.get(i);
        String newCardCode = newCardCodes.get(i);

        // Find the old card in the GUI and replace it
        for (int j = 0; j < targetCardRow.getChildren().size(); j++) {
          ImageView cardView = (ImageView) targetCardRow.getChildren().get(j);
          // We need to identify the card by its user data or some other means
          // For now, we'll replace cards sequentially
          if (j < targetCardRow.getChildren().size()) {
            boolean moveUp = isPlayer1;
            ImageView newCard =
                createSelectableCard(newCardCode, moveUp, isPlayer1, targetSelectedSet);
            targetCardRow.getChildren().set(j, newCard);
            break;
          }
        }
      }

      updateCardOpacity();
    }
  }

  // Setter for the current player
  public void setCurrentPlayer(int playerNumber) {
    this.currentPlayer = playerNumber;
    updateCardOpacity();
  }

}
