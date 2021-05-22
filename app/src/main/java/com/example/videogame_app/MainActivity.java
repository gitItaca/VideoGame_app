package com.example.videogame_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.videogame_app.authentication.Login;
import com.example.videogame_app.interfaces.VideojuegoAPI;
import com.example.videogame_app.models.VideogameModel;
import com.example.videogame_app.models.VideojuegoRespuesta;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "libreria";
    private Retrofit retrofit;
    private RecyclerView recyclerView;
    private ListaVideojuegosAdapter listaVideojuegosAdapter;
    private ArrayList<VideogameModel> videjuegoLista;
    private int page=1;
    private boolean isScrolling = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setTitle("Gamecheck");

        recyclerView=findViewById(R.id.recyclerView);

        //___Creo el intent y le asocio la pagina de detalle
        Intent intent = new Intent(this, VideogameDetailActivity.class);

        //___Al adapter le meto el item click y le asocio el intent para que muestre la pagina de detalle.
        listaVideojuegosAdapter =new ListaVideojuegosAdapter(new ListaVideojuegosAdapter.ItemClickListener() {
            @Override
            public void onItemClick(VideogameModel videogameModel) {

                intent.putExtra("id_videogame_selected", videogameModel.getId());
                startActivity(intent);
                //Log.d(TAG, videogameModel.getName() + " aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            }
        });

        //__Meto la lista de juegos en el listaVideojuegosAdapter
        recyclerView.setAdapter(listaVideojuegosAdapter);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager=new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy>0){
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if(isScrolling){
                        if((visibleItemCount + pastVisibleItems)>= totalItemCount) {
                            Log.i(TAG, "Final.");

                            isScrolling = false;
                            page += 1;
                            obtenerDatos(page);
                        }
                    }
                }
            }
        });

        retrofit = new Retrofit.Builder().baseUrl("https://api.rawg.io/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        isScrolling = true;
        obtenerDatos(page);
    }

    private void obtenerDatos(int page){
        VideojuegoAPI videojuegoAPI = retrofit.create(VideojuegoAPI.class);
        Call<VideojuegoRespuesta> videojuegoRespuestaCall = videojuegoAPI.obtenerListaVideojuegos(page);

        videojuegoRespuestaCall.enqueue(new Callback<VideojuegoRespuesta>() {
            @Override
            public void onResponse(Call<VideojuegoRespuesta> call, Response<VideojuegoRespuesta> response) {
                isScrolling = true;
                if(response.isSuccessful()){
                    VideojuegoRespuesta videojuegoRespuesta = response.body();
                    videjuegoLista = videojuegoRespuesta.getResults();

                    listaVideojuegosAdapter.addListaVideojuegos(videjuegoLista);
                }else{
                    Log.e(TAG, "onResponse: " +  String.valueOf(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<VideojuegoRespuesta> call, Throwable t) {
                isScrolling = true;
                Log.e(TAG, "onFailure: " +  String.valueOf(t.getMessage()));
            }
        });
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }



//    public ArrayList<VideogameModel> dataqueue(){                                                   //Metodo con el que meto la información que necesito de la API en un ArrayList
//        ArrayList<VideogameModel> holderVideogames=new ArrayList<>();
//
//        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.rawg.io/")
//                .addConverterFactory(GsonConverterFactory.create()).build();
//        VideojuegoAPI videojuegoAPI = retrofit.create(VideojuegoAPI.class);
//
//        ArrayList<Integer> listIds = new ArrayList<Integer>();
//        for(int x=3498;x>3496;x--){
//            listIds.add(x);
//        }
//
//        for(int x=0;x<listIds.size();x++){
//            //Integer id = x;
//            Call<VideogameModel> call = videojuegoAPI.find(listIds.get(x).toString());              //Llama a la API por el metodo de la interfaz find()
//
//            call.enqueue(new Callback<VideogameModel>() {
//                @Override
//                public void onResponse(Call<VideogameModel> call, Response<VideogameModel> response) {
//                    try{
//                        if(response.isSuccessful()){
//                            VideogameModel videojuego = new VideogameModel();
//                            videojuego = response.body();
//                            videojuego.getName();
//                            videojuego.getBackground_image();
//                            holderVideogames.add(videojuego);
//                            //videojuego.getDescription();
//                            //Glide.with(getApplication()).load(videojuego.getBackground_image()).into(imgProducto);
//                        }
//                    }catch(Exception ex){
//                        Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<VideogameModel> call, Throwable t) {
//                    Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT);
//                }
//            });
//        }
//
//        return holderVideogames;
//    }

}