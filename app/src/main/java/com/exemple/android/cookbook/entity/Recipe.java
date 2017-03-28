package com.exemple.android.cookbook.entity;



public class Recipe extends CategoryRecipes {

    private int isPersonal;

    private int isInBasket = 0;
    private int isInSaved = 1;

    public Recipe() {
    }

    public Recipe(String name, String photoUrl, int isPersonal) {
        super(name, photoUrl);
        this.isPersonal = isPersonal;
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
