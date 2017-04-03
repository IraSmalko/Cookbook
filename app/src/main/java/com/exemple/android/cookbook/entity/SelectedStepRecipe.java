package com.exemple.android.cookbook.entity;


public class SelectedStepRecipe extends StepRecipe {

    private int idRecipe;

    public SelectedStepRecipe (){}

    public SelectedStepRecipe(StepRecipe stepRecipe, int idRecipe){
        this.numberStep = stepRecipe.getNumberStep();
        this.textStep = stepRecipe.getTextStep();
        this.photoUrlStep = stepRecipe.getPhotoUrlStep();
        this.idRecipe = idRecipe;
    }

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
