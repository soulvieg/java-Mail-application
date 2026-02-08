package com.example.mailserver.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private final String msg;
    private final LogType type;
    private final LocalDateTime timestamp;

    private static final DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public Logger(LogType type, String msg) {
        this.type = type;
        this.msg = msg;
        this.timestamp = LocalDateTime.now();
    }

    public String getMsg() {
        return msg;
    }

    public LogType getType() {
        return type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        String prefix = (type == LogType.ERROR) ? "ERROR" : "INFO";
        return prefix + " [" +timestamp.format(formatter) + "] " + msg;
    }
}
