package com.exemple.android.cookbook.supporting;



public class StepRecipe {

    private String numberStep;
    private String textStep;
    private String photoUrlStep;

    public StepRecipe(){}

    public StepRecipe(String numberStep, String textStep, String photoUrlStep){
        this.numberStep = numberStep;
        this.textStep = textStep;
        this.photoUrlStep = photoUrlStep;
    }

    public String getNumberStep() {
        return numberStep;
    }

    public void setNumberStep(String numberStep) {
        this.numberStep = numberStep;
    }

    public String getTextStep() {
        return textStep;
    }

    public void setTextStep(String textStep) {
        this.textStep = textStep;
    }

    public String getPhotoUrlStep() {
        return photoUrlStep;
    }

    public void setPhotoUrlStep(String photoUrlStep) {
        this.photoUrlStep = photoUrlStep;
    }
}
