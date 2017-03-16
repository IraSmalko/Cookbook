package com.exemple.android.cookbook.models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import io.realm.RealmObject;

/**
 * Created by Sakurov on 15.03.2017.
 */

public class Ingredient extends RealmObject {

    private String name;
    private float quantity;
    private String unit;

    public Ingredient() {
    }

    public Ingredient(FirebaseIngredient firebaseIngredient) {
        name = firebaseIngredient.getName();
        quantity = firebaseIngredient.getQuantity();
        unit = firebaseIngredient.getUnit();
    }

    public Ingredient(Ingredient ingredient){
        name = ingredient.getName();
        quantity = ingredient.getQuantity();
        unit = ingredient.getUnit();
    }

    public Ingredient(String name, float quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public Ingredient(DataSnapshot dataSnapshot) {
        Ingredient ingredient = dataSnapshot.getValue(Ingredient.class);
        name = ingredient.getName();
        quantity = ingredient.getQuantity();
        unit = ingredient.getUnit();
    }

    public String getName() {
        return name;
    }

    public float getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
