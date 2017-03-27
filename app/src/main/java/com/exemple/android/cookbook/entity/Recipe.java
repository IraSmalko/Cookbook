package com.exemple.android.cookbook.entity;



public class Recipe extends CategoryRecipes {

    protected String description;
    protected int isPersonal;

    private int isInBasket = 0;
    private int isInSaved = 1;

    public Recipe() {
    }

    public Recipe(String name, String photoUrl, String description, int isPersonal) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.description = description;
        this.isPersonal = isPersonal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIsPersonal() {
        return isPersonal;
    }

    public void setIsPersonal(int isPersonal) {
        this.isPersonal = isPersonal;
    }

    public int getIsInBasket() {
        return isInBasket;
    }

    public void setIsInBasket(int isInBasket) {
        this.isInBasket = isInBasket;
    }

    public int getIsInSaved() {
        return isInSaved;
    }

    public void setIsInSaved(int isInSaved) {
        this.isInSaved = isInSaved;
    }
}
