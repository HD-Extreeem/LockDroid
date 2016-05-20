package com.hellomicke89gmail.projektsmartlock;

import android.util.Log;

/**
 * Created by benjo on 2016-05-02.
 * Author
 * Klassen representerar log objekt med datum,tid,namn och status
 */
public class LogInfo {
    String name, date, status, time;

    /**
     * Hämtar datumet för loginfo
     *
     * @return datumet för ett visst loginfo objekt
     */
    public String getDate() {
        return date;
    }

    /**
     * Hämtar namnet för ett loginfo objekt
     * @return namnet
     */
    public String getName() {
        return name;
    }

    /**
     * hämtar tiden för loginfo objektet
     * @return tiden
     */
    public String getTime() {
        return time;
    }

    /**
     * Hämtar status för ett loginfo objekt
     * @return status ifall lyckad eller inte
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sätter datumet
     * @param date då man blippade/öppnade
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Sätter namnet för en användare
     * @param name namnet för en användare som användes
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sätter status ifall lyckat eller inte
     * @param status på ifall lyckat eller inte
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sätter tiden för då man blippade/öppnade
     * @param time tiden som skall sättas
     */
    public void setTime(String time) {
        this.time = time;
    }

}
