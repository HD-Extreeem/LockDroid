package com.hellomicke89gmail.projektsmartlock;

import android.app.admin.DeviceAdminReceiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Klassen har hand om appens samtliga operationer, dvs alla övriga objekts operationer går via Controllerobjektet
 * Initieras samt uppdateras av MainActivity då appen startas/återställs
 * Created by Mikael & Hadi on 2016-05-11.
 */
public class Controller implements PopupMenu.OnMenuItemClickListener{
    private HashMap<String, Boolean> approvedMap=new HashMap<>();
    private HashMap<String, String> idNameMap=new HashMap<>();
    private BroadcastReceiver broadcastReceiver;
    private String key;
    private idFragment idfragment;
    private loggFragment loggfragment;
    private String authString;
    private NavigationView navigationView;
    private TabLayout tabLayout;
    private MainActivity mainActivity;
    private ImageView doorView;
    private DrawerLayout parentDrawerLayout;
    private Boolean doorOpen=false;
    private MenuItem doorStatusItem;


    /**
     * Konstruktorn får en referens till MainActivity, som behövs då det är denna klass som representerar appens context som behövs när man ska skapa popups, initiera broadcastRecievers mm.
     * samt authstring som innehåller namn och lösen som ska användas varje gång vi gör en anslutning till servern
     * @param mainActivity referens till MainActivity
     * @param authString innehåller namn och lösen som ska användas varje gång vi gör en anslutning till servern
     */
    public Controller(MainActivity mainActivity, String authString){
        this.authString=authString;
        this.mainActivity=mainActivity;
    }

    /**
     * Metoden används för att controllern ska få referenser till de objekt den behöver från MainActivity-klassen, listorna hämtas här initialt och vi får därmed även doorstatus
     * anropet på denna metod görs endast i onCreate i MainActivity
     * @param idfragment fragmentet som innehåller recyclerview med tillhörande id-lista
     * @param loggfragment fragmentet som innehåller recyclerview med tillhörande log-lista
     * @param tabLayout tablayouten
     * @param navigationView navigationviewn, behövs för att kunna påverka vad händer i menyn som finns i navigationdrawern
     * @param doorView doorViewn som visar upp doorstatus i
     * @param parentDrawerLayout denna referens behövs för att drawern ska stängas automatiskt då en användare tryckt på en item i menyn
     */
    public void startUp(idFragment idfragment, loggFragment loggfragment, TabLayout tabLayout,NavigationView navigationView, ImageView doorView, DrawerLayout parentDrawerLayout){
        this.idfragment=idfragment;
        this.loggfragment=loggfragment;
        this.tabLayout=tabLayout;
        this.navigationView=navigationView;
        doorStatusItem=navigationView.getMenu().findItem(R.id.doorStatus);
        this.doorView=doorView;
        this.parentDrawerLayout=parentDrawerLayout;
        navigationview();//aktivera lyssnaren för vår navigationdrawer
        getIdList();
        getLoggList();

    }


    /**
     *  Metoden kallas på från MainActivity då appen återställs via onRestoreinstanceState
     * @param authenticationString
     * @param idfragment
     * @param loggfragment
     */
    public void restoreState(String authenticationString, idFragment idfragment, loggFragment loggfragment){
        this.authString=authenticationString;
        this.idfragment=idfragment;
        this.loggfragment=loggfragment;
        getLoggList();
        getIdList();
    }

    /**
     * Metoden kallas på från MainActivity då appen går in i onResume(), listorna hämtas på nytt och därigenom uppdateras även aktuell doorStatus
     */
    public void resumeState(){
        getIdList();
        getLoggList();
        setUpBroadcastReciever();
    }

    /**
     * Metoden returnerar aktuellt idFragment, kallas på från MainActivity vid onSaveInstanceState
     * @return aktuellt idfragment
     */
    public idFragment getidFragment(){
        return this.idfragment;
    }

    /**
     * Metoden returnerar aktuellt logFragment, kallas på från MainActivity vid onSaveInstanceState
     * @return aktuellt logfragment
     */
    public loggFragment getlogFragment(){
        return this.loggfragment;
    }

    /**
     * Metoden tar emot en boolean, true eller false, beroende på om dörren är öppen eller stängd och kallar sedan på presentDoorStatus
     * @param doorOpen true eller false beroende på om dörren är öppen eller stängd
     */
    public void setDoorBoolean(Boolean doorOpen){
        this.doorOpen=doorOpen;
        presentDoorStatus();
    }

