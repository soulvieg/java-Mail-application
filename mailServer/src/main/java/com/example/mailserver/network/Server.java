package com.example.mailserver.network;


import com.example.mailserver.model.JSONManager;
import com.example.mailserver.model.LogType;
import com.example.mailserver.model.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


public class Server {

    private final int port;
    private final ExecutorService executors;
    private final JSONManager jsonManager;
    private volatile boolean isRunning;
    private ServerSocket socket;
    private final Consumer<Logger> logger;

    public Server(int port, Consumer<Logger> logger) {
        this.port = port;
        this.jsonManager = new JSONManager();
        this.executors = Executors.newCachedThreadPool();
        this.isRunning = false;
        this.logger = logger;
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

    private void loggerWrapper(Logger log){
        if(logger != null){
            logger.accept(log);
        }
    }

    public void start() {
        this.isRunning = true;
        log("Avvio del server in corso...", LogType.INFO);

        new Thread(() -> {
            try{
                socket = new ServerSocket(port);
                log("Mail Server avviato e in ascolto sulla porta " + port, LogType.INFO);
                while (isRunning) {
                    try{
                        Socket clientSocket = socket.accept();
                        log("Nuova connessione da: " + clientSocket.getInetAddress(), LogType.INFO);

                        executors.execute(new RequestHandler(clientSocket, jsonManager, this.logger));
                    }catch(IOException ex){
                        if(isRunning){
                            log("Impossibile aprire il server sulla porta "+port+ ": "+ ex.getMessage(), LogType.ERROR);
                        }
                    }
                }
            } catch (IOException e) {
                if (isRunning) {
                    log("Errore di connessione: " + e.getMessage(), LogType.ERROR);
                }
            } finally {
                stop();
            }
        }).start();
    }

    public void stop() {
        if (!isRunning && (socket == null || socket.isClosed())) return;

        this.isRunning = false;

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            log("Errore durante la chiusura socket: " + e.getMessage(), LogType.ERROR);
        }
        executors.shutdown();
        log("Server arrestato correttamente.", LogType.INFO);
    }

}
