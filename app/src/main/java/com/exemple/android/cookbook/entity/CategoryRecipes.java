package com.exemple.android.cookbook.entity;


public class CategoryRecipes {

    private String name;
    private String photoUrl;

    public CategoryRecipes() {
    }

    public CategoryRecipes(String name, String photoUrl) {
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

}
