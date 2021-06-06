package com.example.videogame_app.models;

public class VideogameModel {

    int id;
    String name;
    String background_image;
    String description;
    String website;
    float personalRating;


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackground_image() {
        return background_image;
    }

    public void setBackground_image(String background_image) {
        this.background_image = background_image;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }
    public void setWebsite(String website) {
        this.website = website;
    }

    public float getPersonalRating() {
        return personalRating;
    }
    public void setPersonalRating(float personalRating) {
        this.personalRating = personalRating;
    }

}
