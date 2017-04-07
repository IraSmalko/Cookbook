package com.exemple.android.cookbook.entity;


import java.util.ArrayList;
import java.util.List;

public class RecipeForSQLite extends Recipe {

    private List<Ingredient> ingredients = new ArrayList<>();

    private int isInBasket = 0;
    private int isInSaved = 1;

    private int isEdited = 0;

    public RecipeForSQLite() {
    }

    public RecipeForSQLite(String name, String photoUrl, int isPersonal, List<Ingredient> ingredients) {
        super(name, photoUrl, isPersonal);
        this.ingredients = ingredients;
    }

    public RecipeForSQLite(String name, String photoUrl, int isPersonal,
                           List<Ingredient> ingredients, int isInSaved, int isInBasket) {
        super(name, photoUrl, isPersonal);
        this.ingredients = ingredients;
        this.isInSaved = isInSaved;
        this.isInBasket = isInBasket;
    }

    public RecipeForSQLite(String name, String photoUrl, int isPersonal,
                           List<Ingredient> ingredients, int isInSaved, int isInBasket, int isEdited){
        super(name, photoUrl, isPersonal);
        this.ingredients = ingredients;
        this.isInSaved = isInSaved;
        this.isInBasket = isInBasket;
        this.isEdited = isEdited;
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

    public int getIsEdited() {
        return isEdited;
    }

    public void setIsEdited(int isEdited) {
        this.isEdited = isEdited;
    }
}