    /**
     * Metoden sätter ikon på vår toolbar samt ikon och text i vår navigationdrawer beroende på om dörren är stängd eller öppen(Boolean doorOpen)
     */
    private void presentDoorStatus() {
        if (doorOpen){
            doorView.setBackground(mainActivity.getDrawable(R.drawable.open1));
            doorStatusItem.setIcon(R.drawable.open1);
            doorStatusItem.setTitle(R.string.open);
        }   else{
            doorView.setBackground(mainActivity.getDrawable(R.drawable.closed1));
            doorStatusItem.setIcon(R.drawable.closed1);
            doorStatusItem.setTitle(R.string.closed);
        }
    }

    /**
     * Metoden startar en ny instans av av klassen AsyncTaskUnlock som i sin tur ansluter till en endpoint hos servern och låset öppnas
     */
    private void unlock(){
        AsyncTaskUnlock unlock = new AsyncTaskUnlock(authString,this);
        unlock.execute();
    }

    /**
     * Metoden hämtar in idMap och idNameMap från fragmenten och startar sedan en ny instans av klassen AsyncTaskPOST som ansluter
     * till en endpoint och skickar sen in dem uppdaterade hashmapsen dit
     */
    public void saveToServer(){
        approvedMap = idfragment.getIdMap();
        idNameMap = idfragment.getIdNameMap();
        AsyncTaskPOST post = new AsyncTaskPOST(approvedMap, idNameMap,authString);
        post.execute();
        errorToast("List Sent to Server!");
    }

    /**
     * Metoden startar en ny instans av av klassen AsyncTaskGET som i sin tur ansluter till en endpoint hos servern och en uppdaterad id-lista tas emot
     */
    public void getIdList(){
        new AsyncTaskGET(this, authString).execute();
    }

    /**
     * Metoden startar en ny instans av av klassen AsyncTaskLogGet som i sin tur ansluter till en endpoint hos servern och en uppdaterad log-lista tas emot
     */
    public void getLoggList(){
        new AsyncTaskLogGET(this,authString,"").execute();
    }

    /**
     * Metoden tömmer id-listorna och gör sedan ett anrop till updateadapter() med de nya listorna
     * efter det görs ett anrop till saveToServer() så att de nu ändrade listorna också hamnar på servern
     */
    private void emptylist (){
        approvedMap.clear();
        idNameMap.clear();
        updateIdAdapter(approvedMap, idNameMap);
        saveToServer();
        errorToast("List Cleared!");
    }

    /**
     * Metoden tar emot den uppdaterade idlistan från idRecycleradapter när någon har ändrat behörighet för en användare
     * @param idMap den uppdaterade idMapen från idRecyclerAdaptern
     */
    public void updateList(HashMap<String, Boolean> idMap){

        approvedMap=idMap;

        for(Map.Entry<String, Boolean> p: approvedMap.entrySet()){
            System.out.println(p.getKey().toString()+" , "+p.getValue().toString());
        }

    }

