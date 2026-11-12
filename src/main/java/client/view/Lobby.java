package client.view;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Lobby {
  private BorderPane root;
  private StackPane mainLayout;
  private Rectangle rectangle1, rectangle2;
  private Label labelRectangle1, labelRectangle2;
  private Label countdownLabel;
  private Button readyButton, cancelReadyButton;
  private VBox readyBox;

  public Lobby() {
    rectangle1 = new Rectangle(200, 100, Color.RED);
    rectangle2 = new Rectangle(200, 100, Color.RED);

    labelRectangle1 = new Label("Waiting...");
    labelRectangle2 = new Label("Waiting...");
    labelRectangle1.getStyleClass().add("label-player-connection");
    labelRectangle2.getStyleClass().add("label-player-connection");

    StackPane playerBox1 = new StackPane(rectangle1, labelRectangle1);
    StackPane playerBox2 = new StackPane(rectangle2, labelRectangle2);
    playerBox1.setAlignment(Pos.CENTER);
    playerBox2.setAlignment(Pos.CENTER);

    HBox rectanglesBox = new HBox(40, playerBox1, playerBox2);
    rectanglesBox.setAlignment(Pos.CENTER);

    Label title = new Label("Waiting for players to connect...");
    title.getStyleClass().add("lobby-title");
    title.setAlignment(Pos.CENTER);

    countdownLabel = new Label("");
    countdownLabel.getStyleClass().add("countdown-label");
    countdownLabel.setAlignment(Pos.CENTER);
    countdownLabel.setVisible(false);

    // Ready/Cancel buttons
    readyButton = new Button("Ready");
    readyButton.getStyleClass().add("ready-button");
    readyButton.setVisible(false);

    cancelReadyButton = new Button("Cancel Ready");
    cancelReadyButton.getStyleClass().add("cancel-ready-button");
    cancelReadyButton.setVisible(false);

    // Ready/Cancel stacked
    readyBox = new VBox(10, readyButton, cancelReadyButton);
    readyBox.setAlignment(Pos.CENTER);

    VBox centerContent = new VBox(30, title, rectanglesBox, countdownLabel, readyBox);
    centerContent.setAlignment(Pos.CENTER);

    root = new BorderPane();
    root.getStyleClass().add("background");
    root.setCenter(centerContent);

    mainLayout = new StackPane(root);
  }

  /** Set slot with name and ready state */
  public void setPlayerSlotReady(int index, String playerId, boolean ready) {
    if (index == 0) {
      labelRectangle1.setText(playerId != null ? playerId : "Waiting...");
      rectangle1.setFill(playerId == null ? Color.RED : (ready ? Color.LIMEGREEN : Color.ORANGE));
    } else if (index == 1) {
      labelRectangle2.setText(playerId != null ? playerId : "Waiting...");
      rectangle2.setFill(playerId == null ? Color.RED : (ready ? Color.LIMEGREEN : Color.ORANGE));
    }
  }

  /** Show/hide Ready and Cancel Ready buttons */
  public void showReadyControls(boolean visible) {
    readyButton.setVisible(visible);
    // Only show one at a time by default; cancel will be swapped in setReadyButtonState
    cancelReadyButton.setVisible(false);
  }

  /** Called by controller to toggle which button is active */
  public void setReadyButtonState(boolean isReady) {
    readyButton.setVisible(!isReady);
    cancelReadyButton.setVisible(isReady);
  }

  /** Assign handler for Ready button */
  public void setReadyButtonHandler(javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
    readyButton.setOnAction(handler);
  }
  /** Assign handler for Cancel Ready button */
  public void setCancelReadyButtonHandler(javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
    cancelReadyButton.setOnAction(handler);
  }

  /** Countdown display */
  public void startCountdown(int seconds) {
    countdownLabel.setVisible(true);
    countdownLabel.setText("Starting in " + seconds + "...");
  }
  public void cancelCountdown() {
    countdownLabel.setText("");
    countdownLabel.setVisible(false);
  }

  public void setPlayerSlot(int index, String playerId, boolean connected) {
    if (index == 0) {
      labelRectangle1.setText(playerId != null ? playerId : "Waiting...");
      rectangle1.setFill(connected ? Color.GREEN : Color.RED);
    } else if (index == 1) {
      labelRectangle2.setText(playerId != null ? playerId : "Waiting...");
      rectangle2.setFill(connected ? Color.GREEN : Color.RED);
    }
  }

  /** Set both slots (for convenience) */
  public void setPlayerSlots(String player1, boolean conn1, String player2, boolean conn2) {
    setPlayerSlot(0, player1, conn1);
    setPlayerSlot(1, player2, conn2);
  }

  public Pane getView() {
    return mainLayout;
  }
}
