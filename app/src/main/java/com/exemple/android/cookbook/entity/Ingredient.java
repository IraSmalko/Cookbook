package com.exemple.android.cookbook.entity;


public class Ingredient {
    private String name;
    private float quantity;
    private String unit;

    public Ingredient(){}

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
}
