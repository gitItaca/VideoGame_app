package com.example.videogame_app.models;

import java.util.ArrayList;

public class UserModel {
    String idUser;
    ArrayList<Integer> listaFavoritos;
    ArrayList<Integer> listaDeseo;

    public String getIdUser() {
        return idUser;
    }
    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public ArrayList<Integer> getListaFavoritos() {
        return listaFavoritos;
    }

    public void setListaFavoritos(ArrayList<Integer> listaFavoritos) {
        this.listaFavoritos = listaFavoritos;
    }

    public ArrayList<Integer> getListaDeseo() {
        return listaDeseo;
    }

    public void setListaDeseo(ArrayList<Integer> listaDeseo) {
        this.listaDeseo = listaDeseo;
    }
}
