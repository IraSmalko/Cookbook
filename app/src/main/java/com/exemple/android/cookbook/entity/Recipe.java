package com.exemple.android.cookbook.entity;

public class Recipe extends CategoryRecipes {

    protected String description;
    protected int isPersonal;

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
}
