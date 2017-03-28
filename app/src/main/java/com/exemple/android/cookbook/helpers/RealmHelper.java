package com.exemple.android.cookbook.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.exemple.android.cookbook.entity.firebase.FirebaseRecipe;
import com.exemple.android.cookbook.entity.realm.RealmRecipe;
import com.exemple.android.cookbook.entity.realm.RealmStepRecipe;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;

import io.realm.Realm;

/**
 * Created by Sakurov on 18.03.2017.
 */

public class RealmHelper {

    public static final int BASKET = 100;
    public static final int SELECTED = 200;

    Realm mRealm = Realm.getDefaultInstance();
    Context mContext;
    FirebaseRecipe mFirebaseRecipe = new FirebaseRecipe();

    public RealmHelper() {

    }

    public RealmHelper(Context context, FirebaseRecipe firebaseRecipe) {
        mContext = context;
        mFirebaseRecipe = firebaseRecipe;
    }

    public void saveRecipeInRealm(final int saveTarget) {

        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRecipe newRealmRecipe = realm.createObject(RealmRecipe.class);
                newRealmRecipe.setRealmRecipe(mFirebaseRecipe);
                newRealmRecipe.setPhotoByteArray(loadPhoto(newRealmRecipe.getRecipePhotoUrl()));
                for (RealmStepRecipe realmStepRecipe : newRealmRecipe.getRecipeSteps()) {
                    realmStepRecipe.setPhotoByteArray(loadPhoto(realmStepRecipe.getStepPhotoUrl()));
                }
                if (saveTarget == SELECTED) {
                    newRealmRecipe.setInSaved(true);
                } else if (saveTarget == BASKET) {
                    newRealmRecipe.setInBasket(true);
                }
            }

        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (saveTarget == SELECTED) {
                    Toast.makeText(mContext, "Збережено", Toast.LENGTH_SHORT).show();
                } else if (saveTarget == BASKET) {
                    Toast.makeText(mContext, "Додано в кошик", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void updateRecipeInRealm(int updateTarget) {
        RealmRecipe newRecipe = new RealmRecipe();
        newRecipe.setRealmRecipe(mFirebaseRecipe);
        int countOfChanges = 0;
        final RealmRecipe oldRecipe = mRealm.where(RealmRecipe.class).equalTo("recipeName", newRecipe.getRecipeName()).findAll().get(0);
        mRealm.beginTransaction();
        if (!oldRecipe.getRecipePhotoUrl().equals(newRecipe.getRecipePhotoUrl())) {
            oldRecipe.setRecipePhotoUrl(newRecipe.getRecipePhotoUrl());
            mRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    oldRecipe.setPhotoByteArray(loadPhoto(oldRecipe.getRecipePhotoUrl()));
                }
            });
            countOfChanges++;
        }
        if (newRecipe.getRecipeDescription() != null) {
            if (oldRecipe.getRecipeDescription() != null) {
                if (!oldRecipe.getRecipeDescription().equals(newRecipe.getRecipeDescription())) {
                    oldRecipe.setRecipeDescription(newRecipe.getRecipeDescription());
                    countOfChanges++;
                }
            } else {
                oldRecipe.setRecipeDescription(newRecipe.getRecipeDescription());
                countOfChanges++;
            }
        }
        if (!oldRecipe.getIngredients().containsAll(newRecipe.getIngredients()) &&
                newRecipe.getIngredients().containsAll(oldRecipe.getIngredients())) {
            oldRecipe.setIngredients(newRecipe.getIngredients());
            countOfChanges++;
        }
        if (!oldRecipe.getRecipeSteps().containsAll(newRecipe.getRecipeSteps()) &&
                newRecipe.getRecipeSteps().containsAll(oldRecipe.getRecipeSteps())) {
            oldRecipe.setRecipeSteps(newRecipe.getRecipeSteps());
            mRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (RealmStepRecipe realmStepRecipe : oldRecipe.getRecipeSteps()) {
                        realmStepRecipe.setPhotoByteArray(loadPhoto(realmStepRecipe.getStepPhotoUrl()));
                    }
                }
            });
            countOfChanges++;
        }
        if (updateTarget == SELECTED) {
            if (oldRecipe.isInSaved()) {
                if (countOfChanges != 0) {
                    Toast.makeText(mContext, "Оновлено", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "У вас актуальна версія", Toast.LENGTH_SHORT).show();
                }
                oldRecipe.setInSaved(true);
            } else {
                Toast.makeText(mContext, "Збережено", Toast.LENGTH_SHORT).show();
                oldRecipe.setInSaved(true);
            }

        } else if (updateTarget == BASKET) {
            if (oldRecipe.isInBasket()) {
                Toast.makeText(mContext, "Вже в кошику", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Додано в кошик", Toast.LENGTH_SHORT).show();
                oldRecipe.setInBasket(true);
            }
        }
        mRealm.commitTransaction();

    }

    public byte[] loadPhoto(String photoUrl) {
        Bitmap bitmap = null;
        try {
            bitmap = Glide
                    .with(mContext)
                    .load(photoUrl)
                    .asBitmap()
                    .into(660, 480)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

}
