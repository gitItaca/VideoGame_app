package com.example.videogame_app;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.example.videogame_app.interfaces.VideojuegoAPI;
import com.example.videogame_app.models.VideogameModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideogameDetailActivity extends AppCompatActivity {

   private Bundle bundle;
   private Retrofit retrofit;
   private boolean inLoveList, inPlayList, voted;
   private RatingBar ratingBar, ratingGeneral;
   private String userId, idVideogameToString;
   private int idVG;
   private static final String TAG = "VideogameDetailActivity";

   FirebaseAuth fbAuth;
   FirebaseFirestore db;
   VideogameModel videojuego;
   VideogameModel videojuegoRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videogame_detail);

        Button buttonReturn = findViewById(R.id.buttonReturnDetail);
        Button butonLoveList = findViewById(R.id.buttonDetailsToLoveList);
        Button buttonPlayList = findViewById(R.id.buttonDetailsToPlayedList);
        FloatingActionButton buttonSaveLove = findViewById(R.id.addToLoveListButton);
        FloatingActionButton buttonSavePlay = findViewById(R.id.addToPlayListButton);
        ImageView portada = findViewById(R.id.imageVideogameDetail);
        TextView title = findViewById(R.id.nameVideogameDetail);
        TextView description = findViewById(R.id.descriptionVideogameDetail);
        TextView webText = findViewById(R.id.nameWebsiteVideogameDetail);
        ratingBar = findViewById(R.id.ratingBar);
        ratingGeneral = findViewById(R.id.ratingBarGeneral);

        Drawable cruxImg = ResourcesCompat.getDrawable(getResources(), R.drawable.close, null);
        Drawable heartImg = ResourcesCompat.getDrawable(getResources(), R.drawable.heart, null);
        Drawable minusImg = ResourcesCompat.getDrawable(getResources(), R.drawable.minus, null);
        Drawable consoleImg = ResourcesCompat.getDrawable(getResources(), R.drawable.console, null);

        //___Recojo el id del MainActivity.
        bundle = getIntent().getExtras();
        idVG = bundle.getInt("id_videogame_selected");
        idVideogameToString = Integer.toString(idVG);

        //___Inicializo la base de datos.
        fbAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = fbAuth.getCurrentUser().getUid();
        DocumentReference docRefListaDeseo = db.document(userId+"/listaDeseo");
        DocumentReference docRefListaJugados = db.document(userId+"/listaJugados");
        DocumentReference docRefListaValoraciones = db.document(userId+"/listaValoraciones");
        DocumentReference docRefValoracionesGenerales = db.collection("valoraciones").document(idVideogameToString); //userId

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

        //___Leo la lista de valoraciones de mi base de datos, compruebo si el videojuego está y muestro la valoración que tengo guardada.
        docRefListaValoraciones.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Object valoradoPlayListGame = documentSnapshot.get(idVideogameToString);
                    if(valoradoPlayListGame != null){
                        String loadRating = valoradoPlayListGame.toString();
                       //Log.d("RATINGGGGGGGGGGGGGGGG", loadRating);
                        loadRating = StringUtils.substringBetween(loadRating, "personalRating=", ",");
                        ratingBar.setRating(Float.parseFloat(loadRating));

                    }
                }else{
                    Toast.makeText(VideogameDetailActivity.this, "El documento no existe", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //___Leo la lista de valoracionesGenerales de mi base de datos y hago la media entre los usuarios.
        docRefValoracionesGenerales.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        List<String> listVotos = new ArrayList<>();
                        Map<String, Object> map = doc.getData();
                        double total = 0;
                        if(map != null){
                            for(Map.Entry<String, Object> entry : map.entrySet()){
                                listVotos.add(entry.getValue().toString());
                            }
                            for(String vote : listVotos){
                                total += Double.parseDouble(vote);
                            }
                            //Log.d("VOTOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOS", Double.toString(total));
                        }
                        int numVotos = map.size();
                        double media = total/numVotos;
                        ratingGeneral.setRating((float)media);
                        //Log.d("VOTOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOS",Double.toString(media));
                    }
                }
            }
        });

//        docRefValoracionesGenerales.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if(documentSnapshot.exists()){
////                    int numCli = documentSnapshot.getDocumentReference().ge
////
////                    Object valoradoGeneralListGame = documentSnapshot.get(idVideogameToString);
////                    if(valoradoGeneralListGame != null){
////                        String loadRating = valoradoGeneralListGame.toString();
////                        Log.d("RATINGGGGGGGGGGGGGGGG", loadRating);
////                        loadRating = StringUtils.substringBetween(loadRating, "personalRating=", ",");
////                        ratingBar.setRating(Float.parseFloat(loadRating));
//                    }
//                }else{
//                    Toast.makeText(VideogameDetailActivity.this, "El documento no existe", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        //___Guardo los videojuegos con firebase.
        Map<String, Object> videojuegoMap = new HashMap<>();
        Map<String, Object> ratingMap = new HashMap<>();


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

        //Guarda la valoracion del ratingBar.
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //___Add a new personal document with a generated ID
                videojuegoRating = new VideogameModel();
                videojuegoRating.setId(idVG);
                videojuegoRating.setPersonalRating(rating);
                videojuegoMap.put(idVideogameToString, videojuegoRating);

                docRefListaValoraciones.set(videojuegoMap, SetOptions.merge());

                //___Add to the general page
                ratingMap.put(userId, videojuegoRating.getPersonalRating());
                docRefValoracionesGenerales.set(ratingMap, SetOptions.merge());
            }
        });
//_________________Botones menu_______________________________________________________________
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

        //___Boton para ir a la PlayList.
        Intent intentPlayList = new Intent(this, PlayListActivity.class);
        buttonPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentPlayList);
            }
        });

    }

}