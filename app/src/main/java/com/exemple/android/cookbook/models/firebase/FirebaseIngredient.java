package com.exemple.android.cookbook.models.firebase;

import android.support.annotation.Keep;

/**
 * Created by Sakurov on 16.03.2017.
 */
@Keep
public class FirebaseIngredient {
    private String name;
    private float quantity;
    private String unit;

    public FirebaseIngredient() {
    }

    public FirebaseIngredient(String name, float quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
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
}
