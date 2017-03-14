package com.exemple.android.cookbook.models;

import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.exemple.android.cookbook.entity.Ingredient;

import java.io.ByteArrayOutputStream;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Required;
import io.realm.internal.Context;

/**
 * Created by Sakurov on 14.03.2017.
 */

public class RealmRecipe extends RealmObject {

    private String recipeName;

    private String description;

    private RealmList<RealmIngredient> ingredients;

    private byte[] photo;

    public void setRecipe(String recipeName, String description, RealmList<RealmIngredient> ingredients) {
        this.recipeName = recipeName;
        this.description = description;
        this.ingredients = ingredients;
    }

    public void setRecipe(String recipeName, String description, List<Ingredient> ingredients) {
        this.recipeName = recipeName;
        this.description = description;
        this.setIngredients(ingredients);
    }

    public void setRecipe(android.content.Context context, String recipeName, String description, RealmList<RealmIngredient> ingredients, String photoUrl) {
        this.recipeName = recipeName;
        this.description = description;
        this.ingredients = ingredients;
        Glide.with(context).load(photoUrl).asBitmap().into(new SimpleTarget<Bitmap>(660, 480) {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                resource.compress(Bitmap.CompressFormat.PNG, 100, stream);
                photo = stream.toByteArray();
            }
        });
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

    public RealmList<RealmIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(RealmList<RealmIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void setIngredients(List<Ingredient> ingredientsList) {
        for (Ingredient ingredient : ingredientsList) {
            ingredients.add(new RealmIngredient(ingredient));
        }
    }
}
