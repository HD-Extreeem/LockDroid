package com.hellomicke89gmail.projektsmartlock;

import android.os.AsyncTask;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Klassen är en asynctask klass för att hantera en anslutning till vår server
 * Ansluter till endpointen "client?message=open" hos servern(HTTPS) som i sin tur låser upp låset
 * Created by HadiDeknache on 16-04-28.
 */
public class AsyncTaskUnlock extends  AsyncTask<Void,Void,Void>{
    String authString;
    Controller controller;


    /**
     * Konstruktorn tar emot en referens till authstring som är användarnamn och lösenord till servern samt
     * en referens till controller för att kunna visa upp eventuella felmeddelanden
     * @param authString strängen som innehåller användarnamn och lösenord till servern
     * @param controller referens till controller-klassen
     */
    public AsyncTaskUnlock (String authString, Controller controller){
        this.authString=authString;
        this.controller=controller;
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpsURLConnection urlConnection;

        try {

            System.out.println("Base64 encoded auth string: " + authString);

            URL url = new URL("https://" + authString + "@lockdroid.se/client?message=open");
            urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setRequestProperty("Authorization", "Basic " + authString);

            int statusCode = urlConnection.getResponseCode();

            //fick appen kontakt med servern?
            if(statusCode==200){
                controller.errorToast("Door Have Been Unlocked!");
            }else{
                controller.errorToast("No Connection to Server");
            }

            System.out.println(url);

            System.out.println(statusCode);

        } catch (Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

}
