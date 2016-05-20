package com.hellomicke89gmail.projektsmartlock;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Author
 * Klassen gör en anslutning till HTTPS servern för att ta emot loggdata
 * loggdatan skickas sedan vidare till controllerklassen
 */
public class AsyncTaskLogGET extends AsyncTask<Void, Void, Integer> {
    private ArrayList<LogInfo> logList = new ArrayList<>();
    private Controller controller;
    private String authString;
    private String logType;


    /**
     * @param controller skickar med instans till controller klassen
     * @param authString skickar med authstring för att göra en anslutning till HTTPS servern
     * @param logType    logtyp som skall sökas efter i loggen
     */
    AsyncTaskLogGET(Controller controller, String authString, String logType) {
        this.controller = controller;
        this.authString = authString;
        this.logType = logType;
    }

    /**
     * Metoden ansluter till HTTPS servern för att sedan ta emot en sträng med dataloggen
     * urln bestäms beroende på ifall vi sökt eller bara vill ha hela loggen
     * ifall anslutningen lyckades läser vi in hela strängen från server
     * delar upp den och sätter result till 1
     * annars sätter vi result till 0 och poppar ut ett meddelande att det misslyckades
     *
     * @param params
     * @return resultatet på anslutningen ifall det gick eller inte
     */
    @Override
    protected Integer doInBackground(Void... params) {
        String parts[];
        String text = null;
        URL url;
        Integer result = 0;


        try {
            if (logType.equals("")) {
                //Denna satsen körs ifall man vill ha hela loglistan, exempelvis vid start och refresh
                url = new URL("https://" + authString + "@lockdroid.se/log?/");
            }
            //Denna körs annars ifall man använt sökfunktion där man söker efter en viss tid,person,status eller datum
            else {
                //enkodar länken eftersom url inte stöjder att det formatet som matas in och måste omformateras för att det skall funka
                logType= URLEncoder.encode(logType, "utf-8");
                url = new URL("https://" + authString + "@lockdroid.se/log?search="+logType);
            }
            Log.v("LOGURL",url.toString());
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            //Sätter enkryptering för att ansluta till https
            urlConnection.setRequestProperty("Authorization", "Basic " + authString);
            urlConnection.setRequestMethod("GET");

            int statusCode = urlConnection.getResponseCode();
            System.out.println(urlConnection.getURL());
            if (statusCode == 200) {
                BufferedReader read = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                //Läser in första tomraden för att inte problem skall uppstå
                for (int i = 0; i < 2; i++) {
                    text = read.readLine();
                }
                //Läser in hela strängen och lägger in i personobjekt som sedan läggs i listan
                while (text != null) {
                    LogInfo currentPerson = new LogInfo();
                    parts = text.split(",");
                    for (int i = 0; i < 4; i++) {
                        if (i == 0) {
                            currentPerson.setName(parts[i]); //hämta namn
                        } else if (i == 1) {
                            currentPerson.setDate(parts[i]); // hämta datum
                        } else if (i == 2) {
                            currentPerson.setTime(parts[i]); // hämta tid
                        } else if (i == 3) {
                            currentPerson.setStatus(parts[i]); // hämta status
                        }
                    }
                    logList.add(0, currentPerson);
                    text = read.readLine();
                }
                result = 1;
            } else {
                result = 0;
            }

        } catch (Exception e) {
            Log.d("AsynTaskLogGet", e.getLocalizedMessage());
        }
        return result;
    }

    /**
     * Kollar ifall anslutningen gick för att skicka listan vidare till controllern
     * Annars så poppar det ut ett meddelande "failed to update log"
     *
     * @param result resultatet på ifall anslutningen gick eller inte
     */
    @Override
    protected void onPostExecute(Integer result) {
        if (result == 1) {
            controller.updateLogAdapter(logList);
            controller.errorToast("Log updated");
        } else {
            System.out.println("Failed to update log");
            controller.errorToast("Failed to update log");
        }
    }

}
