package com.exemple.android.cookbook.entity;



public class StepRecipe {

    protected String stepNumber;
    protected String stepText;
    protected String stepPhotoUrl;

    public StepRecipe(){}

    public StepRecipe(String stepNumber, String stepText, String stepPhotoUrl){
        this.stepNumber = stepNumber;
        this.stepText = stepText;
        this.stepPhotoUrl = stepPhotoUrl;
    }

    public String getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(String stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getStepText() {
        return stepText;
    }

    public void setStepText(String stepText) {
        this.stepText = stepText;
    }

    public String getStepPhotoUrl() {
        return stepPhotoUrl;
    }

    public void setStepPhotoUrl(String stepPhotoUrl) {
        this.stepPhotoUrl = stepPhotoUrl;
    }
}
