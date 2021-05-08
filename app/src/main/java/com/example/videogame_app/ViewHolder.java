package com.example.videogame_app;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder{

    ImageView imgView;
    TextView txtView;

    public ImageView getImgView() {
        return imgView;
    }
    public void setImgView(ImageView imgView) {
        this.imgView = imgView;
    }

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        imgView=(ImageView)itemView.findViewById(R.id.imgPrueba);
        txtView=(TextView)itemView.findViewById(R.id.textPrueba);
    }
}

