package com.exemple.android.cookbook.entity;


import java.util.ArrayList;
import java.util.List;

public class RecipeForSQLite extends Recipe {

    private List<Ingredient> ingredients = new ArrayList<>();

    public RecipeForSQLite() {
    }

    public RecipeForSQLite(String name, String photoUrl, String description, int isPersonal, List<Ingredient> ingredients) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.description = description;
        this.isPersonal = isPersonal;
        this.ingredients = ingredients;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

}
