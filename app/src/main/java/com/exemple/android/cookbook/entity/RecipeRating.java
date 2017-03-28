package com.exemple.android.cookbook.entity;

/**
 * Created by Sakurov on 28.03.2017.
 */

public class RecipeRating {
    private float rating;
    private int numberOfUsers;

    RecipeRating() {
        this.rating = 0;
        this.numberOfUsers = 0;
    }

    public void addRating(float rating) {
        this.rating = (this.rating * this.numberOfUsers + rating) / (this.numberOfUsers + 1);
        this.numberOfUsers++;
    }

    public float getRating() {
        return rating;
    }

    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public void editRating(float oldRating, float newRating) {
        rating = (rating * numberOfUsers - oldRating + newRating) / numberOfUsers;
    }
}