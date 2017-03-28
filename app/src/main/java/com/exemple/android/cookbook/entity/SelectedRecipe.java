package com.exemple.android.cookbook.entity;



public class SelectedRecipe extends CategoryRecipes {

    private int idRecipe;

    private int isInBasket = 0;
    private int isInSaved = 1;

    public SelectedRecipe() {
    }

    public SelectedRecipe(String name, String photoUrl, int idRecipe) {
        super(name, photoUrl);
        this.idRecipe = idRecipe;
    }

    public int getIdRecipe() {
        return idRecipe;
    }

    public void setIdRecipe(int idRecipe) {
        this.idRecipe = idRecipe;
    }


}
