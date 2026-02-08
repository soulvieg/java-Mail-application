package com.example.mailserver.controller;

import com.example.mailserver.model.LogType;
import com.example.mailserver.model.Logger;
import com.example.mailserver.network.Server;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;


public class ServerController {


    @FXML
    private ListView<Logger> logList;

    @FXML
    private Button btnStart;
    @FXML
    private Button btnStop;

    private Server server;


    public void initialize() {
        logList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Logger item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.toString());


                    if (item.getType() == LogType.ERROR) {
                        setTextFill(Color.RED);
                        setStyle("-fx-font-weight: bold;");
                    } else {
                        setTextFill(Color.BLACK);
                        setStyle("");
                    }
                }
            }
        });
    }

    @FXML
    public void onStartClick() {
        server = new Server(8189, (log) -> {
            Platform.runLater(() -> {
                logList.getItems().add(log);
                logList.scrollTo(logList.getItems().size() - 1);
            });
        });
        server.start();
        btnStart.setDisable(true);
        btnStop.setDisable(false);
    }

    @FXML
    public void onStopClick() {
        if (server != null) {
            server.stop();
            server = null;
        }
        btnStart.setDisable(false);
        btnStop.setDisable(true);
    }

    public void shutdown() {
        if (server != null) {
            server.stop();
        }
    }
}


