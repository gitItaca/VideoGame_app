package com.example.videogame_app.interfaces;

import com.example.videogame_app.models.VideogameModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface VideojuegoAPI {

    @GET("api/games/{id}?key=0ae4a974e9844e94859f16ff0bd5202b")
    public Call<VideogameModel> find(@Path("id") String id);

}
