package com.exemple.android.cookbook.entity.firebase;

import android.support.annotation.Keep;

import java.util.HashMap;

/**
 * Created by Sakurov on 15.03.2017.
 */
@Keep
public class Recipe {

    private String name;
    private String description;
    private String photoUrl;
    private HashMap<String, RecipeIngredient> ingredients;
    private HashMap<String, RecipeStep> steps;

    private int isPersonal;

    private String recipeId;

    public Recipe() {
    }

    public Recipe(String name,
                  String photoUrl,
                  String description,
                  int isPersonal) {
        this.name = name;
        this.description = description;
        this.photoUrl = photoUrl;
        this.isPersonal = isPersonal;
    }

    public Recipe(String name,
                  String description,
                  String photoUrl,
                  HashMap<String, RecipeIngredient> ingredients,
                  HashMap<String, RecipeStep> steps) {
        this.name = name;
        this.description = description;
        this.photoUrl = photoUrl;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public HashMap<String, RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(HashMap<String, RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public HashMap<String, RecipeStep> getSteps() {
        return steps;
    }

    public void setSteps(HashMap<String, RecipeStep> steps) {
        this.steps = steps;
    }

    public int getIsPersonal() {
        return isPersonal;
    }

    public void setIsPersonal(int isPersonal) {
        this.isPersonal = isPersonal;
    }
}
