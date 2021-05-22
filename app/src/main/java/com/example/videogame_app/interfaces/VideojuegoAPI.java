package com.example.videogame_app.interfaces;

import com.example.videogame_app.models.VideogameModel;
import com.example.videogame_app.models.VideojuegoRespuesta;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VideojuegoAPI {

    @GET("api/games/{id}?key=d5007a1d7594498b9e178bb524a3c666")
    Call<VideogameModel> find(@Path("id") String id);

    @GET("api/games?key=d5007a1d7594498b9e178bb524a3c666")
    Call<VideojuegoRespuesta> obtenerListaVideojuegos(@Query("page") int page);
}
