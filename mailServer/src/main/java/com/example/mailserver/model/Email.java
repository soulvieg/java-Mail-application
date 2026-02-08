package com.example.mailserver.model;

import java.util.ArrayList;
import java.util.List;

public class Email {
    private final int id;
    private String sender;
    private List<String> recipients;
    private String subject;
    private String body;



    public Email(){
        this.id = 0;
        this.sender = "";
        this.recipients = new ArrayList<>();
        this.subject = "";
        this.body = "";
    }
    public Email(int id, String sender, List<String> recipients, String subject, String body) {
        this.id = id;
        this.sender = sender;
        this.recipients = recipients;
        this.subject = subject;
        this.body = body;
    }


    public Email(int id, String sender, String receipient, String subject, String body) {
        this.id = id;
        this.sender = sender;
        this.recipients = new ArrayList<String>();
        this.recipients.add(receipient);
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

    public List<String> getReceipientList() {
        return recipients;
    }

    public void setRecipientList(List<String> receipient) {
        this.recipients = receipient;
    }

    public void addRecipient(String receipient) {
        recipients.add(receipient);}

    public void removeRecipient(String receipient) {
        recipients.remove(receipient); }

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
