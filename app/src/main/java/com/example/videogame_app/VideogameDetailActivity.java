package com.example.videogame_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.videogame_app.interfaces.VideojuegoAPI;
import com.example.videogame_app.models.VideogameModel;
import com.example.videogame_app.models.VideojuegoRespuesta;

import org.jsoup.Jsoup;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideogameDetailActivity extends AppCompatActivity {

   private Bundle bundle;
   private Retrofit retrofit;
   private static final String TAG = "VideogameDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videogame_detail);

        Button buttonReturn = findViewById(R.id.buttonReturnDetail);
        ImageView portada = findViewById(R.id.imageVideogameDetail);
        TextView title = findViewById(R.id.nameVideogameDetail);
        TextView description = findViewById(R.id.descriptionVideogameDetail);

        //___Recojo el id del MainActivity
        bundle = getIntent().getExtras();
        int id = bundle.getInt("id_videogame_selected");
        String idToAPI = Integer.toString(id);
        Log.d(TAG, String.valueOf(id));

        //__Llamo a la API
        retrofit = new Retrofit.Builder().baseUrl("https://api.rawg.io/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        VideojuegoAPI videojuegoAPI = retrofit.create(VideojuegoAPI.class);
        Call<VideogameModel> videogameDetailCall = videojuegoAPI.find(idToAPI);

        videogameDetailCall.enqueue(new Callback<VideogameModel>() {
            @Override
            public void onResponse(Call<VideogameModel> call, Response<VideogameModel> response) {
                if(response.isSuccessful()){
                    VideogameModel videojuego = new VideogameModel();
                    videojuego = response.body();

                    //__Recojo en variables la info del model que he recogido con la API
                    String name = videojuego.getName();
                    String descr = videojuego.getDescription();
                    //__Quito las HTML tags de la descripcion
                    descr = Jsoup.parse(descr).text();
                    //__Asigno al layout la info
                    title.setText(name);
                    description.setText(descr);
                    Glide.with(getApplication()).load(videojuego.getBackground_image()).into(portada);
                    //Log.d(TAG, String.valueOf(name));

                }else{
                    Log.e(TAG, "onResponse FAIL: " +  String.valueOf(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<VideogameModel> call, Throwable t) {
                Log.e(TAG, "onFailure: " +  String.valueOf(t.getMessage()));
            }
        });

        //__Boton para volver a la pagina principal
        Intent intentVolver = new Intent(this, MainActivity.class);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentVolver);
            }
        });

    }
}