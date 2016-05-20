package com.hellomicke89gmail.projektsmartlock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HadiDeknache on 16-04-25.
 * Author Mikael,Hadi
 * Klassen skapar ett fragment till idlistan som visar idn/namn och ifall den har tillåten åtkomst
 */
public class idFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static HashMap<String, Boolean> idMap;
    private static HashMap<String, String> idNameMaps;
    private static ArrayList<Person> keys;
    private static Controller controller;
    protected RecyclerView recyclerView;
    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.Adapter myAdapter;

    /**
     * @param key         idn lagrade i en arraylist
     * @param idMaps      hashmappen med idn och  boolean
     * @param idNameMap   hashmap med namn och id som är kopplat
     * @param controllers kontrollerklass referens
     * @return returnerar ett nytt fragment vid start
     */
    public static idFragment newInstance(ArrayList<Person> key, HashMap<String, Boolean> idMaps, HashMap<String, String> idNameMap, Controller controllers) {
        keys = key;
        idMap = idMaps;
        idNameMaps = idNameMap;
        controller = controllers;
        return new idFragment();
    }

    /**
     * Metoden fungerar som oncreate
     * Initierar komponenter och sätter lyssnare
     * Skapar ett nytt adapter för idfragmentet och sätter det till recyclerviewn
     * @param inflater komponent som kan skapa view instans baserat från xml filen
     * @param container Viewn för fragmentet där det representerar sitt fragment
     * @param savedInstanceState bundel med data som kan spara data
     * @return den nya viewn med recyclerviewn
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_id, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView1);
        myAdapter = new IdRecyclerAdapter(keys, idMap, idNameMaps, this.controller);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new MyItemDecoration());

        recyclerView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();

        return rootView;
    }

    /**
     * Metoden uppdaterar recycleradaptern
     * Kollar ifall ett id i idmap finns i idnamemap, sätter namnet istället då
     *
     * @param idMap     hashmapen med id och booleanvärden
     * @param idNameMap hashmap med alla id med ett anknytet namn
     */
    public void updateAdapter(HashMap<String, Boolean> idMap, HashMap<String, String> idNameMap) {
        keys.clear();
        String key;

        Boolean approved;
        for (Map.Entry<String, Boolean> p : idMap.entrySet()) {
            String name = null;
            if (idNameMap.containsKey(p.getKey())) {
                name = idNameMap.get(p.getKey());
            }
            approved = p.getValue();
            key = p.getKey().toString();
            Person person = new Person(key, name);
            this.keys.add(person);

        }
        idFragment.idMap = idMap;
        idNameMaps = idNameMap;

        //Kollar ifall recyclerviewn redan har en adapter
        //ifall den har så byter den befintliga adaptern med den nya
        if (recyclerView.getAdapter() != null) {
            recyclerView.swapAdapter(new IdRecyclerAdapter(this.keys, idMap, idNameMaps, controller), false);
        }
        //Annars skapas en ny adapter för recyclerview
        else {
            myAdapter = new IdRecyclerAdapter(this.keys, idMap, idNameMaps, controller);
            recyclerView.setAdapter(myAdapter);
        }
    }

    /**
     * Metoden är en lyssnare till swiperefresh
     * hämtar idlista när man refreshar
     */
    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        controller.getIdList();
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Metoden skickar en kopia av idnamehashmappen
     *
     * @return en kopia av idnamap
     */
    public static HashMap<String, String> getIdNameMap() {
        return (HashMap<String, String>) idNameMaps.clone();
    }

    /**
     * Metoden skickar en kopia av idhashmappen
     *
     * @return en kopia av idmap med alla idn och booleanvärden
     */
    public static HashMap<String, Boolean> getIdMap() {
        return (HashMap<String, Boolean>) idMap.clone();
    }


}