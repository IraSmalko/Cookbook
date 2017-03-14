package com.exemple.android.cookbook.models;

import com.exemple.android.cookbook.entity.Ingredient;

import io.realm.RealmObject;

/**
 * Created by Sakurov on 14.03.2017.
 */

public class RealmIngredient extends RealmObject {

    private String name;
    private float quantity;
    private String unit;

    public RealmIngredient() {
        name = "test";
        quantity = 0;
        unit = "kg";
    }

    public RealmIngredient(Ingredient ingredient){
        name = ingredient.getName();
        quantity = ingredient.getQuantity();
        unit = ingredient.getUnit();
    }

    public RealmIngredient(String name, float quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
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
