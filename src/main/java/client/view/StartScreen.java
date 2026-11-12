package client.view;

import client.ScreenManager;
import client.controller.StartController;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class StartScreen {
  private final StartController controller;
  private BorderPane root;
  private StackPane mainLayout;
  private Button playButton;

  public StartScreen(StartController controller) {
    this.controller = controller;

    // Root-Inhalt mit BorderPane
    root = new BorderPane();
    root.getStyleClass().add("background");

    // Titel
    Label title = new Label("DUOLATRO");
    title.getStyleClass().add("title");
    title.setMaxWidth(Double.MAX_VALUE);
    title.setAlignment(Pos.CENTER);

    // Quit-Button
    Button quitButton = new Button("Quit  ✕");
    quitButton.getStyleClass().add("quit-button");
    quitButton.setOnAction(e -> System.exit(0));

    AnchorPane overlay = new AnchorPane(quitButton);
    AnchorPane.setTopAnchor(quitButton, 10.0);
    AnchorPane.setRightAnchor(quitButton, 10.0);
    overlay.setPickOnBounds(false);

    // Kombiniere Quit-Button und Titel oben
    VBox topBox = new VBox(title);
    topBox.setAlignment(Pos.TOP_CENTER);
    topBox.setFillWidth(true);
    root.setTop(topBox);

    // Play-Button
    playButton = new Button("Play");
    playButton.getStyleClass().add("play-button");

playButton.setOnAction(e -> controller.onPlayButtonClicked());

    // Buttons in VBox(aktuell nur Play-Button)
    VBox buttons = new VBox(20, playButton);
    buttons.setAlignment(Pos.CENTER);
    buttons.getStyleClass().add("menu-buttons");

    root.setCenter(buttons);

    // Hauptlayout als StackPane: root + QuitOverlay
    mainLayout = new StackPane(root, overlay);
  }

  public Pane getView() {
    return mainLayout;
  }

  public Button getPlayButton() {
    return playButton;
  }

  public StartController getController() {
    return controller;
  }
}
