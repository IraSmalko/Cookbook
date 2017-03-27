package com.exemple.android.cookbook.entity.firebase;

import android.support.annotation.Keep;

import java.util.HashMap;

/**
 * Created by Sakurov on 15.03.2017.
 */
@Keep
public class FirebaseRecipe {
    private String name;
    private String description;
    private String photoUrl;
    private HashMap<String, FirebaseIngredient> ingredients;
    private HashMap<String, FirebaseStepRecipe> steps;

    private int isPersonal;

    private String recipeId;

    public FirebaseRecipe() {
    }

    public FirebaseRecipe(String name,
                          String photoUrl,
                          String description,
                          int isPersonal) {
        this.name = name;
        this.description = description;
        this.photoUrl = photoUrl;
        this.isPersonal = isPersonal;
    }

    public FirebaseRecipe(String name,
                          String description,
                          String photoUrl,
                          HashMap<String, FirebaseIngredient> ingredients,
                          HashMap<String, FirebaseStepRecipe> steps) {
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

    public HashMap<String, FirebaseIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(HashMap<String, FirebaseIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public HashMap<String, FirebaseStepRecipe> getSteps() {
        return steps;
    }

    public void setSteps(HashMap<String, FirebaseStepRecipe> steps) {
        this.steps = steps;
    }

    public int getIsPersonal() {
        return isPersonal;
    }

    public void setIsPersonal(int isPersonal) {
        this.isPersonal = isPersonal;
    }
}
