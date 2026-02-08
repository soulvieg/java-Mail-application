package com.example.mailserver.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class JSONManager {

    private final String DATA_DIR = "src/main/resources/com/example/mailserver/server_data/";
    private final Gson gson;
    private final ConcurrentHashMap<String, ReentrantLock> userLocks = new ConcurrentHashMap<>();
    private final AtomicInteger countId;

    public JSONManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdir();
        int startId = getMaxIdFound();
        this.countId = new AtomicInteger(startId);
    }

    public int generateNewId() {
        return countId.incrementAndGet();
    }

    private ReentrantLock getLock(String email) {
        return userLocks.computeIfAbsent(email, k -> new ReentrantLock());
    }


    public List<Email> loadMailbox(String email) {
        ReentrantLock lock = getLock(email);
        lock.lock();
        try {
            File file = new File(DATA_DIR + email + ".json");
            if (!file.exists()) return new ArrayList<>();

            try (Reader reader = new FileReader(file)) {
                Email[] emails = gson.fromJson(reader, Email[].class);
                return emails != null ? new ArrayList<>(Arrays.asList(emails)) : new ArrayList<>();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return new ArrayList<>();
            }
        } finally {
            lock.unlock();
        }
    }


    public void saveMailbox(String email, List<Email> emails) {
        ReentrantLock lock = getLock(email);
        lock.lock();
        try {
            File file = new File(DATA_DIR + email + ".json");
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(emails, writer);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } finally {
            lock.unlock();
        }
    }


    private int getMaxIdFound() {
        int max = 0;
        File dir = new File(DATA_DIR);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));

        if (files != null) {
            for (File f : files) {
                List<Email> list = loadMailbox(f.getName().replace(".json", ""));
                for (Email e : list) {
                    if (e.getId() > max) max = e.getId();
                }
            }
        }
        return max;
    }

    public boolean userExists(String email) {
        File file = new File(DATA_DIR + email + ".json");
        return file.exists();
    }
}
