package com.exemple.android.cookbook.entity;


import java.util.ArrayList;
import java.util.List;

public class RecipeForSQLite extends Recipe {

    private List<Ingredient> ingredients = new ArrayList<>();

    private int isInBasket = 0;
    private int isInSaved = 1;

    public RecipeForSQLite() {
    }

    public RecipeForSQLite(String name, String photoUrl, String description, int isPersonal, List<Ingredient> ingredients) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.description = description;
        this.isPersonal = isPersonal;
        this.ingredients = ingredients;
    }

    public RecipeForSQLite(String name, String photoUrl, String description, int isPersonal,
                           List<Ingredient> ingredients, int isInSaved, int isInBasket) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.description = description;
        this.isPersonal = isPersonal;
        this.ingredients = ingredients;
        this.isInSaved = isInSaved;
        this.isInBasket = isInBasket;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public int getIsInBasket() {
        return isInBasket;
    }

    @Override
    public void setIsInBasket(int isInBasket) {
        this.isInBasket = isInBasket;
    }

    @Override
    public int getIsInSaved() {
        return isInSaved;
    }

    @Override
    public void setIsInSaved(int isInSaved) {
        this.isInSaved = isInSaved;
    }
}
