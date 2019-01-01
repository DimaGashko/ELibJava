package com;

import com.windows.IndexWindow;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class ELib extends Application {
    private IndexWindow indexWindow;

    @Override
    public void start(Stage primaryStage) throws IOException {
        indexWindow = new IndexWindow(primaryStage);
        indexWindow.getWindow().show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
