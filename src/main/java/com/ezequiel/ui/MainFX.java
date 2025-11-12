package com.ezequiel.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));

        Parent root = loader.load();

        primaryStage.setTitle("Gym Metrics");
        primaryStage.setScene(new javafx.scene.Scene(root));
        primaryStage.show();
    }
}
