package com.example.mailclient.model;

import com.example.mailclient.network.*;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Arrays;

public class ClientModel {
    private final Client client;
    private final ObservableList<Email> inbox = FXCollections.observableArrayList();
    private final ObjectProperty<Email> selectedEmail = new SimpleObjectProperty<>();
    private String userEmail;
    private final Gson gson = new Gson();

    public ClientModel(Client client) {
        this.client = client;
    }

    public ObservableList<Email> getInbox() {
        return inbox;
    }

    public ObjectProperty<Email> selectedEmailProperty() {
        return selectedEmail;
    }

    public void setSelectedEmail(Email email) {
        this.selectedEmail.set(email);
    }

    public String getUserEmail() {
        return userEmail;
    }


    public ResponseStatus login(String email) {
        Request request = new Request(RequestCommands.LOGIN, email, null);
        Response response = client.send(request);

        if (response.getStatus() == ResponseStatus.SUCCESS) {
            this.userEmail = email;

            List<Email> serverMails = convertPayloadToEmailList(response.getPayload());


            Platform.runLater(() -> {
                inbox.clear();
                inbox.addAll(serverMails);
            });
        }
        return response.getStatus();
    }

    public void refreshInbox() {
        if (userEmail == null) return;

        int lastId = inbox.stream()
                .mapToInt(Email::getId)
                .max()
                .orElse(0);

        Request req = new Request(RequestCommands.UPDATE_INBOX, userEmail, lastId);
        Response resp = client.send(req);

        if (resp.getStatus() == ResponseStatus.SUCCESS && resp.getPayload() != null) {
            List<Email> newMails = convertPayloadToEmailList(resp.getPayload());

            if (!newMails.isEmpty()) {
                Platform.runLater(() -> inbox.addAll(newMails));
                System.out.println("Downloaded " + newMails.size() + " new emails.");
            }
        }
    }

    public Response sendEmail(List<String> recipients, String subject, String body) {
        Email newEmail = new Email(0, userEmail, recipients, subject, body);
        Request req = new Request(RequestCommands.SEND_MAIL, userEmail, newEmail);
        return client.send(req);
    }

    public void deleteEmail(int id) {
        Request req = new Request(RequestCommands.DELETE_MAIL, userEmail, id);
        Response resp = client.send(req);

        if (resp.getStatus() == ResponseStatus.SUCCESS) {
            Platform.runLater(() ->
                    inbox.removeIf(email -> email.getId() == id)
            );
        } else {
            System.err.println("ERROR couldn't delete email with " + id + ": " + resp.getMessage());
        }
    }

    private List<Email> convertPayloadToEmailList(Object payload) {
        if (payload == null) return List.of();

        String json = gson.toJson(payload);
        Email[] emailArray = gson.fromJson(json, Email[].class);

        return Arrays.asList(emailArray);
    }
}
