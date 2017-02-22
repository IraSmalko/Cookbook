package com.exemple.android.cookbook.helpers;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.exemple.android.cookbook.entity.ImageCard;
import com.exemple.android.cookbook.entity.StepRecipe;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FirebaseHelper {

    private FirebaseHelper.OnSaveImage onSaveImage;
    private static List<StepRecipe> stepRecipe = new ArrayList<>();

    public FirebaseHelper(FirebaseHelper.OnSaveImage onSaveImage) {
        this.onSaveImage = onSaveImage;
    }

    public void saveImage(StorageReference storageReference, ImageCard imageCard) {
        final Random random = new Random();
        UploadTask uploadTask = storageReference.child("Photo_Сategory_Recipes"
                + String.valueOf(random.nextInt())).putBytes(imageCard.getBytesImage());
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrlCamera = taskSnapshot.getDownloadUrl();
                onSaveImage.OnSave(downloadUrlCamera);
            }
        });
    }

    public static void  getStepsRecipe (final Context context, final int idRecipe, String recipeList, String recipe){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference()
                .child("Step_recipe/" + recipeList + "/" + recipe);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    StepRecipe step = postSnapshot.getValue(StepRecipe.class);
                    stepRecipe.add(step);
                }
                new DataSourceSQLite(context).saveStepsSQLite(stepRecipe, idRecipe);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public interface OnSaveImage {
        void OnSave(Uri photoUri);
    }
}
