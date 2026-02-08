package com.example.mailclient.network;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private  String host = "127.0.0.1";
    private final int port;
    private final Gson gson = new Gson();

    public Client(int port) {
        this.port = port;
    }

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Response send(Request request) {
        try (Socket socket = new Socket(host, port);
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            output.println(gson.toJson(request));

            String line = input.readLine();
            if (line != null) {
                return gson.fromJson(line, Response.class);
            }

        } catch (IOException e) {
            return new Response(ResponseStatus.ERROR, "Server non raggiungibile: " + e.getMessage());
        }
        return new Response(ResponseStatus.ERROR, "Risposta nulla del server");
    }
}
