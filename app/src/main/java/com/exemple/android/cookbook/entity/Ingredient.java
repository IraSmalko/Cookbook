package com.exemple.android.cookbook.entity;

/**
 * Created by Sakurov on 12.03.2017.
 */

public class Ingredient {
    private String name;
    private float quantity;
    private String unit;

    private String id;

    public Ingredient() {
        name = "test";
        quantity = 0;
        unit = "kg";
    }

    public Ingredient(String name, float quantity, String unit) {
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

    public void setId(String id) {
        this.id = id;
    }
}
