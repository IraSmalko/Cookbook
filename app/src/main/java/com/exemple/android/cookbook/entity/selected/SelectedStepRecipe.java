package com.exemple.android.cookbook.entity.selected;


import com.exemple.android.cookbook.entity.StepRecipe;

public class SelectedStepRecipe extends StepRecipe {

    private int idRecipe;

    public SelectedStepRecipe (){}

    public SelectedStepRecipe (String numberStep, String textStep, String photoUrlStep, int idRecipe){
        this.numberStep = numberStep;
        this.textStep = textStep;
        this.photoUrlStep = photoUrlStep;
        this.idRecipe = idRecipe;
    }

    public int getIdRecipe() {
        return idRecipe;
    }

    public void setIdRecipe(int idRecipe) {
        this.idRecipe = idRecipe;
    }
}
