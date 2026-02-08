package com.example.mailclient.controller;

import com.example.mailclient.model.ClientModel;
import com.example.mailclient.network.ResponseStatus;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private VBox pane;
    @FXML
    private TextField emailField;
    @FXML
    private Label errorLabel;

    private ClientModel model;

    public void initModel(ClientModel model) {
        this.model = model;
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            errorLabel.setText("Enter your email address");
            return;
        }


        ResponseStatus status = model.login(email);

        if (status == ResponseStatus.SUCCESS) {
            switchToClientView();
        } else if (status == ResponseStatus.USER_NOT_FOUND) {
            errorLabel.setText("User not found");
        } else {
            errorLabel.setText("Server is unavailable");
        }
    }

    private void switchToClientView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client-view.fxml"));
            Parent root = loader.load();

            ClientController controller = loader.getController();
            controller.initModel(this.model);

            Stage mainStage = new Stage();
            mainStage.setTitle("THE MAIL - " + model.getUserEmail());
            mainStage.setScene(new Scene(root));
            mainStage.show();


            Stage loginStage = (Stage) pane.getScene().getWindow();
            loginStage.close();

        } catch (IOException e) {
            System.err.println(e.getMessage());
            errorLabel.setText("ERROR: "+ e.getMessage());
        }
    }
}
