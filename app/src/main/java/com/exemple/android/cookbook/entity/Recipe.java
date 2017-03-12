package com.exemple.android.cookbook.entity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Recipe extends CategoryRecipes {

    protected String description;

    private HashMap<String, Ingredient> ingredientsHashMap;

    private List<Ingredient> ingredients = new ArrayList<>();

    public Recipe() {
    }

    public Recipe(String name, String photoUrl, String description) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.description = description;
        for (String key : ingredientsHashMap.keySet()) {
                ingredients.add(ingredientsHashMap.get(key));
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public void setIngredients(List<Ingredient> ingredients){
//        this.ingredients = ingredients;
//    }
//
//    public void addIngredient(Ingredient ingredient){
//        ingredients.add(ingredient);
//    }
//
//    public List<Ingredient> getIngredients(){
//        return ingredients;
//    }
//
//    public int getIngredientsCount(){
//        return ingredients.size();
//    }

}
