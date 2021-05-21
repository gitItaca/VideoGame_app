package com.example.videogame_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class VideogameDetailActivity extends AppCompatActivity {

   //Bundle datos;
   private static final String TAG = "VideogameDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videogame_detail);
        Log.d(TAG, "llamado");
//        datos = getIntent().getExtras();
//        String textoIn = datos.getString("id");
//        TextView tvPru = findViewById(R.id.textView2);
//        tvPru.setText(textoIn);
    }
}