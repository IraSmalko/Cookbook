package com.exemple.android.cookbook.activities;


import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.exemple.android.cookbook.PhotoFromCameraHelper;
import com.exemple.android.cookbook.ProcessPhotoAsyncTask;
import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.CategoryRecipes;
import com.exemple.android.cookbook.entity.ImageCard;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class AddCategoryRecipeActivity extends AppCompatActivity {

    private static final int PIC_CROP = 2;

    private EditText inputCategoryName;
    private ImageView imageView;
    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri downloadUrlCamera;
    private int backPressed = 0;
    private String pictureCropImagePath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/4" + ".jpg";
    PhotoFromCameraHelper photoFromCameraHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_category_recipe);

        imageView = (ImageView) findViewById(R.id.photoImageView);
        inputCategoryName = (EditText) findViewById(R.id.name);
        ImageButton btnPhotFromGallery = (ImageButton) findViewById(R.id.categoryRecipesPhotoUrlGallery);
        ImageButton btnPhotoFromCamera = (ImageButton) findViewById(R.id.categoryRecipesPhotoUrl);
        Button btnSave = (Button) findViewById(R.id.btnSave);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.progress_dialog_title));

        photoFromCameraHelper = new PhotoFromCameraHelper(AddCategoryRecipeActivity.this, new PhotoFromCameraHelper.OnPhotoPicked() {
            @Override
            public void onPicked(Uri photoUri) {
                final ProcessPhotoAsyncTask photoAsyncTask = new ProcessPhotoAsyncTask(AddCategoryRecipeActivity.this, listener);
                photoAsyncTask.execute(photoUri);
            }
        });

        btnPhotFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoFromCameraHelper.pickPhoto();
            }
        });

        btnPhotoFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    photoFromCameraHelper.takePhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        databaseReference = firebaseDatabase.getReference("Сategory_Recipes");
        storageReference = firebaseStorage.getReference().child("Photo_Сategory_Recipes");
        firebaseDatabase.getReference("app_title").setValue("Cookbook");

        if (isOnline()) {
            btnSave.setOnClickListener(oclBtnSave);
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_online), Toast.LENGTH_SHORT).show();
        }
    }

    private void performCrop(Uri picUri) {
        try {
            File file = new File(pictureCropImagePath);
            Uri outputFileUri = Uri.fromFile(file);
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", true);
            cropIntent.putExtra("aspectX", 33);
            cropIntent.putExtra("aspectY", 24);
            cropIntent.putExtra("outputX", 660);
            cropIntent.putExtra("outputY", 480);
            cropIntent.putExtra("return-data", true);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(cropIntent, PIC_CROP);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    View.OnClickListener oclBtnSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (inputCategoryName.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_category_name), Toast.LENGTH_SHORT).show();
            } else {
                if (downloadUrlCamera != null) {
                    CategoryRecipes categoryRecipes = new CategoryRecipes(inputCategoryName.getText().toString(), downloadUrlCamera.toString());
                    String recipeId = databaseReference.push().getKey();
                    databaseReference.child(recipeId).setValue(categoryRecipes);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.data_save), Toast.LENGTH_SHORT).show();
                    imageView.setImageResource(R.drawable.dishes);
                    inputCategoryName.setText("");
                    downloadUrlCamera = null;
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_input), Toast.LENGTH_LONG).show();
                }
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        photoFromCameraHelper.onActivityResult(resultCode, requestCode, imageReturnedIntent);
    }

    private final ProcessPhotoAsyncTask.OnPhotoProcessed listener = new ProcessPhotoAsyncTask.OnPhotoProcessed() {
        @Override
        public void onDataReady(@Nullable ImageCard imageCard) {
            final Random random = new Random();
            progressDialog.show();
            UploadTask uploadTask = storageReference.child("Photo_Сategory_Recipes"
                    + String.valueOf(random.nextInt())).putBytes(imageCard.getBytesImage());
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadUrlCamera = taskSnapshot.getDownloadUrl();
                    progressDialog.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.setMessage(getResources().getString(R.string.progress_vait));
                }
            });
            imageView.setImageBitmap(imageCard.getImage());
        }
    };

    @Override
    public void onBackPressed() {
        if (!inputCategoryName.getText().toString().equals("") || downloadUrlCamera != null) {
            if (backPressed == 1) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (backPressed == 0) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.input_will_lost), Toast.LENGTH_SHORT).show();
                backPressed = 1;
            }
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
