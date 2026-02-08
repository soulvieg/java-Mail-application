package com.example.mailserver.network;

import com.example.mailserver.model.Email;
import com.example.mailserver.model.JSONManager;
import com.example.mailserver.model.LogType;
import com.example.mailserver.model.Logger;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

//Handle the Request from the client
public class RequestHandler implements  Runnable {

    private final Socket clientSocket;
    private final JSONManager jsonManager;
    private final Gson gson;
    private final Consumer<Logger> logger;

    public RequestHandler(Socket socket, JSONManager jsonManager, Consumer<Logger> logger) {
        this.clientSocket = socket;
        this.jsonManager = jsonManager;
        this.logger = logger;
        this.gson = new Gson();
    }

    private void log(String message, LogType type) {
        Logger log = new Logger(type, message);


        if (type == LogType.ERROR) {
            System.err.println(log);
        }
        else {
            System.out.println(log);
        }

        if (logger != null) {
            logger.accept(log);
        }
    }

    @Override
    public void run() {
        try (
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String requestJson = input.readLine();
            String clientIP = clientSocket.getInetAddress().toString();
            if (requestJson != null) {

                System.out.println("RICEVUTO: " + requestJson);

                Request request = gson.fromJson(requestJson, Request.class);

                String user_email = (request.getUserEmail() != null) ? request.getUserEmail() : "Utente non riconosciuto";
                log("[" + clientIP + "] Richiesta: " + request.getCommand() + " da " + user_email, LogType.INFO);

                Response response = processRequest(request);


                output.println(gson.toJson(response));

                System.out.println("RISPOSTA: " + gson.toJson(response));
            }
        } catch (Exception e) {
            System.err.println("Errore nella gestione della richiesta: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Errore chiusura socket: " + e.getMessage());
            }
        }
    }


    private Response processRequest(Request request) {
        RequestCommands command = request.getCommand();

        return switch (command) {
            case LOGIN -> handleLogin(request);
            case SEND_MAIL -> handleSendMail(request);
            case UPDATE_INBOX -> handleUpdateInbox(request);
            case DELETE_MAIL -> handleDeleteMail(request);
            default -> new Response(ResponseStatus.ERROR, "Comando non riconosciuto.");
        };
    }

    private Response handleLogin(Request req) {
        if (!jsonManager.userExists(req.getUserEmail())) {
            log("Login fallito: l'utente " + req.getUserEmail() + " non esiste.", LogType.ERROR);
            return new Response(ResponseStatus.USER_NOT_FOUND, "Utente non trovato o non registrato");
        }

        List<Email> mailbox = jsonManager.loadMailbox(req.getUserEmail());
        log("Login effettuato con successo per: " + req.getUserEmail(), LogType.INFO);
        return new Response(ResponseStatus.SUCCESS, "Login OK", mailbox);
    }

    private Response handleSendMail(Request req) {
        try {
            String tempJson = gson.toJson(req.getPayload());
            Email emailData = gson.fromJson(tempJson, Email.class);

            int newId = jsonManager.generateNewId();

            Email emailToSend = new Email(
                    newId,
                    req.getUserEmail(),
                    emailData.getReceipientList(),
                    emailData.getSubject(),
                    emailData.getBody()
            );

            List<String> validRecipients = new ArrayList<>();
            for (String recipient : emailToSend.getReceipientList()) {
                List<Email> recipientInbox = jsonManager.loadMailbox(recipient);
                recipientInbox.add(emailToSend);
                jsonManager.saveMailbox(recipient, recipientInbox);
                validRecipients.add(recipient);
            }

            List<Email> senderMailbox = jsonManager.loadMailbox(req.getUserEmail());
            senderMailbox.add(emailToSend);
            jsonManager.saveMailbox(req.getUserEmail(), senderMailbox);

            log("Email inviata da " + req.getUserEmail() + " a " + validRecipients, LogType.INFO);

            return new Response(ResponseStatus.SUCCESS, "Email inviata", validRecipients);
        } catch (Exception e) {
            log("Errore durante l'invio mail da " + req.getUserEmail() + ": " + e.getMessage(), LogType.ERROR);
            return new Response(ResponseStatus.ERROR, "Errore invio: " + e.getMessage());
        }
    }

    private Response handleUpdateInbox(Request req) {
        List<Email> allMails = jsonManager.loadMailbox(req.getUserEmail());
        if (req.getPayload() != null) {
            try {
                double lastIdDouble = (double) req.getPayload();
                int lastId = (int) lastIdDouble;
                List<Email> newMails = allMails.stream()
                        .filter(e -> e.getId() > lastId)
                        .toList();

                if (!newMails.isEmpty()) {
                    log("Trovate " + newMails.size() + " nuove mail per " + req.getUserEmail(), LogType.INFO);
                }
                return new Response(ResponseStatus.SUCCESS, "Nuove mail", newMails);
            } catch (Exception e) {
                log("Errore parsing ID per update inbox di " + req.getUserEmail() + ". Eseguo full sync.", LogType.ERROR);
                return new Response(ResponseStatus.SUCCESS, "Full sync", allMails);
            }
        }
        return new Response(ResponseStatus.SUCCESS, "Inbox completa", allMails);
    }

    private Response handleDeleteMail(Request req) {
        try {
            double idDouble = (double) req.getPayload();
            int idToDelete = (int) idDouble;

            log("Richiesta cancellazione mail ID " + idToDelete + " da parte di " + req.getUserEmail(), LogType.INFO);

            List<Email> mailbox = jsonManager.loadMailbox(req.getUserEmail());
            boolean removed = mailbox.removeIf(e -> e.getId() == idToDelete);

            if (removed) {
                jsonManager.saveMailbox(req.getUserEmail(), mailbox);
                log("Mail ID " + idToDelete + " eliminata correttamente.", LogType.INFO);
                return new Response(ResponseStatus.SUCCESS, "Cancellata");
            } else {
                log("Impossibile eliminare mail ID " + idToDelete + ": non trovata.", LogType.ERROR);
                return new Response(ResponseStatus.ERROR, "Mail non trovata");
            }
        } catch (Exception e) {
            log("Errore durante la cancellazione mail per " + req.getUserEmail() + ": " + e.getMessage(), LogType.ERROR);
            return new Response(ResponseStatus.ERROR, "Errore cancellazione");
        }
    }
}


