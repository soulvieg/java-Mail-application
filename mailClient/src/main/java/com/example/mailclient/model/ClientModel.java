package com.example.mailclient.model;

import com.example.mailclient.network.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class ClientModel {
    private final Client client;
    private final ObservableList<Email> inbox = FXCollections.observableArrayList();
    private final ObjectProperty<Email> selectedEmail = new SimpleObjectProperty<>();
    private String userEmail;

    public ClientModel(Client client) {
        this.client = client;
    }

    public ObservableList<Email> getInbox() { return inbox; }


    public ObjectProperty<Email> selectedEmailProperty() { return selectedEmail; }
    public void setSelectedEmail(Email email) { this.selectedEmail.set(email); }

    public Response sendEmail(List<String> recipients, String subject, String body) {
        Email newEmail = new Email(0, userEmail, recipients, subject, body);
        Request req = new Request(RequestCommands.SEND_MAIL, userEmail, newEmail);
        return client.send(req);
    }

    public ResponseStatus login(String email) {
        return null;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
