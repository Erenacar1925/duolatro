package client;

import client.controller.GameController;
import client.controller.LobbyController;
import client.controller.ResultController;
import client.controller.StartController;
import client.view.GameScreen;
import client.view.Lobby;
import client.view.ResultScreen;
import client.view.StartScreen;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScreenManager {
  private final Stage primaryStage;
  private final NetworkClient networkClient;

  public ScreenManager(Stage primaryStage, NetworkClient networkClient) {
    this.primaryStage = primaryStage;
    this.networkClient = networkClient;
  }

  public void showStartScreen() {
    // Create controller for StartScreen
    StartController startController = new StartController(networkClient, this);
    StartScreen startScreen = new StartScreen(startController);
    Scene scene = new Scene(startScreen.getView(), 1280, 720);
    scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public void showGameScreen() {
    GameScreen gameScreen = new GameScreen(this); // pass ScreenManager if needed

    Scene scene = new Scene(gameScreen.getView(), 1280, 720);
    scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.show();
    GameController gameController = new GameController(networkClient, this, gameScreen);
    gameScreen.setController(gameController); // So view can call controller methods
  }

public void showLobbyScreen() {
    // Create controller for LobbyScreen
  Lobby lobbyScreen = new Lobby(); // No controller argument!
  LobbyController lobbyController = new LobbyController(networkClient, this, lobbyScreen);
  Scene scene = new Scene(lobbyScreen.getView(), 1280, 720);
  scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
    primaryStage.setScene(scene);
  }
  public void showResultScreen(
          boolean won,
          int targetScore,
          int playerScore,
          int bank,
          int winAmount
  ) {
    ResultController resultController = new ResultController(this);
    ResultScreen resultScreen = new ResultScreen(resultController, won, targetScore, playerScore);

    Scene scene = new Scene(resultScreen.getView(), 1280, 720);
    scene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
    primaryStage.setScene(scene);
  }
  // Add similar methods for GameScreen, ResultScreen, etc.
}