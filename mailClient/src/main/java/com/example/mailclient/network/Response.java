package com.example.mailclient.network;

public class Response{
    private final ResponseStatus status;
    private final String message;
    private final Object payload;


    public Response(ResponseStatus status, String message) {
        this.status = status;
        this.message = message;
        this.payload = null;
    }

    public Response(ResponseStatus status, String message, Object payload) {
        this.status = status;
        this.message = message;
        this.payload = payload;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getPayload() {
        return payload;
    }
}
