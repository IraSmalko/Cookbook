package com.exemple.android.cookbook.helpers;


import android.net.Uri;
import android.support.annotation.NonNull;

import com.exemple.android.cookbook.entity.ImageCard;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

public class FirebaseHelper {

    private FirebaseHelper.OnSaveImage onSaveImage;

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

    public interface OnSaveImage {
        void OnSave(Uri photoUri);
    }
}
