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

    public FirebaseRecipe() {
    }

    public FirebaseRecipe(String name,
                          String description,
                          String photoUrl){
        this.name = name;
        this.description = description;
        this.photoUrl = photoUrl;
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
}
