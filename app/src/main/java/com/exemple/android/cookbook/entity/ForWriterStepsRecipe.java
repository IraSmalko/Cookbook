package com.exemple.android.cookbook.entity;


import java.util.List;

public class ForWriterStepsRecipe {

    private List<StepRecipe> stepRecipes;
    private String pathPhotoStep;
    private int idRecipe;
    private int iterator;

    public ForWriterStepsRecipe(List<StepRecipe> stepRecipes, String pathPhotoStep, int idRecipe, int iterator){
        this.stepRecipes = stepRecipes;
        this.pathPhotoStep = pathPhotoStep;
        this.idRecipe = idRecipe;
        this.iterator = iterator;
    }

    public List<StepRecipe> getStepRecipes() {
        return stepRecipes;
    }

    public void setStepRecipes(List<StepRecipe> stepRecipes) {
        this.stepRecipes = stepRecipes;
    }

    public int getIdRecipe() {
        return idRecipe;
    }

    public void setIdRecipe(int idRecipe) {
        this.idRecipe = idRecipe;
    }

    public String getPathPhotoStep() {
        return pathPhotoStep;
    }

    public void setPathPhotoStep(String pathPhotoStep) {
        this.pathPhotoStep = pathPhotoStep;
    }

    public int getIterator() {
        return iterator;
    }

    public void setIterator(int iterator) {
        this.iterator = iterator;
    }
}
