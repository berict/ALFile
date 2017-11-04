package com.berict.alfile;

import com.berict.alfile.controller.AlFileController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
//        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
//        primaryStage.setTitle("AlFile");
//        primaryStage.setScene(new Scene(root, 1280, 720));
//        primaryStage.show();

        AlFileController controller = new AlFileController();
        controller.showWindow();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
