package com.hellomicke89gmail.projektsmartlock;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


/**
 * Created by HadiDeknache on 16-04-25.
 * Author ??
 * Klassen representerar ett fragment till logglistan
 * innehåller en recyclerview med loggdata
 *
 */
public class loggFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView loggRecyclerView;
    private LogRecyclerAdapter loggAdapter;
    private static ArrayList<LogInfo> logList;
    protected SwipeRefreshLayout swipeRefreshLayout;

    private static Controller controller;
    protected RecyclerView.Adapter myAdapter;
    private View rootView;

    /**
     * Metoden returnerar ett nytt fragment
     * @param logglist listan med all loggdata
     * @param controllers referens till controller klassen
     * @return det nya fragmentet för loggen
     */
    public static loggFragment newInstance(ArrayList<LogInfo> logglist, Controller controllers) {
        logList = logglist;
        controller = controllers;
        return new loggFragment();
    }

    /**
     * Metoden fungerar som oncreate
     * initierar komponenter
     * registrerar lyssnare på swiperefresh
     * skapar ny adapter för loggfragment och sätter det för recyclerviewn
     * @param inflater komponent som kan skapa view instans baserat från xml filen
     * @param container Viewn för fragmentet där det representerar sitt fragment
     * @param savedInstanceState bundel med data som kan spara data
     * @return den nya viewn med recyclerviewn
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_logg, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefreshLogg);
        swipeRefreshLayout.setOnRefreshListener(this);


        myAdapter = new LogRecyclerAdapter(logList);
        loggRecyclerView = (RecyclerView) rootView.findViewById(R.id.logglist);

        final LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());

        loggRecyclerView.setLayoutManager(LinearLayoutManager);
        loggRecyclerView.addItemDecoration(new MyItemDecoration());

        loggRecyclerView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();

        return rootView;
    }

    /**
     * Metoden uppdaterar adaptern för recyclerviewn
     *
     * @param loggList listan med all data för loggen
     */
    public void updateAdapter(ArrayList<LogInfo> loggList) {
        this.logList.clear();
        for (int i = loggList.size() - 1; i >= 0; i--) {
            int k = 0;
            this.logList.add(k, loggList.get(i));
            k++;
        }
        //Kollar ifall recyclerviewn redan har en adapter
        //ifall den har så byter den befintliga adaptern med den nya
        if (loggRecyclerView.getAdapter() != null) {
            loggRecyclerView.swapAdapter(new LogRecyclerAdapter(this.logList), false);
        }
        //Annars skapas en ny adapter för recyclerview
        else {
            loggAdapter = new LogRecyclerAdapter(this.logList);
            loggRecyclerView.setAdapter(loggAdapter);
        }
    }

    /**
     * Metoden är en lyssnare till swiperefresh
     * hämtar loglistan när man refreshar
     */
    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        controller.getLoggList();
        swipeRefreshLayout.setRefreshing(false);
    }


}