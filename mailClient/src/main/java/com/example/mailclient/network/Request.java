package com.example.mailclient.network;

public class Request {
    private final RequestCommands command;
    private final String userEmail;
    private final Object payload;


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
