package com.example.videogame_app;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.example.videogame_app.interfaces.VideojuegoAPI;
import com.example.videogame_app.models.VideogameModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.jsoup.Jsoup;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideogameDetailActivity extends AppCompatActivity {

   private Bundle bundle;
   private Retrofit retrofit;
   private boolean inLoveList;
    private boolean inPlayList;
   private static final String TAG = "VideogameDetailActivity";

   FirebaseAuth fbAuth;
   FirebaseFirestore db;
   VideogameModel videojuego;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videogame_detail);

        Button buttonReturn = findViewById(R.id.buttonReturnDetail);
        Button butonLoveList = findViewById(R.id.buttonDetailsToLoveList);
        FloatingActionButton buttonSaveLove = findViewById(R.id.addToLoveListButton);
        FloatingActionButton buttonSavePlay = findViewById(R.id.addToPlayListButton);
        ImageView portada = findViewById(R.id.imageVideogameDetail);
        TextView title = findViewById(R.id.nameVideogameDetail);
        TextView description = findViewById(R.id.descriptionVideogameDetail);
        TextView webText = findViewById(R.id.nameWebsiteVideogameDetail);

        Drawable cruxImg = ResourcesCompat.getDrawable(getResources(), R.drawable.close, null);
        Drawable heartImg = ResourcesCompat.getDrawable(getResources(), R.drawable.heart, null);
        Drawable minusImg = ResourcesCompat.getDrawable(getResources(), R.drawable.minus, null);
        Drawable consoleImg = ResourcesCompat.getDrawable(getResources(), R.drawable.console, null);

        //___Recojo el id del MainActivity.
        bundle = getIntent().getExtras();
        int id = bundle.getInt("id_videogame_selected");
        String idVideogameToString = Integer.toString(id);

        //___Inicializo la base de datos.
        fbAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String userId = fbAuth.getCurrentUser().getUid();
        DocumentReference docRefListaDeseo = db.document(userId+"/listaDeseo");
        DocumentReference docRefListaJugados = db.document(userId+"/listaJugados");

        //___Leo la lista de deseo de mi base de datos y compruebo si el videojuego está.
        docRefListaDeseo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Object savedLoveListGame = documentSnapshot.get(idVideogameToString);
                    if(savedLoveListGame == null){
                        buttonSaveLove.setImageDrawable(heartImg);
                        inLoveList = false;
                    }else{
                        buttonSaveLove.setImageDrawable(cruxImg);
                        inLoveList = true;
                    }
                }else{
                    Toast.makeText(VideogameDetailActivity.this, "El documento no existe", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //___Leo la lista de jugados de mi base de datos y compruebo si el videojuego está.
        docRefListaJugados.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Object savedPlayListGame = documentSnapshot.get(idVideogameToString);
                    if(savedPlayListGame == null){
                        buttonSavePlay.setImageDrawable(consoleImg);
                        inPlayList = false;
                    }else{
                        buttonSavePlay.setImageDrawable(minusImg);
                        inPlayList = true;
                    }
                }else{
                    Toast.makeText(VideogameDetailActivity.this, "El documento no existe", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //___Guardo los videojuegos con firebase.
        Map<String, Object> videojuegoMap = new HashMap<>();

        //___Creo el intent y le asocio la pagina de la web.
        Intent intentWeb = new Intent(this, WebsiteVideogameActivity.class);

        //___Llamo a la API.
        retrofit = new Retrofit.Builder().baseUrl("https://api.rawg.io/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        VideojuegoAPI videojuegoAPI = retrofit.create(VideojuegoAPI.class);
        Call<VideogameModel> videogameDetailCall = videojuegoAPI.find(idVideogameToString);

        videogameDetailCall.enqueue(new Callback<VideogameModel>() {
            @Override
            public void onResponse(Call<VideogameModel> call, Response<VideogameModel> response) {
                if(response.isSuccessful()){
                    videojuego = new VideogameModel();
                    videojuego = response.body();

                    //__Recojo en variables la info del model que he recogido con la API
                    String imgPortada = videojuego.getBackground_image();
                    String name = videojuego.getName();
                    String descr = videojuego.getDescription();
                    String web = videojuego.getWebsite();
                    //__Quito las HTML tags de la descripcion
                    descr = Jsoup.parse(descr).text();
                    //__Asigno al layout la info
                    title.setText(name);
                    description.setText(descr);
                    webText.setText(web);
                    Glide.with(getApplication()).load(imgPortada).into(portada);

                    if(webText!= null) {
                        //__Hago del texto un boton
                        webText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                intentWeb.putExtra("URL", web);
                                startActivity(intentWeb);
                            }
                        });
                    }


                }else{
                    Log.e(TAG, "onResponse FAIL: " +  String.valueOf(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<VideogameModel> call, Throwable t) {
                Log.e(TAG, "onFailure: " +  String.valueOf(t.getMessage()));
            }
        });

        //___Boton para enviar el videojuego a mi lista de deseos y guardarlo o borrarlo.
        buttonSaveLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(inLoveList==false) {
                    //___Add a new document with a generated ID
                    videojuegoMap.put(idVideogameToString, videojuego);

                    docRefListaDeseo.set(videojuegoMap, SetOptions.merge());

                    buttonSaveLove.setImageDrawable(cruxImg);
                }else {
                    //___Delete
                    docRefListaDeseo.update(idVideogameToString, FieldValue.delete());

                    buttonSaveLove.setImageDrawable(heartImg);
                }
                inLoveList=!inLoveList;
            }
        });

        //___Boton para enviar el videojuego a mi lista de jugados y guardarlo o borrarlo.
        buttonSavePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(inPlayList==false) {
                    //___Add a new document with a generated ID
                    videojuegoMap.put(idVideogameToString, videojuego);

                    docRefListaJugados.set(videojuegoMap, SetOptions.merge());

                    buttonSavePlay.setImageDrawable(minusImg);
                }else {
                    //___Delete
                    docRefListaJugados.update(idVideogameToString, FieldValue.delete());

                    buttonSavePlay.setImageDrawable(consoleImg);
                }
                inPlayList=!inPlayList;
            }
        });

        //___Boton para volver a la pagina principal.
        Intent intentHome = new Intent(this, MainActivity.class);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentHome);
            }
        });

        //___Boton para ir a la LoveList.
        Intent intentLoveList = new Intent(this, LoveListActivity.class);
        butonLoveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentLoveList);
            }
        });

    }

}