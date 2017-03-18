package com.exemple.android.cookbook.models.realm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.exemple.android.cookbook.models.firebase.FirebaseIngredient;
import com.exemple.android.cookbook.models.firebase.FirebaseRecipe;
import com.exemple.android.cookbook.models.firebase.FirebaseStepRecipe;

import java.io.ByteArrayOutputStream;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Sakurov on 15.03.2017.
 */

public class RealmRecipe extends RealmObject {

    private String recipeName;
    private String description;
    private String photoUrl;

    private RealmList<RealmIngredient> realmIngredients;
    private RealmList<RealmStepRecipe> steps;

    private byte[] photo;

    public void Recipe() {
    }

    public void setRecipe(String recipeName,
                          String description,
                          String photoUrl,
                          RealmList<RealmIngredient> realmIngredients,
                          RealmList<RealmStepRecipe> steps) {
        this.recipeName = recipeName;
        this.description = description;
        this.photoUrl = photoUrl;
        this.realmIngredients = realmIngredients;
        this.steps = steps;
    }

    public void setRecipe(RealmRecipe realmRecipe) {
        this.recipeName = realmRecipe.getRecipeName();
        this.description = realmRecipe.getDescription();
        this.photoUrl = realmRecipe.getPhotoUrl();
        this.realmIngredients = realmRecipe.getIngredients();
        this.steps = realmRecipe.getSteps();
        if (realmRecipe.getPhoto() != null) {
            this.photo = realmRecipe.getPhotoByteArray();
        }
    }

    public void setRecipe(FirebaseRecipe firebaseRecipe) {
        this.recipeName = firebaseRecipe.getName();
        this.description = firebaseRecipe.getDescription();
        this.photoUrl = firebaseRecipe.getPhotoUrl();

        for (FirebaseIngredient firebaseIngredient :
                firebaseRecipe.getIngredients().values()) {
            realmIngredients.add(new RealmIngredient(firebaseIngredient));
        }

        for (FirebaseStepRecipe firebaseStepRecipe :
                firebaseRecipe.getSteps().values()) {
            steps.add(new RealmStepRecipe(firebaseStepRecipe));
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

    public RealmList<RealmIngredient> getIngredients() {
        return realmIngredients;
    }

    public void setIngredients(RealmList<RealmIngredient> realmIngredients) {
        this.realmIngredients = realmIngredients;
    }

    public RealmList<RealmStepRecipe> getSteps() {
        return steps;
    }

    public void setSteps(RealmList<RealmStepRecipe> steps) {
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
            for (RealmStepRecipe step : steps) {
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
