package com.hellomicke89gmail.projektsmartlock;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

/**
 * Created by Mikael on 2016-05-03.
 */
public class GCMRegistrationIntentService extends IntentService{
    public static final String registration_sucsses="Registration_Sucsess";
    public static final String registration_failed="Registration_Failed";


    public GCMRegistrationIntentService(){
        super("");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        registerGCM();

    }


    private void registerGCM(){
        Intent registrationComplete=null;
        String token=null;
        try{
            InstanceID instanceID=InstanceID.getInstance(getApplicationContext());
            token=instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.v("RegIntentService", "token: "+token);
            registrationComplete=new Intent(registration_sucsses);
            registrationComplete.putExtra("token", token);
        }catch (Exception e){
            Log.v("RegIntentService", "Registration Error");
            registrationComplete=new Intent(registration_failed);

        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}
