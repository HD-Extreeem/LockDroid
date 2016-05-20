package com.hellomicke89gmail.projektsmartlock;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by benjo on 2016-05-02.
 */
public class LogRecyclerAdapter extends RecyclerView.Adapter<LogRecyclerAdapter.MyViewHolder> {

    private ArrayList<LogInfo> logList = new ArrayList<>(); // no null variables

    
    public LogRecyclerAdapter(ArrayList<LogInfo> logList) {

        this.logList = logList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.logg_list_row, parent, false); // inflate and get root for the logg_list_row (get the xml file)
        MyViewHolder holder = new MyViewHolder(view); // pass it to viewHolder (convert it to viewholder)
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        LogInfo current = logList.get(position);
        if (current.getStatus().equals("Failed")) {
            holder.status.setTextColor(Color.parseColor("#FF0000"));
        } else {
            holder.status.setTextColor(Color.parseColor("#009900"));
        }

        holder.name.setText(current.getName());
        holder.date.setText(current.getDate());
        holder.time.setText(current.getTime());
        holder.status.setText(current.getStatus());
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, status, time;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);
            status = (TextView) itemView.findViewById(R.id.status);
        }
    }
}
