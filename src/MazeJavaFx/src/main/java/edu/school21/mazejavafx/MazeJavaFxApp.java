package edu.school21.mazejavafx;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MazeJavaFxApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MazeJavaFxApp.class.getResource("maze-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1015, 581);
        stage.setTitle("Maze");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}