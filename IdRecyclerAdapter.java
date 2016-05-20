package com.hellomicke89gmail.projektsmartlock;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author
 * Klassen har hand om adaptern för recyclerviewn
 * Sätter lyssnare på card-idn och switchen när man trycker
 * Uppdaterar vyn när man ändrar positionen
 */
public class IdRecyclerAdapter extends RecyclerView.Adapter<IdRecyclerAdapter.PersonViewHolder> {
    private HashMap<String, Boolean> idMap;
    private HashMap<String, String> idNameMap;
    private ArrayList<Person> keys;
    private Controller controller;

    /**
     * @param keys       idn för alla användare
     * @param idMap      hashmappen med id och åtkomststatus true/false
     * @param idNameMap  hashmap med namn som är anknutna till ett namn
     * @param controller referens till controller
     */
    IdRecyclerAdapter(ArrayList<Person> keys, HashMap<String, Boolean> idMap, HashMap<String, String> idNameMap, Controller controller) {
        this.idMap = idMap;
        this.keys = keys;
        this.idNameMap = idNameMap;
        this.controller = controller;

    }

    /**
     * Konstruerar upp items för varje rad för att visas upp i recyclerview
     *
     * @param viewGroup där den nya vyn skall läggas in
     * @param viewType  den nya vyn typen för den nya vyn
     * @return nya vyn för personviewholder
     */
    public IdRecyclerAdapter.PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.id_row, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    /**
     * Metoden kallas av recyclerviewn för att visa specifik data på viss position
     * Sätter lyssnare på switcharna och card_id
     * @param holder   holdern som skall uppdateras för att representera rätt givna position
     * @param position på den nya item i listan
     */
    @Override
    public void onBindViewHolder(PersonViewHolder holder, final int position) {
        //Kollar ifall en användare har satt ett namn för ett id
        //Sätter card-id till namnet istället
        if (keys.get(position).getName() == null) {

            holder.card_Id.setText(keys.get(position).getKey());
        }
        //Annars sätts id på card-id
        else {

            holder.card_Id.setText(keys.get(position).getName());
        }


        holder.card_Id.setOnClickListener(clickListener);
        holder.card_Id.setOnLongClickListener(longClickListener);

        holder.chkSelected.setChecked(idMap.get(keys.get(position).getKey()));

        holder.chkSelected.setTag(keys.get(position).getKey());

        holder.chkSelected.setOnClickListener(boxclickListener);

        holder.card_Id.setTag(holder);


    }

    /**
     * Kallas av recyclerview när den observerar data
     *
     * @param rcView recyclerviewn som används för att visa data på
     */
    public void onAttachedToRecyclerView(RecyclerView rcView) {
        super.onAttachedToRecyclerView(rcView);
    }

    /**
     * Hä,tar antalet element i listan
     *
     * @return antalet element i recyclerviewn
     */
    @Override
    public int getItemCount() {

        return keys.size();
    }

    /**
     * Denna klass har hand om olika lyssnare för att lyssna klick på
     * en viss rad i recyclerview
     */
    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        TextView card_Id;
        SwitchCompat chkSelected;

        /**
         * initierar komponenter i id-row
         *
         * @param itemView viewn för en recyclern
         */
        PersonViewHolder(View itemView) {
            super(itemView);

            card_Id = (TextView) itemView.findViewById(R.id.card_id);
            chkSelected = (SwitchCompat) itemView.findViewById(R.id.chkSelected);

        }

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        /**
         * Metoden har hand om lyssnare för när man trycker på ett
         * Kollar ifall ett id är true så sätts det till false
         * annars så sätts det till true
         * Uppdaterar listan och adaptern
         * och skickar till servern
         * @param view för att hämta intryckta item
         */
        @Override
        public void onClick(View view) {

            PersonViewHolder holder = (PersonViewHolder) view.getTag();
            int position = holder.getAdapterPosition();
            String key = keys.get(position).getKey();

            if (idMap.get(key)) {
                idMap.put(key, false);
            } else {
                idMap.put(key, true);
            }

            controller.updateList(idMap);
            controller.updateIdAdapter(idMap, idNameMap);
            controller.saveToServer();


        }


    };

    View.OnClickListener boxclickListener = new View.OnClickListener() {
        /**
         * lyssnar på klick på switch
         * Kollar ifall switchad till on/off
         * lägger in den i listan och skickar till servern
         * @param v viewn för id-row
         */
        @Override
        public void onClick(View v) {
            SwitchCompat cb = (SwitchCompat) v;

            String key = cb.getTag().toString();

            idMap.put(key, cb.isChecked());
            controller.updateList(idMap);
            controller.saveToServer();
        }
    };

    View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        /**
         * Metoden lyssnar på längre tryckningar på id
         * Hämtar det intryckta id:t
         * Visar popup med 2 alternativ namnge/ta bort
         * @param v viewn för id-row
         * @return true
         */
        @Override
        public boolean onLongClick(View v) {
            //hämtar tag för det intryckta idt
            PersonViewHolder holder = (PersonViewHolder) v.getTag();
            //hämtar positionen för id
            int position = holder.getAdapterPosition();
            String key = keys.get(position).getKey();
            controller.showPopUp(v, key);
            System.out.println(key);

            return true;
        }

    };


}