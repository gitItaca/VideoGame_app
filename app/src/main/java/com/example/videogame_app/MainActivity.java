package com.example.videogame_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.videogame_app.authentication.Login;
import com.example.videogame_app.interfaces.VideojuegoAPI;
import com.example.videogame_app.models.VideogameModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Gamecheck");

        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);

        adapter=new Adapter(dataqueue());                                                           //Meto la lista de juegos en el adapter
        recyclerView.setAdapter(adapter);

        GridLayoutManager gridLayoutManager=new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    public ArrayList<VideogameModel> dataqueue(){                                                   //Metodo con el que meto la información que necesito de la API en un ArrayList
        ArrayList<VideogameModel> holderVideogames=new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.rawg.io/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        VideojuegoAPI videojuegoAPI = retrofit.create(VideojuegoAPI.class);

        for(int x=3498;x>3496;x--){
            Integer id = x;
            Call<VideogameModel> call = videojuegoAPI.find(id.toString());              //Llama a la API por el metodo de la interfaz find()

            call.enqueue(new Callback<VideogameModel>() {
                @Override
                public void onResponse(Call<VideogameModel> call, Response<VideogameModel> response) {
                    try{
                        if(response.isSuccessful()){
                            VideogameModel videojuego = new VideogameModel();
                            videojuego = response.body();
                            videojuego.getName();
                            videojuego.getBackground_image();
                            holderVideogames.add(videojuego);
                            //videojuego.getDescription();
                            //Glide.with(getApplication()).load(videojuego.getBackground_image()).into(imgProducto);
                        }
                    }catch(Exception ex){
                        Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT);
                    }
                }

                @Override
                public void onFailure(Call<VideogameModel> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT);
                }
            });
        }

        return holderVideogames;
    }

}