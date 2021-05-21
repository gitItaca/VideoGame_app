package com.example.videogame_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.videogame_app.models.VideogameModel;

import java.util.ArrayList;

public class ListaVideojuegosAdapter extends  RecyclerView.Adapter<ListaVideojuegosAdapter.ViewHolder> {

    private ArrayList<VideogameModel> data;
    private ItemClickListener myItemClickListener;

    //Añado al constructor el itemClickListener
    public ListaVideojuegosAdapter(ItemClickListener itemClickListener){
        data = new ArrayList<>();
        this.myItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ListaVideojuegosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_videojuego, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaVideojuegosAdapter.ViewHolder holder, int position) {
        VideogameModel vm = data.get(position);
        holder.textViewPortada.setText(vm.getName());
        Glide.with(holder.imageViewPortada.getContext()).load(vm.getBackground_image()).into(holder.imageViewPortada);

        //Añado en el holder el ClicListener y cojo la posicion del videojuego al que he hecho click.
        holder.itemView.setOnClickListener(view -> {
            myItemClickListener.onItemClick(data.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addListaVideojuegos(ArrayList<VideogameModel> videjuegoLista) {
        data.addAll(videjuegoLista);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageViewPortada;
        private TextView textViewPortada;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewPortada = itemView.findViewById(R.id.imageViewPortada);
            textViewPortada = itemView.findViewById(R.id.textViewPortada);
        }
    }

    //Añado la interface con el tipo de item que voy a hacer click
    public interface ItemClickListener{
        void onItemClick(VideogameModel videogameModel);
    }

}