package com.example.videogame_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.videogame_app.models.VideogameModel;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<ViewHolder> {

    ArrayList<VideogameModel> data;

    public Adapter(ArrayList<VideogameModel> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater lytInflater=LayoutInflater.from(parent.getContext());
        View view= lytInflater.inflate(R.layout.singlerow,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtView.setText(data.get(position).getName());
        //holder.imgView.setImageResource(data.get(position).getBackground_image());
        Glide.with(holder.imgView.getContext()).load(data.get(position).getBackground_image()).into(holder.imgView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}