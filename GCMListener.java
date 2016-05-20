package com.hellomicke89gmail.projektsmartlock;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Klassen hanterar inkommande pushnotiser då appen är igång, extendar GcmListenerService overrides onMessageRecieved
 * Created by Mikael on 2016-05-03.
 */
public class GCMListener extends GcmListenerService {


    /**
     * Metoden tar emot en bundle med data samt ett sender ID som GCM använder för att kontrollera om servern får skicka meddelande till appen
     * eller inte
     * @param from sender id
     * @param data data bundle som i vårt fall endast innehåller String-meddelanden
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String silent=data.getString("silent");
        String message=data.getString("message");
        Log.v("GCMListener",message);
        Log.v("GCMListener", "FROM: "+from);

        //ska pushnotisen vara tyst eller ska den visas upp på skärmen?
        if(silent.equals("false")) {

            //En synlig notis byggs upp för att visas som "larm" om att dörren stått öppen i
            Log.v("GCMLISTENER", message);
            Intent intent = new Intent(this, Controller.class);
            //säkerhetsställer att intentet inte hamnar hos en ny instans av Controller utan till den "pågående" instansen av klassen
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            int requestCode = 0;
            PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);//detta pendingIntent kan endast användas en gång, ingen risk för återanvändning

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle("Ny notis från Lockdroidservern")
                    .setContentText(message)
                    .setSmallIcon(R.drawable.icon)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, builder.build());



        }
        else {
            Intent intent = new Intent();
            intent.setAction(message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent); //localbroadcastmanager garanterar att vårt intent inte skickas någonstans utanför appen
        }

    }

}
