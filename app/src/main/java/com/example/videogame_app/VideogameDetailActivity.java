package com.example.videogame_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.videogame_app.interfaces.VideojuegoAPI;
import com.example.videogame_app.models.UserModel;
import com.example.videogame_app.models.VideogameModel;
import com.example.videogame_app.models.VideojuegoRespuesta;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.os.Build.VERSION_CODES.M;

public class VideogameDetailActivity extends AppCompatActivity {

   private Bundle bundle;
   private Retrofit retrofit;
   private static final String TAG = "VideogameDetailActivity";

   FirebaseAuth fbAuth;
   FirebaseFirestore db;
   VideogameModel videojuego;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videogame_detail);

        Button buttonReturn = findViewById(R.id.buttonReturnDetail);
        FloatingActionButton buttonSaveLove = findViewById(R.id.addToLoveListButton);
        ImageView portada = findViewById(R.id.imageVideogameDetail);
        TextView title = findViewById(R.id.nameVideogameDetail);
        TextView description = findViewById(R.id.descriptionVideogameDetail);
        TextView webText = findViewById(R.id.nameWebsiteVideogameDetail);

        //___Inicializo la base de datos
        fbAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String userId = fbAuth.getCurrentUser().getUid();
        //___Guardo los videojuegos con firebase
        Map<String, Object> videojuegoMap = new HashMap<>();
        Map<String, Object> userMap = new HashMap<>();

        //___Recojo el id del MainActivity
        bundle = getIntent().getExtras();
        int id = bundle.getInt("id_videogame_selected");
        String idToAPI = Integer.toString(id);
        //Log.d(TAG, String.valueOf(id));

        //___Creo el intent y le asocio la pagina de la web
        Intent intentWeb = new Intent(this, WebsiteVideogameActivity.class);

        //___Llamo a la API
        retrofit = new Retrofit.Builder().baseUrl("https://api.rawg.io/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        VideojuegoAPI videojuegoAPI = retrofit.create(VideojuegoAPI.class);
        Call<VideogameModel> videogameDetailCall = videojuegoAPI.find(idToAPI);

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

        //___Boton para enviar el videojuego a mi lista y guardarlo
        buttonSaveLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userMap.put("IdUser", userId);

                //___Meto la info que quiero guardar en el objeto videojuegoMap
                //videojuegoMap.put("IdGame", videojuego.getId());
                UserModel userm = new UserModel();
                userm.setIdUser(userId);
                ArrayList<Integer> listaDeseo = new ArrayList<>();
                listaDeseo.add(videojuego.getId());
                userm.setListaDeseo(listaDeseo);

//                db.collection("users")
//                        .document(userId)
//                        .set(userm)
//                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                            @Override
//                            public void onSuccess(DocumentReference documentReference) {
//                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.w(TAG, "Error adding document", e);
//                            }
//                        });
                //___Add a new document with a generated ID
                db.collection("videojuegos")
                        .add(videojuegoMap)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }
        });

        //___Boton para volver a la pagina principal
        Intent intentVolver = new Intent(this, MainActivity.class);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentVolver);
            }
        });

    }

}