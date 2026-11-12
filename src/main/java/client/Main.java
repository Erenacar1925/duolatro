package client;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
  @Override
  public void start(Stage primaryStage) {
    try {
      NetworkClient networkClient = new NetworkClient("localhost", 8080);
      ScreenManager screenManager = new ScreenManager(primaryStage, networkClient);
      screenManager.showStartScreen();
    } catch (Exception e) {
      e.printStackTrace(); // Print the real cause
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
