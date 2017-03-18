package com.exemple.android.cookbook.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.exemple.android.cookbook.activities.RecipeActivity;
import com.exemple.android.cookbook.models.firebase.FirebaseRecipe;
import com.exemple.android.cookbook.models.realm.RealmIngredient;
import com.exemple.android.cookbook.models.realm.RealmRecipe;

import java.io.ByteArrayOutputStream;

import io.realm.Realm;

/**
 * Created by Sakurov on 18.03.2017.
 */

public class RealmHelper {
    Realm mRealm = Realm.getDefaultInstance();
    Context mContext;
    RealmRecipe mRecipe = new RealmRecipe();;

    public RealmHelper(){

    }

    public RealmHelper(Context context, FirebaseRecipe recipe){
        mContext = context;
        mRecipe.setRecipe(recipe);
    }

    public void saveRecipeInRealm() {
        Glide.with(mContext).load(mRecipe.getPhotoUrl()).asBitmap().into(new SimpleTarget<Bitmap>(660, 480) {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                resource.compress(Bitmap.CompressFormat.PNG, 100, stream);
                final byte[] photoByteArray = stream.toByteArray();
                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmRecipe newRealmRecipe = realm.createObject(RealmRecipe.class);
                        newRealmRecipe.setRecipe(mRecipe);
                        newRealmRecipe.setPhoto(photoByteArray);
                    }

                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(mContext, "Success!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void updateRecipeInRealm() {
        RealmRecipe newRecipe = mRecipe;
        final RealmRecipe oldRecipe = mRealm.where(RealmRecipe.class).equalTo("recipeName", newRecipe.getRecipeName()).findAll().get(0);
        if (!oldRecipe.getPhotoUrl().equals(newRecipe.getPhotoUrl())) {
            oldRecipe.setPhotoUrl(newRecipe.getPhotoUrl());
            mRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    oldRecipe.savePhoto(mContext, mRealm);
                }
            });
        }
        if (!oldRecipe.getDescription().equals(newRecipe.getDescription())) {
            oldRecipe.setDescription(newRecipe.getDescription());
        }
        if (!oldRecipe.getIngredients().equals(newRecipe.getIngredients())) {
            oldRecipe.setIngredients(newRecipe.getIngredients());
        }
        if (!oldRecipe.getSteps().equals(newRecipe.getSteps())) {
            oldRecipe.setSteps(newRecipe.getSteps());
        }
    }
}
