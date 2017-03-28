package com.exemple.android.cookbook.entity.firebase;

import android.support.annotation.Keep;

import com.exemple.android.cookbook.entity.realm.RealmIngredient;

/**
 * Created by Sakurov on 16.03.2017.
 */
@Keep
public class RecipeIngredient {
    private String name;
    private float quantity;
    private String unit;

    private String id;

    public RecipeIngredient() {
    }

    public RecipeIngredient(String name, float quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public RecipeIngredient(RealmIngredient realmIngredient){
        this.name = realmIngredient.getName();
        this.quantity = realmIngredient.getQuantity();
        this.unit = realmIngredient.getUnit();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
