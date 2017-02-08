package com.exemple.android.cookbook.entity;


import com.exemple.android.cookbook.entity.CategoryRecipes;

public class Recipe extends CategoryRecipes {

    protected String description;

    public Recipe() {
    }

    public Recipe(String name, String photoUrl, String description) {
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
