package model;

/*
Mail arrivano in formato json tramite connessione http, chiedere alla lily se si può usare la libreria
Nel caso link di stackoverflow: https://stackoverflow.com/questions/10926353/how-to-read-json-file-into-java-with-simple-json-library
*/

public class Email {
    private final int id;
    private String sender;
    private String receipient;
    private String subject;
    private String body;

    public Email(int id, String sender, String receipient, String subject, String body) {
        this.id = id;
        this.sender = sender;
        this.receipient = receipient;
        this.subject = subject;
        this.body = body;
    }

    public int getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceipient() {
        return receipient;
    }

    public void setReceipient(String receipient) {
        this.receipient = receipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }



}
