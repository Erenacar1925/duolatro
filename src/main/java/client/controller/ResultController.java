package client.controller;

import client.ScreenManager;

public class ResultController {
    private final ScreenManager manager;

    public ResultController(ScreenManager manager) {
        this.manager = manager;
    }
    public void showLobby() {
        manager.showLobbyScreen();
    }
    public void showStartScreen() {
        manager.showStartScreen();
    }
}