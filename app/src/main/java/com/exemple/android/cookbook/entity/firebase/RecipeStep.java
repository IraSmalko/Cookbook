package com.exemple.android.cookbook.entity.firebase;

import android.support.annotation.Keep;

/**
 * Created by Sakurov on 16.03.2017.
 */
@Keep
public class RecipeStep {
    private String stepNumber;
    private String stepText;
    private String stepPhotoUrl;

    public RecipeStep() {
    }

    public RecipeStep(String stepNumber, String stepPhotoUrl, String stepText) {
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
