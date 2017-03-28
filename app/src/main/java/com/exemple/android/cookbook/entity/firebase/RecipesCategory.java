package com.exemple.android.cookbook.entity.firebase;


public class RecipesCategory {

    protected String name;
    protected String photoUrl;

    public RecipesCategory() {
    }

    public RecipesCategory(String name, String photoUrl) {
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
