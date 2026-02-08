import com.example.mailserver.model.Email;
import com.example.mailserver.network.Request;
import com.example.mailserver.network.RequestCommands;
import com.example.mailserver.network.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestClient {

    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 8189;
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        System.out.println("--- AVVIO TEST CLIENT ---");

        // 1. CREAZIONE UTENTI (Simuliamo un admin che scrive a Mario e Luigi per creare i loro file)
        System.out.println("\n[1] Inizializzazione utenti (Admin -> Mario, Admin -> Luigi)...");
        sendMail("admin@gmail.com", "mario@gmail.com", "Benvenuto Mario", "Casella creata.");
        sendMail("admin@gmail.com", "luigi@gmail.com", "Benvenuto Luigi", "Casella creata.");

        // 2. LOGIN MARIO
        System.out.println("\n[2] Login Mario...");
        Response loginMario = sendRequest(new Request(RequestCommands.LOGIN, "mario@gmail.com", null));
        printResponse(loginMario);

        // 3. INVIO MAIL (Mario -> Luigi e Mario -> Mario)
        System.out.println("\n[3] Mario invia una mail a Luigi (e in copia a se stesso)...");
        List<String> recipients = Arrays.asList("luigi@gmail.com", "mario@gmail.com");

        // Nota: ID è 0 perché lo decide il server. Mittente è ignorato dal server (usa quello della Request), ma lo mettiamo per coerenza.
        Email email = new Email(0, "mario@gmail.com", recipients, "Prova Progetto", "IT's a me Mario");

        Response sendRes = sendRequest(new Request(RequestCommands.SEND_MAIL, "mario@gmail.com", email));
        printResponse(sendRes);

        // 4. VERIFICA INBOX LUIGI (Dovrebbe vedere la mail di Admin e quella di Mario)
        System.out.println("\n[4] Login Luigi (Controllo posta in arrivo)...");
        Response loginLuigi = sendRequest(new Request(RequestCommands.LOGIN, "luigi@gmail.com", null));
        printResponse(loginLuigi);

        // 5. CANCELLAZIONE MAIL (Opzionale: Prova a cancellare la mail con ID più alto trovata da Luigi)
        if (loginLuigi != null && loginLuigi.getPayload() != null) {
            System.out.println("\n[5] Test Cancellazione (Luigi prova a cancellare l'ultima mail)...");
            // Rileggiamo il payload come lista di email
            String jsonPayload = gson.toJson(loginLuigi.getPayload());
            Email[] emails = gson.fromJson(jsonPayload, Email[].class);

            if (emails.length > 0) {
                int idToDelete = emails[emails.length - 1].getId();
                System.out.println("   -> Tentativo cancellazione ID: " + idToDelete);
                Response delRes = sendRequest(new Request(RequestCommands.DELETE_MAIL, "luigi@gmail.com", (double)idToDelete)); // Gson usa Double per i numeri
                printResponse(delRes);
            }
        }

        System.out.println("\n--- TEST TERMINATO ---");
    }


    private static void sendMail(String sender, String recipient, String subject, String body) {
        Email email = new Email(0, sender, Collections.singletonList(recipient), subject, body);
        Request req = new Request(RequestCommands.SEND_MAIL, sender, email);
        Response res = sendRequest(req);
        System.out.println("   -> Init mail inviata a " + recipient + ": " + (res != null ? res.getMessage() : "Errore"));
    }


    private static Response sendRequest(Request request) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {


            String jsonRequest = gson.toJson(request);
            out.println(jsonRequest);


            String jsonResponse = in.readLine();
            if (jsonResponse != null) {
                return gson.fromJson(jsonResponse, Response.class);
            }

        } catch (IOException e) {
            System.err.println("ERRORE DI CONNESSIONE: " + e.getMessage());
            System.err.println("Assicurati che il server sia avviato sulla porta " + SERVER_PORT);
        }
        return null;
    }

    private static void printResponse(Response res) {
        if (res == null) return;
        System.out.println("   STATUS: " + res.getStatus());
        System.out.println("   MESSAGE: " + res.getMessage());
        if (res.getPayload() != null) {
            // Stampiamo il payload formattato bene
            System.out.println("   PAYLOAD: " + gson.toJson(res.getPayload()));
        }
    }
}