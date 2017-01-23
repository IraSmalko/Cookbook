package com.exemple.android.cookbook.supporting;


public class Recipes extends CategoryRecipes {

    private String description;

    public Recipes() {
    }

    public Recipes(String name, String photoUrl, String description) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