    /**
     * Metoden används av diverse klasser för att visa upp ett meddelande på skärmen i form av en snackbar
     * @param txt meddelandet som ska visas upp på skärmen
     */
    public void errorToast(String txt){
        Snackbar snackbar = Snackbar
                .make(this.navigationView, txt,   Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    /**
     * Metoden uppdaterar idfragmentet med nya uppdaterade hashmaps
     * @param idMap den nya idMapen(hashmap) som ska visas på skärmen
     * @param idNameMap den nya idNameMap(hashmap) som ska visas på skärmen
     */
    public void updateIdAdapter(HashMap<String,Boolean> idMap,  HashMap<String, String> idNameMap){
        idfragment.updateAdapter(idMap,idNameMap);
    }

    /**
     * Metoden uppdaterar idfragmentet med nya uppdaterade hashmaps
     * @param logList den nya logListen(arrayList) som ska visas på skärmen
     */
    public void updateLogAdapter(ArrayList<LogInfo> logList){

        loggfragment.updateAdapter(logList);
    }

    /**
     * Metoden visar skapar en popup som innehåller menyn long_click_popup som i sin tur innehåller knapparna "Edit CardName" och "Remove Card",
     * tar även emot nyckeln som användaren har tryckt på för att få upp pop-upen, denna används senare för att visa upp den egentliga nyckeln som
     * är kopplad till ett eventuellt namn
     * @param v view-objektet där popup-menyn ska visas
     * @param key nyckeln som användaren har tryckt på för att få upp menyn
     */
    public void showPopUp(View v, String key){
        this.key=key;
        Log.v("CONTROLLER",key);
        PopupMenu popupMenu=new PopupMenu(mainActivity, v);
        MenuInflater inflator=popupMenu.getMenuInflater();
        popupMenu.setOnMenuItemClickListener(this);

        inflator.inflate(R.menu.long_click_popup, popupMenu.getMenu());

        popupMenu.show();
    }

    /**
     * Metoden fungerar som lyssnare och returnerar true om någon har tryckt på en item i popupmenyn(denna item som tas emot som parameter)
     * trycker man på "Edit CardName" så anropas showInputDialog(), trycker man på "Remove Card" så hämtas aktuella listor in från id-fragmentet,
     * aktuell nyckel-tas bort från dessa och sedan görs ett anrop tillupdateIdAdapter med dem nya listorna som parametrar
     * @param item aktuell item som har blivit tryckt
     * @return true/false om något av dessa items har blivit valda eller inte
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case(R.id.edit_cardname):
                showInputDialog(key);
                return true;

            case(R.id.remove_card):
                approvedMap = idFragment.getIdMap();
                idNameMap = idFragment.getIdNameMap();
                approvedMap.remove(key);
                idNameMap.remove(key);
                updateIdAdapter(approvedMap,idNameMap);
                saveToServer();
                return true;
            default: return false;
        }
    }

    /**
     * Metoden bygger upp en AlertDialog som innehåller en inputView(som i sin tur instansierat input_dialog).
     * Den tar emot "key" för att kunna visa upp den längst upp i fönstret.
     * Den innehåller även en inre lyssnare som hanterar knappvalen
     * @param key nyckeln som skall visas upp i fönstret
     */
    public void showInputDialog(final String key) {
        this.key=key;

        LayoutInflater inflater=LayoutInflater.from(mainActivity);
        View inputView=inflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder DialogBuilder= new AlertDialog.Builder(mainActivity); //AlertDialogBuilder för att få en färdig menu med cancel och ok knapp
        DialogBuilder.setView(inputView);
        final EditText editext=(EditText)inputView.findViewById(R.id.name_edit_text);

        DialogBuilder.setPositiveButton("SetDate",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        DialogBuilder.setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int id){
                approvedMap=idFragment.getIdMap();
                idNameMap=idFragment.getIdNameMap();
                String cardName=editext.getText().toString();
                if(cardName.equals("")){
                    idNameMap.remove(key);
                }else{
                    idNameMap.put(key, cardName);
                }

                updateIdAdapter(approvedMap,idNameMap);
                saveToServer();

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int id){
                dialog.cancel();
            }
        }).setTitle("CardId: "+key);
        AlertDialog alert=DialogBuilder.create();

        alert.show();

    }

    /**
     * Metoden bygger upp en AlertDialog som innehåller en inputView(som i sin tur instansierat search_input_dialog).
     * Den innehåller även en inre lyssnare som hanterar knappvalen
     * Innehåller även en fokuslyssnare för edittext för att välja datum
     */
    public void showInputSearchDialog() {

        LayoutInflater inflater = LayoutInflater.from(mainActivity);
        final View promptView = inflater.inflate(R.layout.search_input_dialog, null);
        //AlertDialogBuilder för att få en färdig menu med cancel och ok knapp
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mainActivity);
        alertDialogBuilder.setView(promptView);

        final EditText etDate=(EditText) promptView.findViewById(R.id.etDate);

