package com.exemple.android.cookbook.entity.realm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.exemple.android.cookbook.entity.firebase.FirebaseStepRecipe;

import java.io.ByteArrayOutputStream;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by Sakurov on 15.03.2017.
 */

public class RealmStepRecipe extends RealmObject {

    private String stepNumber;
    private String stepText;
    private String stepPhotoUrl;

    private byte[] stepPhoto;

    public RealmStepRecipe() {
    }

    public RealmStepRecipe(FirebaseStepRecipe firebaseStepRecipe) {
        this.stepNumber = firebaseStepRecipe.getStepNumber();
        this.stepText = firebaseStepRecipe.getStepText();
        this.stepPhotoUrl = firebaseStepRecipe.getStepPhotoUrl();
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

    public void setPhotoByteArray(byte[] photoByteArray){
        this.stepPhoto = photoByteArray;
    }

    public void saveStepPhoto(final Context context, final Realm realm) {
        if (context != null && stepPhotoUrl != null) {
            Glide.with(context).load(stepPhotoUrl).asBitmap().into(new SimpleTarget<Bitmap>(660, 480) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    resource.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            stepPhoto = stream.toByteArray();
                        }
                    });
                }
            });
        }
    }

    public Bitmap getStepPhoto() {
        Bitmap bitmap = null;
        if (stepPhoto != null) {
            bitmap = BitmapFactory.decodeByteArray(stepPhoto, 0, stepPhoto.length);
        }
        return bitmap;
    }
}
