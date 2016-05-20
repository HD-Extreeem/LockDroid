package com.hellomicke89gmail.projektsmartlock;

import android.os.AsyncTask;
import android.util.Base64;
//import com.fasterxml.jackson.databind.*;

//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.core.JsonParseException;
import org.apache.http.client.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
//import com.fasterxml

/**
 * Created by Mikael on 2016-04-20.
 * Author Mikael, Hadi
 * Klassen skickar en lista till HTTP servern som skall uppdatera den befintliga med en ny lista
 */
public class AsyncTaskPOST extends AsyncTask<Void, Void, Void> {
    HashMap<String, Boolean> idMap;
    HashMap<String, String> idNameMap;
    String authString;

    /**
     * @param idMap      idlistan med id och boolean
     * @param idNameMap  idnamn listan med namn och idt som är anknytet
     * @param authString skickar med authstring för att göra en anslutning till HTTPS servern
     */
    AsyncTaskPOST(HashMap<String, Boolean> idMap, HashMap<String, String> idNameMap, String authString) {
        this.idMap = idMap;
        this.idNameMap = idNameMap;
        this.authString = authString;
    }

    /**
     * Metoden ansluter till https servern och skickar en lista till servern med all data
     * Datan skickas som JSON format
     *
     * @param params
     * @return null
     */
    @Override
    protected Void doInBackground(Void... params) {
        //Packar in hashmappen i JSON objekt för att kunna skicka
        JSONObject idNameMap = new JSONObject(this.idNameMap);
        JSONObject idMap = new JSONObject(this.idMap);
        JSONObject rfidMap = new JSONObject();


        try {
            //Lägger in JSON objekten i ett slutligt JSON Objekt som skall skickas
            rfidMap.put("rfidMap", idMap);
            rfidMap.put("idNameMap", idNameMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        URL url;
        try {
            HttpURLConnection urlConnection;

            System.out.println("Base64 encoded auth string: " + authString);

            url = new URL("https://" + authString + "@lockdroid.se/admin");
            urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setRequestProperty("Authorization", "Basic " + authString);

            //Sätter setdooutput för att skicka med body
            urlConnection.setDoOutput(true);

            //Sätter längden till rfidmap för att kunna skicka den
            urlConnection.setFixedLengthStreamingMode(rfidMap.toString().length());

            //Sätter anslutningsmetod till POST
            urlConnection.setRequestMethod("POST");

            //Sätter body till JSON format
            urlConnection.setRequestProperty("Content-Type,", "application/json; charset=UTF-8");
            OutputStreamWriter write = new OutputStreamWriter(urlConnection.getOutputStream());

            //skickar ut mappen och stänger anslutingen
            write.write(rfidMap.toString());
            write.flush();
            write.close();
            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

