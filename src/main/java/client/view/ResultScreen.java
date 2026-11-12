package client.view;

import client.ScreenManager;
import client.controller.ResultController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class ResultScreen {
  private StackPane mainLayout;

  public ResultScreen(
      ResultController manager,
      boolean won,
      int targetScore,
      int playerScore) {

    // Hauptlayout als StackPane
    mainLayout = new StackPane();

    // Hintergrundbild
    ImageView background =
        new ImageView(
            new Image(
                getClass()
                    .getResource("/images/duolatro-startscreen-background-image.png")
                    .toExternalForm()));
    background.setFitWidth(1280);
    background.setFitHeight(720);
    background.setPreserveRatio(false);

    // Dunkelblauer Hintergrund-Container ("Card")
    BorderPane card = new BorderPane();
    card.setPadding(new Insets(40));
    card.setMaxWidth(1000);
    card.setMaxHeight(600);
    card.getStyleClass().add("result-box");

    // Icon und Titel
    ImageView icon =
        new ImageView(
            new Image(
                getClass().getResourceAsStream(won ? "/images/winner.png" : "/images/loser.png")));
    icon.setFitWidth(150);
    icon.setPreserveRatio(true);

    Label title = new Label(won ? "You win!" : "You lose!");
    title.getStyleClass().add("result-title");

    VBox iconTitleBox = new VBox(10, icon, title);
    iconTitleBox.setAlignment(Pos.CENTER);
    card.setTop(iconTitleBox);
    BorderPane.setAlignment(iconTitleBox, Pos.TOP_CENTER);
    BorderPane.setMargin(iconTitleBox, new Insets(40, 0, 0, 0));

    // Score-Details
    GridPane scoreGrid = new GridPane();
    scoreGrid.setVgap(8);
    scoreGrid.setAlignment(Pos.BOTTOM_LEFT);

    int row = 0;
    addLineToGrid(scoreGrid, row++, "Target score:", String.valueOf(targetScore));
    addLineToGrid(scoreGrid, row++, "Your score:", String.valueOf(playerScore));



    card.setLeft(scoreGrid);
    BorderPane.setAlignment(scoreGrid, Pos.BOTTOM_LEFT);
    BorderPane.setMargin(scoreGrid, new Insets(0, 0, 40, 40));

    // Buttons
    VBox buttonBox = new VBox(15);
    buttonBox.setAlignment(Pos.BOTTOM_RIGHT);

    if (won) {
      Button nextRound = new Button("Start next round");
      nextRound.getStyleClass().add("result-screen-button");
      nextRound.setOnAction(e -> manager.showLobby());
      buttonBox.getChildren().add(nextRound);
    }

    Button quit = new Button("Quit game");
    quit.getStyleClass().add("result-screen-button");
    quit.setOnAction(e -> manager.showStartScreen());
    buttonBox.getChildren().add(quit);

    card.setRight(buttonBox);
    BorderPane.setAlignment(buttonBox, Pos.BOTTOM_RIGHT);
    BorderPane.setMargin(buttonBox, new Insets(0, 40, 40, 0));

    // Layout zusammensetzen
    mainLayout.getChildren().addAll(background, card);
  }

  private void addLineToGrid(GridPane grid, int row, String labelText, String valueText) {
    Label label = new Label(labelText);
    label.getStyleClass().add("result-label");

    Label value = new Label(valueText);
    value.getStyleClass().add("result-value");

    GridPane.setMargin(label, new Insets(5, 10, 5, 0));
    GridPane.setMargin(value, new Insets(5, 0, 5, 10));

    grid.add(label, 0, row); // i = 0 ist dabei die 1. Spalte
    grid.add(value, 1, row); // i = 1 ist dabei die 2. Spalte
  }

  public Pane getView() {
    return mainLayout;
  }
}
