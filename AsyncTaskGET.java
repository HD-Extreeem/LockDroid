package com.hellomicke89gmail.projektsmartlock;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * Author Mikael, Hadi
 * Klassen Gör en anslutning till en https server för att sedan hämta in listan med idn,namn på idn och dörrstatus
 */
public class AsyncTaskGET extends AsyncTask<Void, Void, Integer> {
    private Controller controller;
    private HashMap<String, String> idNameMap = new HashMap<>();
    private HashMap<String, Boolean> idMap = new HashMap<>();
    private String authString;
    private Boolean doorStatus;

    /**
     * @param controller skickar med instans till controller klassen
     * @param authString skickar med authstring för att göra en anslutning
     */
    AsyncTaskGET(Controller controller, String authString) {
        this.controller = controller;
        this.authString = authString;
    }

    /**
     * Metoden gör en anslutning till en HTTPS server för att kunna mottaga data från servern
     * Ifall anslutningen var lyckad, läser vi in JSONsträngen och skickar vidare den och sätter resultat
     * Annars sätts resultatet till 0
     *
     * @param params
     * @return resultat vilket är en 1 (ifall anslutningen gick) eller 0 ifall anslutningen misslyckades
     */
    @Override
    protected Integer doInBackground(Void... params) {

        Integer result = 0;
        HttpURLConnection urlConnection;

        try {

            System.out.println("Base64 encoded auth string: " + authString);

            URL url = new URL("https://" + authString + "@lockdroid.se/admin");
            urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setRequestProperty("Authorization", "Basic " + authString);
            urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();

            if (statusCode == 200) {
                BufferedReader read = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = read.readLine()) != null) {
                    response.append(line);
                }
                parseResult(response.toString());

                result = 1;
            } else {

                result = 0;
            }
            urlConnection.disconnect();
        } catch (Exception e) {
            Log.v("ASYNCTASKGET", e.toString());
        }
        return result;
    }

    /**
     * Kollar ifall anslutningen gick för att skicka listan vidare till controllern
     * Sätter dörrstatus ifall öppen/stängd
     * Annars så poppar det ut ett meddelande "failed to get id"
     *
     * @param result resultatet på ifall anslutningen gick eller inte
     */
    @Override
    protected void onPostExecute(Integer result) {
        if (result == 1) {
            controller.updateIdAdapter(idMap, idNameMap);
            controller.setDoorBoolean(doorStatus);
        } else {
            controller.errorToast("Failed to get id");
        }
    }

    /*
    Metoden tar emot en JSON sträng som mottagits från servern,för att sedan dela upp den och läsas in
    Strängen delas upp i JSON-objekt med rfidmap och idnamemap
     */
    private void parseResult(String result) {
        try {
            //Tömmer id-listan för att läsa in nya värden från servern
            idMap.clear();

            //Lägger in strängen i ett JSON-objekt
            JSONObject response = new JSONObject(result);
            //Bryter ut JSON objektet vid rfidMap till ett nytt JSONobjekt
            JSONObject rfidpost = response.optJSONObject("rfidMap");

            //itererar igenom hela JSON objektet efter nycklarna
            Iterator<?> keys = rfidpost.keys();

            //Går igenom alla nycklar och värden för att lägga in i id hashMappen
            while (keys.hasNext()) {
                String key = (String) keys.next();
                Boolean value = rfidpost.getBoolean(key);

                idMap.put(key, value);
            }
            //Bryter ut JSONobjektet vid idnamemap till ett nytt JSONobejkt
            JSONObject idNamepost = response.optJSONObject("idNameMap");

            //Kör samma proccess som ovan igen men i en ny hashmap
            Iterator<?> idname = idNamepost.keys();
            while (idname.hasNext()) {
                String key = (String) idname.next();
                String value = idNamepost.getString(key);

                idNameMap.put(key, value);

            }
            //Läser in dörrstatus som skickas med från servern
            doorStatus = response.optBoolean("doorOpen");
            Log.v("ASSYNCTASKGET", doorStatus.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
