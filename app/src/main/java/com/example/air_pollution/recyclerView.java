package com.example.air_pollution;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class recyclerView extends RecyclerView.Adapter<recyclerView.Holder> {


    private ArrayList<String> sensor_names;
    private ArrayList<String> sensor_ids;
    private ArrayList<String> sensor_threshold;


    public recyclerView(ArrayList<String> sensor_names, ArrayList<String> sesnor_ids, ArrayList<String> sensor_threshold) {
        this.sensor_names = sensor_names;
        this.sensor_ids = sesnor_ids;
        this.sensor_threshold = sensor_threshold;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_list,parent,false);
        Holder holder = new Holder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        holder.sensor_name.setText("Sensor name: "+sensor_names.get(position));
        holder.sensor_id.setText("sensor id: " +sensor_ids.get(position));
        holder.sensor_threshold.setText("sensor threshold: "+sensor_threshold.get(position));


    }

    @Override
    public int getItemCount() {
        return sensor_ids.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView sensor_name;
        TextView sensor_id;
        TextView sensor_threshold;
        public Holder(@NonNull View itemView) {
            super(itemView);
            sensor_id = itemView.findViewById(R.id.sensor_id);
            sensor_name = itemView.findViewById(R.id.sensor_name);
            sensor_threshold = itemView.findViewById(R.id.sensor_threshold);
        }
    }
}
