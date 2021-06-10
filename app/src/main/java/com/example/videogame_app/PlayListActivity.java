package com.example.videogame_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.videogame_app.models.VideogameModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

public class PlayListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ListaVideojuegosAdapter listaVideojuegosAdapter;
    private String id, img, name,allInfo;
    private Button home, loveButton;
    private TextView viewUsername;
    FirebaseAuth fbAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        recyclerView = findViewById(R.id.recyclerViewPlay);
        home = findViewById(R.id.buttonPlayListToMain);
        loveButton = findViewById(R.id.buttonPlayToLoveList);
        viewUsername = findViewById(R.id.textViewNombreUserPlayList);

        //___Creo el intent y le asocio la pagina de detalle
        Intent intent = new Intent(this, VideogameDetailActivity.class);

        //___Al adapter le meto el item click y le asocio el intent para que muestre la pagina de detalle.
        listaVideojuegosAdapter =new ListaVideojuegosAdapter(new ListaVideojuegosAdapter.ItemClickListener() {
            @Override
            public void onItemClick(VideogameModel videogameModel) {
                intent.putExtra("id_videogame_selected", videogameModel.getId());
                startActivity(intent);
            }
        });

        //___Meto la lista de juegos en el listaVideojuegosAdapter
        recyclerView.setAdapter(listaVideojuegosAdapter);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager=new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        //___Inicializo la base de datos.
        fbAuth = FirebaseAuth.getInstance();
            String emailUser = fbAuth.getCurrentUser().getEmail();
            viewUsername.setText("Hello " + emailUser+ "!");
        db = FirebaseFirestore.getInstance();
        String userId = fbAuth.getCurrentUser().getUid();
        DocumentReference docRefListaDeseo = db.document(userId+"/listaJugados");

        //___Leo la lista de mi base de datos.
        docRefListaDeseo.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();


                    //___Aqui cojo toda la info en un String y la separo para coger lo que me interesa.
                    Collection models = document.getData().values();
                    ArrayList<VideogameModel> videojuegoLista = new ArrayList<VideogameModel>();
                    for(Object item : models){
                        allInfo= String.valueOf(item);

                        id = StringUtils.substringBetween(allInfo, ", id=","}");
                        name = StringUtils.substringBetween(allInfo, ", name=",", description=");
                        img = StringUtils.substringBetween(allInfo, "background_image=",", website=");

                        VideogameModel vgModel = new VideogameModel();
                        vgModel.setId(Integer.parseInt(id));
                        vgModel.setName(name);
                        vgModel.setBackground_image(img);
                        videojuegoLista.add(vgModel);
                    }
                    //Meto la lista de ideojuegos en el adapter.
                    listaVideojuegosAdapter.addListaVideojuegos(videojuegoLista);


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

        //___Boton para volver a la pagina love list.
        Intent intentLove = new Intent(this, LoveListActivity.class);
        loveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentLove);
            }
        });

    }//FIN onCreate

}