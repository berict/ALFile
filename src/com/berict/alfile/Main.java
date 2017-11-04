package com.berict.alfile;

import com.berict.alfile.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        // uncomment this to load jfoenix
//        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
//        primaryStage.setTitle("AlFile");
//        primaryStage.setScene(new Scene(root, 800, 600));
//        primaryStage.show();

        MainController mainController = new MainController();
        mainController.setVisible(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
