package com.example.videogame_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.Toast;

import com.example.videogame_app.interfaces.VideojuegoAPI;
import com.example.videogame_app.models.VideogameModel;
import com.example.videogame_app.models.VideojuegoRespuesta;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoveListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ListaVideojuegosAdapter listaVideojuegosAdapter;
    private String id, img, name,allInfo;
    FirebaseAuth fbAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_love_list);

        recyclerView = findViewById(R.id.recyclerViewLove);
        Button home = findViewById(R.id.buttonLoveListToMain);
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

        //___Meto la lista de juegos en el listaVideojuegosAdapter
        recyclerView.setAdapter(listaVideojuegosAdapter);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager=new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        //___Inicializo la base de datos.
        fbAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String userId = fbAuth.getCurrentUser().getUid();
        DocumentReference docRefListaDeseo = db.document(userId+"/listaDeseo");

        //___Leo la lista de mi base de datos.
        docRefListaDeseo.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();

                //___Aqui cojo toda la info en un String, habria que separar y coger la info como interesa
                Collection models = document.getData().values();
                ArrayList<VideogameModel> videojuegoLista = new ArrayList<VideogameModel>();
                for(Object item : models){
                    allInfo= String.valueOf(item);

                    id = StringUtils.substringBetween(allInfo, ", id=","}");
                    name = StringUtils.substringBetween(allInfo, ", name=",", description=");
                    img = StringUtils.substringBetween(allInfo, "background_image=",", website=");
                    //Log.d("LOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOVE LIST", id + " " + name);
                    VideogameModel vgModel = new VideogameModel();
                    vgModel.setId(Integer.parseInt(id));
                    vgModel.setName(name);
                    vgModel.setBackground_image(img);
                    videojuegoLista.add(vgModel);

                }

                listaVideojuegosAdapter.addListaVideojuegos(videojuegoLista);

                //___Aquí cojo solo los id de los juegos.
//                setListDeseoId = document.getData().keySet();
//                for(String idGame : setListDeseoId){
//                    Log.d("LOVE LISTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT", idGame);
//                }

            }//FIN onComplete
        });//FIN addOnComplete

        //___Boton para volver a la pagina principal.
        Intent intentHome = new Intent(this, MainActivity.class);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentHome);
            }
        });

    }//FIN onCreate

}//FIN class LoveListActivity




