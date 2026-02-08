package com.example.mailclient;

import com.example.mailclient.controller.LoginController;
import com.example.mailclient.model.ClientModel;
import com.example.mailclient.network.Client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientApplication extends Application {

    private ScheduledExecutorService scheduler;

    @Override
    public void start(Stage stage) throws IOException {

        Client client = new Client("127.0.0.1", 8189);
        ClientModel model = new ClientModel(client);


        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        LoginController controller = fxmlLoader.getController();
        controller.initModel(model);

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            if (model.getUserEmail() != null) {
                model.refreshInbox();
            }
        }, 5, 30, TimeUnit.SECONDS);


        stage.setTitle("THE MAIL!!!");
        stage.setScene(scene);
        stage.show();
    }


    @Override
    public void stop() throws Exception {
        super.stop();
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
        System.out.println("App closed successfully");
    }
}
