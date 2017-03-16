package com.exemple.android.cookbook.models;

import android.support.annotation.Keep;

/**
 * Created by Sakurov on 16.03.2017.
 */
@Keep
public class FirebaseStepRecipe {
    private String stepNumber;
    private String stepText;
    private String stepPhotoUrl;

    public FirebaseStepRecipe() {
    }

    public FirebaseStepRecipe(String stepNumber, String stepPhotoUrl, String stepText) {
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