        final EditText editSearchInput = (EditText) promptView.findViewById(R.id.editSearch);

        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle("Search in Log:");
        //Lyssnare för när man trycker på edittext fältet för att få upp en kalender
        etDate.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View view, boolean hasfocus){
                if(hasfocus){
                    //Skapar ny dialog fragment och visar upp kalendern
                    DialogFragment dialogFragment = new DatePickerFragment(etDate);
                    dialogFragment.show(mainActivity.getSupportFragmentManager(), "Date_picker_fragment");
                }
            }

        });
        //Lyssnare för när man trycker search vilket söker och ger en ny lista man sökt efter
        alertDialogBuilder.setPositiveButton("Search", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Log.v("OnClick 'ok' :", "");
                String logType = editSearchInput.getText().toString()+"&"+etDate.getText();
                //Väljer andra tabben automatiskt
                TabLayout.Tab tab=tabLayout.getTabAt(1);
                tab.select();
                Log.v("OnClick type: ", "" + logType);
                //Hämtar den sökta listan från servern
                new AsyncTaskLogGET(Controller.this,authString,logType).execute();
            }
        });
        //Lyssnare för cancle knappen vilket avbryter söknings input
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Log.v("Cancel", "");
                dialog.cancel();
            }
        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }



    /**
     * Metoden instansierar ett intentfilter som fångar upp intent med valda "taggar", instansierar en ny instans av den inre klassen BroadcastReciever och
     * registrerar sedan en ny intentreciever hos MainActivity med intentfiltert och BroadcastRecievern som parameter, recievern är av typen LocalBroadcastManager
     * för att säkra att broadcasten håller sig inom appens gränser och inte läcker till andra appar mm.
     */
    private void setUpBroadcastReciever(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Change to card id data on server");
        intentFilter.addAction("Change to log list on server");
        intentFilter.addAction("opened");
        intentFilter.addAction("closed");
        broadcastReceiver = new BroadCastReciever();
        LocalBroadcastManager.getInstance(mainActivity).registerReceiver(broadcastReceiver,new IntentFilter(GCMRegistrationIntentService.registration_sucsses));
        LocalBroadcastManager.getInstance(mainActivity).registerReceiver(broadcastReceiver,new IntentFilter(GCMRegistrationIntentService.registration_failed));
        LocalBroadcastManager.getInstance(mainActivity).registerReceiver(broadcastReceiver,intentFilter);
    }

    /**
     * Metoden kallas från onPause i mainactivity och avregistrerar den aktiva broadcastrecievern, detta för att inte intent ska läcka
     */
    public void unregisterRecievers(){
        LocalBroadcastManager.getInstance(mainActivity).unregisterReceiver(broadcastReceiver);
    }



    /**
     * Den inre klassen representerar en Broadcastmottagare och extendar BroadcastReciever
     * den hanterar de olika intent som skickas inom appen:
     * registration_sucsses: appen har registrerats hos GCM och token har tagits emot
     * registration_failed: av någon anledning har registreringern hos GCM misslyckats
     * Change to card id data on server: id-listan har förändrats på servern=> uppdatera den!
     * Change to log list on server: log-listan har förändrats på servern=> uppdatera den!
     * opened: dörren står öppen=>visa upp det i appen!
     * closed: dörren är stängd=>visa upp det i appen!
     *
     */
    private class BroadCastReciever extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(GCMRegistrationIntentService.registration_sucsses)){
                String token=intent.getStringExtra("token");
                errorToast("GCM token: "+ token);
                Log.v("CONTROLLER",token);
                new AsycTaskPostToken(token,authString).execute();

            }else if(intent.getAction().equals(GCMRegistrationIntentService.registration_failed)){
                errorToast("GCM REGISTRATION FAILED");
            }

            else if(intent.getAction().equals("Change to card id data on server")){
                Log.v("APPROVEDLISTVIEW", "Change to card id data on server!");
                getIdList();

            }else if(intent.getAction().equals("Change to log list on server")){
                Log.v("APPROVEDLISTVIEW", "Change to log list on server");
                getLoggList();
            }else if(intent.getAction().equals("opened")){
                setDoorBoolean(true);
            }
            else if(intent.getAction().equals("closed")){
                setDoorBoolean(false);
            }
        }
    }


    /**
     * Metoden instansierar en lyssnare på vår navigationView och actions utförs därefter
     */
    private void navigationview(){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(false);

                parentDrawerLayout.closeDrawers();//anropet gör att drawern stängs direkt efter att en användare har tryckt på en item i menyn

                switch (item.getItemId()){
                    case R.id.unlock:
                        unlock();

                        item.setChecked(false);
                        return true;

                    case R.id.clearlist: emptylist();
                        item.setChecked(false);

                        return true;

                    case R.id.searchLog:
                        showInputSearchDialog();
                        item.setCheckable(false);

                        return true;

                    case R.id.logOut:
                        logOut();
                        return true;
                }
                return false;
            }
        });navigationView.setItemIconTintList(null);
    }

    /**
     * Metoden anropas då man trycker på logout knappen i vår drawer
     * ett nytt intent att starta LoginActivity skapas en flagga sätts och vår MainActivity avslutas
     */
    private void logOut() {
        Intent intent = new Intent(mainActivity,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);//gör så att LoginActivity inte lagras på stacken om användaren på något sätt navigerar ifrån den
        mainActivity.finish();//mainactivity avslutas
        mainActivity.startActivity(intent);//LoginActivity startas
    }




}
