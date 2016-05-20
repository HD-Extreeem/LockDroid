package com.hellomicke89gmail.projektsmartlock;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Klassen initierar appens samtliga komponenter, sparar undan nödvändig information då appen går in i onPause osv.
 * Vi har valt att använda oss av en controller-klass som utför appens samtliga operationer.
 * Created by Hadi & Mikael on 2016-05-11.
 */
public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private HashMap<String, Boolean> approvedMap=new HashMap<>();
    private ArrayList<Person> personList=new ArrayList<>();
    private HashMap<String, String> idNameMap=new HashMap<>();
    private idFragment idfragment;
    private loggFragment loggfragment;
    private ArrayList<LogInfo> loggList=new ArrayList<>();
    private static String authString;
    private TextView loginLabel;
    private static String usernameLabel;
    private DrawerLayout parentDrawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private Controller controller;
    private ImageView doorView;

    /*
     * Vi har ändrat i manifestet så att klassens onCreate endast kallas en gång.
     * I metoden instansieras först controller-klassen för att kunna lämna en referens av den då vi initierar de olika komponenterna(fragments, layouts, views)
     * Vi skickar sedan referenser till dessa vidare till controller-objektet som sen kan utföra diverse operationer på dem.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        controller=new Controller(this, authString);
        setContentView(R.layout.main_activity);
        registercomponents();
        setupDrawer();
        setUpViewPager();
        controller.startUp(idfragment,loggfragment,tabLayout,navigationView,doorView, parentDrawerLayout);
        googlePlayServices();
    }

    /*
     * Här sparar vi undan samtlig nödvändig information så som authString som är användarnamn och lösenord
     * username sparas undan då vi vill visa upp det i vår navigation-drawer
     * fragmenten sparas undan för att återställa de båda listornas tillstånd i appen
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("authString",authString);
        outState.putString("username",usernameLabel);
        Log.v("OnsaveInstanceState",usernameLabel);
        idfragment=controller.getidFragment();
        loggfragment=controller.getlogFragment();
        getSupportFragmentManager().putFragment(outState,"idFragmentState",idfragment);
        getSupportFragmentManager().putFragment(outState,"logFragmentState",loggfragment);
        super.onSaveInstanceState(outState);
    }

    /*
     * Metoden återställer datan som sparats undan i onSaveInstanceState då appen har pushats för långt ner på programstacken
     */
    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        authString=inState.getString("authString");
        usernameLabel=inState.getString("username");
        idfragment=(idFragment) getSupportFragmentManager().getFragment(inState,"idFragmentState");
        loggfragment=(loggFragment) getSupportFragmentManager().getFragment(inState,"logFragmentState");
        loginLabel.setText(usernameLabel);
        controller.restoreState(authString, idfragment, loggfragment);
        drawerToggle.syncState();//synkroniserar ikonen för drawern med vilket stadie drawern befinner sig i, krävs då vi vill visa denna ikon
    }

    /*
     * Metoden kallar på resumeState i Controller-klassen
      * som uppdaterar listorna(och därigenom också uppdatera aktuell dörrstatus) då appen resumas, även controllerns BroadcastReciever initieras på nytt
     */
    @Override
    protected void onResume() {
        super.onResume();
        controller.resumeState();
    }

    /*
     * I onPause avregistreras BroadcastRecievern för att undvika att intent "läcker"
     */
    @Override
    protected void onPause() {
        super.onPause();
       controller.unregisterRecievers();
    }

    /*
    Metoden instansierar viewpageradapter med tillhörande Fragmentmanager, för att sedan skapa nya instanser av id- samt logfragment och sätta in dessa i viewpagerAdaptern.
    adaptern kopplas sedan till vår viewpager som i sin tur kopplas till vår tablayout
     */
    private void setUpViewPager() {
        FragmentManager manager = getSupportFragmentManager();
        ViewPagerAdapter adapter = new ViewPagerAdapter(manager);
        adapter.addFragment(idfragment = idFragment.newInstance(personList,approvedMap,idNameMap,controller));//hade från början även en titel men bytade sedan till ikoner som sätts in längre ned
        adapter.addFragment(loggfragment=loggfragment.newInstance(loggList,controller));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.idlist);
        //lägger till ikon till tabb(0)
        tabLayout.getTabAt(1).setIcon(R.drawable.loglist);//lägger till ikon till tabb(1)
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//den här raden tar bort vår orginella "app-titel" vilket underlättar då vi själva vill bestämma storlek position och stil på denna
    }

    /*
        Metoden initierar samtliga komponenter som vi har i våra .xml filer
     */
    private void registercomponents() {
        doorView=(ImageView)findViewById(R.id.doorView);

        navigationView=(NavigationView)findViewById(R.id.navigation_view);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        parentDrawerLayout=(DrawerLayout)findViewById(R.id.parent_drawer_layout);//parentDrawerLayout är layouten som kapslar in övriga layouter i vår main_activity.xml fil
        View v=navigationView.getHeaderView(0);
        loginLabel=(TextView)v.findViewById(R.id.UsernameLabel);


    }

    /*
        Metoden initierar vår drawer och möjliggör slidefunktionen
     */
    private void setupDrawer(){
        loginLabel.setText(usernameLabel);
        drawerToggle=new ActionBarDrawerToggle(this, parentDrawerLayout,toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close){ };
        parentDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();//synkroniserar ikonen för drawern med vilket stadie drawern befinner sig i, krävs då vi vill visa denna ikon

    }

    /**
     * Metoden returnerar true om man trycker på den randiga ikonen längst upp till vänster och drawern öppnas
     * @param item ikon som visar att det finns en navigationdrawer till vänster
     * @return true om man trycker på ikonen
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Metoden kallas på från vår LoginActivity-klass som vidarebefodrar vår authString(som används varje gång vi ansluter till servern) och username som sätts i vår navigationdrawerHeader
     * @param authString konverterad sträng av username och password till en bytesträng som används varje gång vi ansluter till servern
     * @param usernameLabel username som används för att visa upp vilken användare som är inloggad för tillfället
     */
    public void setCredentials(String authString,String usernameLabel) {
        this.authString=authString;
        this.usernameLabel = usernameLabel;
    }

    /*
    Metoden kontrollerar om giltlig version av GooglePlay Services är installerad på telefonen, detta  måste kontrolleras i och med att vi använder oss av GoogleCloudMessaging för att skicka
    pushnotiser, bland annat då en förändring i listorna sker, som använder sig av dess anslutning för att skicka meddelanden
     */
    private void googlePlayServices(){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (ConnectionResult.SUCCESS != resultCode) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                controller.errorToast("Googleplay Service is not installed");
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
            } else {
                controller.errorToast("Device does not support GooglePlay Services");
            }
        } else {
            Intent intent = new Intent(this, GCMRegistrationIntentService.class);
            startService(intent);
        }
    }



}
