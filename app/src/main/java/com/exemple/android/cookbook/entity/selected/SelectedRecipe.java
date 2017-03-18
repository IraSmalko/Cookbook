package com.exemple.android.cookbook.entity.selected;

import com.exemple.android.cookbook.entity.Recipe;

public class SelectedRecipe extends Recipe {

    private int idRecipe;

    public SelectedRecipe() {
    }

    public SelectedRecipe(String name, String photoUrl, String description, int idRecipe) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.description = description;
        this.idRecipe = idRecipe;
    }

    public int getIdRecipe() {
        return idRecipe;
    }

    public void setIdRecipe(int idRecipe) {
        this.idRecipe = idRecipe;
    }
}
