package com.exemple.android.cookbook.entity.selected;


import com.exemple.android.cookbook.entity.StepRecipe;

public class SelectedStepRecipe extends StepRecipe {

    private int idRecipe;

    public SelectedStepRecipe (){}

    public SelectedStepRecipe (String numberStep, String textStep, String photoUrlStep, int idRecipe){
        this.stepNumber = numberStep;
        this.stepText = textStep;
        this.stepPhotoUrl = photoUrlStep;
        this.idRecipe = idRecipe;
    }

    public int getIdRecipe() {
        return idRecipe;
    }

    public void setIdRecipe(int idRecipe) {
        this.idRecipe = idRecipe;
    }
}
