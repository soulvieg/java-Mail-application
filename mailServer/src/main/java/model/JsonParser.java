package model;

import com.google.gson.Gson;

/* TODO:
 * Definire struttura del file json che contiene tutte le info e del singolo utente (seguire struttura email)
 * Fare metodo per trasformare mail in  json format
 * Fare metodo per trasformare una mail in formato json nell'oggetto Email
 * Fare metodo che restituisce dato un json un nuovo json con tutte le informazioni solo di un utente
 * Fare metodo che dato un file json di un solo utente in entrata faccia un update al file json con tutti gli utenti
*/
public class JsonParser {
    private Gson parser;

    public JsonParser(){
        parser = new Gson();
    }


}
