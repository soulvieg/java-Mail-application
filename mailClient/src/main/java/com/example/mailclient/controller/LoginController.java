package com.example.mailclient.controller;

import com.example.mailclient.ClientApplication;
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
import java.util.regex.Pattern;

public class LoginController {

    @FXML
    private VBox pane;
    @FXML
    private TextField emailField;
    @FXML
    private Label errorLabel;

    private ClientModel model;
    private static final String regexEmail = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";

    public void initModel(ClientModel model) {
        this.model = model;
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();


        if (email.isEmpty()) {
            errorLabel.setText("Per favore, inserisci un indirizzo email.");
            return;
        }


        if (!isValidEmail(email)) {
            errorLabel.setText("Formato non valido! Usa: user@domain.ext");
            return;
        }

        errorLabel.setText("Connessione in corso...");

        ResponseStatus status = model.login(email);

        switch (status) {
            case SUCCESS -> switchToClientView();
            case USER_NOT_FOUND -> errorLabel.setText("Utente non registrato o inesistente.");
            case ERROR -> errorLabel.setText("Errore di comunicazione col server.");
            default -> errorLabel.setText("Errore sconosciuto.");
        }
    }

    private boolean isValidEmail(String email) {
        return Pattern.matches(regexEmail, email);
    }

    private void switchToClientView() {
        try {
            FXMLLoader loader = new FXMLLoader(ClientApplication.class.getResource("client-view.fxml"));
            Parent root = loader.load();

            ClientController clientController = loader.getController();
            clientController.initModel(this.model);

            Stage stage = new Stage();
            stage.setTitle("THE MAIL - " + model.getUserEmail());
            stage.setScene(new Scene(root));
            stage.show();

            Stage loginStage = (Stage) pane.getScene().getWindow();
            loginStage.close();

        } catch (IOException e) {
            System.err.println(e.getMessage());
            errorLabel.setText("Errore critico: impossibile caricare l'interfaccia client.");
        }
    }
}
