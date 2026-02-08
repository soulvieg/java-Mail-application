package com.example.mailserver.network;

public class Request {
    private RequestCommands command;
    private String userEmail;
    private Object payload;


    public Request(RequestCommands command, String userEmail, Object payload) {
        this.command = command;
        this.userEmail = userEmail;
        this.payload = payload;
    }

    public RequestCommands getCommand() {
        return command;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Object getPayload() {
        return payload;
    }
}
