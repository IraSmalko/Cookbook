package com.exemple.android.cookbook.entity.realm;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.exemple.android.cookbook.entity.firebase.FirebaseIngredient;
import com.exemple.android.cookbook.entity.firebase.FirebaseRecipe;
import com.exemple.android.cookbook.entity.firebase.FirebaseStepRecipe;

import java.io.ByteArrayOutputStream;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Sakurov on 15.03.2017.
 */

public class RealmRecipe extends RealmObject {

    private String recipeName;
    private String recipeDescription;
    private String recipePhotoUrl;

    private RealmList<RealmIngredient> recipeIngredients = new RealmList<>();
    private RealmList<RealmStepRecipe> recipeSteps = new RealmList<>();

    private byte[] photo;

    private boolean isInSaved = false;
    private boolean isInBasket = false;


    public void RealmRecipe() {
    }

    public void setRealmRecipe(FirebaseRecipe firebaseRecipe) {

        this.recipeName = firebaseRecipe.getName();
        this.recipeDescription = firebaseRecipe.getDescription();
        this.recipePhotoUrl = firebaseRecipe.getPhotoUrl();

        for (FirebaseIngredient firebaseIngredient : firebaseRecipe.getIngredients().values()) {
            this.recipeIngredients.add(new RealmIngredient(firebaseIngredient));
        }

        for (FirebaseStepRecipe firebaseStepRecipe : firebaseRecipe.getSteps().values()) {
            recipeSteps.add(new RealmStepRecipe(firebaseStepRecipe));
        }
    }


    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeDescription() {
        return recipeDescription;
    }

    public void setRecipeDescription(String recipeDescription) {
        this.recipeDescription = recipeDescription;
    }

    public String getRecipePhotoUrl() {
        return recipePhotoUrl;
    }

    public void setRecipePhotoUrl(String recipePhotoUrl) {
        this.recipePhotoUrl = recipePhotoUrl;
    }

    public RealmList<RealmIngredient> getIngredients() {
        return recipeIngredients;
    }

    public void setIngredients(RealmList<RealmIngredient> realmIngredients) {
        this.recipeIngredients.deleteAllFromRealm();
        for (RealmIngredient realmIngredient : realmIngredients) {
            this.recipeIngredients.add(realmIngredient);
        }
    }

    public RealmList<RealmStepRecipe> getRecipeSteps() {
        return recipeSteps;
    }

    public void setRecipeSteps(RealmList<RealmStepRecipe> recipeSteps) {
        this.recipeSteps.deleteAllFromRealm();
        for (RealmStepRecipe realmStepRecipe : recipeSteps) {
            this.recipeSteps.add(realmStepRecipe);
        }
    }

    public byte[] getPhotoByteArray() {
        return photo;
    }

    public void setPhotoByteArray(byte[] photo) {
        this.photo = photo;
    }

    public void savePhoto(final Context context, final Realm realm) {
        if (context != null && recipePhotoUrl != null) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Glide.with(context).load(recipePhotoUrl).asBitmap().into(new SimpleTarget<Bitmap>(660, 480) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            resource.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            photo = stream.toByteArray();
                        }
                    });
                }
            });
        }
    }

    public void saveStepsPhoto(final Context context, Realm realm) {
        if (context != null && recipeSteps != null) {
            for (RealmStepRecipe step : recipeSteps) {
                step.saveStepPhoto(context, realm);
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


    public boolean isInSaved() {
        return isInSaved;
    }

    public void setInSaved(boolean inSaved) {
        isInSaved = inSaved;
    }

    public boolean isInBasket() {
        return isInBasket;
    }

    public void setInBasket(boolean inBasket) {
        isInBasket = inBasket;
    }

}
