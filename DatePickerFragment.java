package com.hellomicke89gmail.projektsmartlock;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by HadiDeknache on 16-05-22.
 * Author Hadi
 * Denna klass representerar ett kalender som visas när man söker
 * Visar upp en kalender som man kan välja datum när man söker
 * Sätter edittext när man sökt till det datum man valt
 */
@SuppressLint("ValidFragment")
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private EditText etDate;

    /**
     * Denna metod instansierar edittext som skall skrivas på när man valt datum
     * @param etDate edittext som skall sättas text på när man valt
     */
    public DatePickerFragment(EditText etDate) {
        this.etDate = etDate;
    }

    /**
     * Denna metod sätter dagens datum på kalendern för att underlätta för en
     * @param savedInstanceState bundel som skall lagrar information
     * @return dagens datum vilket sätts på kalendern
     */
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        // Sätter till dagens datum när man startar fragmentet
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);


    }

    /**
     * Metoden sätter datumet man valt i edittext fältet för att underlätta för användaren
     * @param view datepickern viewn som visas när man väljer
     * @param year året man valt i kalender
     * @param month månaden man valt i kalendern
     * @param day dagen som man valt i kalendern
     */
    //Sätter datum efter att man valt ett datum
    public void onDateSet(DatePicker view, int year, int month, int day) {
        String date=day+"/"+(month+1)+"/"+year;
        etDate.setText(date);
        etDate.clearFocus();
    }

}


