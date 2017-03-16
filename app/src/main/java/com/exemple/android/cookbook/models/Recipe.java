package com.exemple.android.cookbook.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.ByteArrayOutputStream;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Sakurov on 15.03.2017.
 */

public class Recipe extends RealmObject {

    private String recipeName;
    private String description;
    private String photoUrl;

    private RealmList<Ingredient> ingredients;
    private RealmList<StepRecipe> steps;

    private byte[] photo;

    public void Recipe() {
    }

    public void setRecipe(String recipeName,
                          String description,
                          String photoUrl,
                          RealmList<Ingredient> ingredients,
                          RealmList<StepRecipe> steps) {
        this.recipeName = recipeName;
        this.description = description;
        this.photoUrl = photoUrl;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public void setRecipe(Recipe recipe) {
        this.recipeName = recipe.getRecipeName();
        this.description = recipe.getDescription();
        this.photoUrl = recipe.getPhotoUrl();
        this.ingredients = recipe.getIngredients();
        this.steps = recipe.getSteps();
        if (recipe.getPhoto() != null) {
            this.photo = recipe.getPhotoByteArray();
        }
    }

    public void setRecipe(FirebaseRecipe firebaseRecipe) {
        this.recipeName = firebaseRecipe.getName();
        this.description = firebaseRecipe.getDescription();
        this.photoUrl = firebaseRecipe.getPhotoUrl();

        for (FirebaseIngredient firebaseIngredient :
                firebaseRecipe.getIngredients().values()) {
            ingredients.add(new Ingredient(firebaseIngredient));
        }

        for (FirebaseStepRecipe firebaseStepRecipe :
                firebaseRecipe.getSteps().values()) {
            steps.add(new StepRecipe(firebaseStepRecipe));
        }
    }


    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public RealmList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(RealmList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public RealmList<StepRecipe> getSteps() {
        return steps;
    }

    public void setSteps(RealmList<StepRecipe> steps) {
        this.steps = steps;
    }

    public byte[] getPhotoByteArray() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public void savePhoto(final Context context, final Realm realm) {
        if (context != null && photoUrl != null) {
            Glide.with(context).load(photoUrl).asBitmap().into(new SimpleTarget<Bitmap>(660, 480) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    resource.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    realm.beginTransaction();
                    photo = stream.toByteArray();
                    realm.commitTransaction();
                    Toast.makeText(context, "photo saved", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void saveStepsPhoto(Context context) {
        if (context != null && steps != null) {
            for (StepRecipe step : steps) {
                step.saveStepPhoto(context);
            }
        }
    }

    public Bitmap getPhoto() {
        Bitmap bitmap = null;
        if (photo != null) {
            bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        }
        return bitmap;
    }

}
