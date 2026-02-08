package com.example.mailclient.controller;

import com.example.mailclient.model.ClientModel;
import com.example.mailclient.model.Email;
import com.example.mailclient.network.Response;
import com.example.mailclient.network.ResponseStatus;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClientController {

    @FXML
    private Label userEmailLabel;

    @FXML
    private ListView<Email> emailListView;


    @FXML
    private VBox readPane;

    @FXML
    private Label detailSubject;

    @FXML
    private Label detailSender;

    @FXML
    private TextArea detailBody;


    @FXML
    private VBox writePane;

    @FXML
    private TextField writeRecipient;

    @FXML
    private TextField writeSubject;

    @FXML
    private TextArea writeBody;


    @FXML
    private Label placeholderLabel;

    private FilteredList<Email> filteredEmailList;

    @FXML private ToggleButton btnInbox;
    @FXML private ToggleButton btnSent;
    private ToggleGroup viewGroup;

    private ClientModel model;


    public void initModel(ClientModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model already initialized");
        }
        this.model = model;


        userEmailLabel.setText(model.getUserEmail());
        filteredEmailList = new FilteredList<>(model.getInbox(), p -> true);
        emailListView.setItems(filteredEmailList);

        showInbox();

        viewGroup = new ToggleGroup();
        btnInbox.setToggleGroup(viewGroup);
        btnSent.setToggleGroup(viewGroup);
        btnInbox.setSelected(true);


        emailListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Email email, boolean empty) {
                super.updateItem(email, empty);
                if (empty || email == null) {
                    setText(null);
                } else {
                    setText(email.getSender() + " - " + email.getSubject());
                }
            }
        });

        emailListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            model.setSelectedEmail(newSelection);
            if (newSelection != null) {
                populateReadPane(newSelection);
                updateView(ViewMode.READING);
            }
        });


        updateView(ViewMode.EMPTY);
    }

    @FXML
    private void showInbox() {
        filteredEmailList.setPredicate(email ->
                !email.getSender().equals(model.getUserEmail())
        );
        updateView(ViewMode.EMPTY);
    }

    @FXML
    private void showSent() {
        filteredEmailList.setPredicate(email ->
                email.getSender().equals(model.getUserEmail())
        );
        updateView(ViewMode.EMPTY);
    }

    @FXML
    private void refreshInbox() {
        if (model != null) {
            model.refreshInbox();
        }
    }

    @FXML
    private void showWritePane() {
        emailListView.getSelectionModel().clearSelection();
        clearWriteFields();
        updateView(ViewMode.WRITING);
    }

    @FXML
    private void hideWritePane() {
        updateView(ViewMode.EMPTY);
    }

    @FXML
    private void handleSend() {
        String recipientsRaw = writeRecipient.getText();
        String subject = writeSubject.getText();
        String body = writeBody.getText();

        if (recipientsRaw.isEmpty() || subject.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing fields", "Enter recipients and object.");
            return;
        }

        List<String> recipientList = Arrays.stream(recipientsRaw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        Response response = model.sendEmail(recipientList, subject, body);

        if (response.getStatus() == ResponseStatus.SUCCESS) {
            showAlert(Alert.AlertType.INFORMATION, "Successo", "Email inviata correttamnte.");
            clearWriteFields();
            updateView(ViewMode.EMPTY);
        } else {
            showAlert(Alert.AlertType.ERROR, "Errore Invio", "Errore: " + response.getMessage());
        }
    }


    @FXML
    private void handleForward() {
        Email selected = model.selectedEmailProperty().get();
        if (selected == null) return;

        showWritePane();

        writeSubject.setText("Fwd: " + selected.getSubject());

        String forwardedBody = "\n\n" +
                "---------- Forwarded message ----------\n" +
                "From: " + selected.getSender() + "\n" +
                "Subject: " + selected.getSubject() + "\n\n" +
                selected.getBody();

        writeBody.setText(forwardedBody);
        writeRecipient.clear();
    }

    @FXML
    private void handleReply() {
        Email selected = model.selectedEmailProperty().get();
        if (selected == null) return;

        showWritePane();

        if (selected.getSender().equals(model.getUserEmail())) {
            writeRecipient.setText(String.join(", ", selected.getReceipientList()));
        } else {
            writeRecipient.setText(selected.getSender());
        }

        String subject = selected.getSubject();
        if (!subject.startsWith("Re:")) {
            subject = "Re: " + subject;
        }
        writeSubject.setText(subject);

        String replyHeader = String.format(
                "\n\n\n---------- Response a %s ----------\nWritten the: %s\n\n",
                selected.getSender(),
                "TIMESTAMP"
        );

        writeBody.setText(replyHeader + selected.getBody());


        writeBody.positionCaret(0);
    }

    @FXML
    private void handleDelete() {
        Email selected = model.selectedEmailProperty().get();
        if (selected != null) {
            model.deleteEmail(selected.getId());
            updateView(ViewMode.EMPTY);
            showAlert(Alert.AlertType.INFORMATION, "Deleted", "Email deleted.");
        }
    }


    private void populateReadPane(Email email) {
        detailSubject.setText(email.getSubject());
        detailSender.setText(email.getSender());
        detailBody.setText(email.getBody());
    }

    private void clearWriteFields() {
        writeRecipient.clear();
        writeSubject.clear();
        writeBody.clear();
    }

    private void updateView(ViewMode mode) {
        readPane.setVisible(false);
        writePane.setVisible(false);
        placeholderLabel.setVisible(false);

        switch (mode) {
            case READING -> readPane.setVisible(true);
            case WRITING -> writePane.setVisible(true);
            case EMPTY -> placeholderLabel.setVisible(true);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}