package com.example.mailserver;

import com.example.mailserver.controller.ServerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("server-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);


        ServerController controller = fxmlLoader.getController();
        stage.setOnCloseRequest(event -> controller.shutdown());

        stage.setTitle("Email server admin, at your service!!!");
        stage.setScene(scene);
        stage.show();
    }
}
