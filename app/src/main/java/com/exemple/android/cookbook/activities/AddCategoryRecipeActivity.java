package com.exemple.android.cookbook.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.camera.CropImageIntentBuilder;
import com.exemple.android.cookbook.FirebaseHelper;
import com.exemple.android.cookbook.PhotoFromCameraHelper;
import com.exemple.android.cookbook.ProcessPhotoAsyncTask;
import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.CategoryRecipes;
import com.exemple.android.cookbook.entity.ImageCard;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddCategoryRecipeActivity extends AppCompatActivity {

    private static final int REQUEST_CROP_PICTURE = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 22;
    private static final int GALLERY_REQUEST = 13;
    private static final String REFERENCE = "Сategory_Recipes";
    private static final String STORAGE_REFERENCE = "Photo_Сategory_Recipes";

    private EditText inputCategoryName;
    private ImageView imageView;
    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri downloadUrlCamera;
    private int backPressed = 0;
    private Uri pictureCropImageUri;
    private PhotoFromCameraHelper photoFromCameraHelper;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_category_recipe);

        imageView = (ImageView) findViewById(R.id.photoImageView);
        inputCategoryName = (EditText) findViewById(R.id.name);
        ImageButton btnPhotoFromGallery = (ImageButton) findViewById(R.id.categoryRecipesPhotoUrlGallery);
        ImageButton btnPhotoFromCamera = (ImageButton) findViewById(R.id.categoryRecipesPhotoUrlCamera);
        Button btnSave = (Button) findViewById(R.id.btnSave);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.progress_dialog_title));

        photoFromCameraHelper = new PhotoFromCameraHelper(AddCategoryRecipeActivity.this, new PhotoFromCameraHelper.OnPhotoPicked() {
            @Override
            public void onPicked(Uri photoUri) {
                pictureCropImageUri = photoFromCameraHelper.createFileUriCrop();
                CropImageIntentBuilder cropImage = new CropImageIntentBuilder(660, 480, pictureCropImageUri);
                cropImage.setOutlineColor(0xFF03A9F4);
                cropImage.setSourceImage(photoUri);
                startActivityForResult(cropImage.getIntent(getApplicationContext()), REQUEST_CROP_PICTURE);
            }
        });

        firebaseHelper = new FirebaseHelper(new FirebaseHelper.OnSaveImage() {
            @Override
            public void OnSave(Uri photoUri) {
                downloadUrlCamera = photoUri;
                progressDialog.dismiss();
            }
        });

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        databaseReference = firebaseDatabase.getReference(REFERENCE);
        storageReference = firebaseStorage.getReference().child(STORAGE_REFERENCE);

        if (isOnline()) {
            btnSave.setOnClickListener(onClickListener);
            btnPhotoFromCamera.setOnClickListener(onClickListener);
            btnPhotoFromGallery.setOnClickListener(onClickListener);
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_online), Toast.LENGTH_SHORT).show();
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.categoryRecipesPhotoUrlGallery:
                    photoFromCameraHelper.pickPhoto();
                    break;
                case R.id.categoryRecipesPhotoUrlCamera:
                    photoFromCameraHelper.takePhoto();
                    break;
                case R.id.btnSave:
                    if (inputCategoryName.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_category_name), Toast.LENGTH_SHORT).show();
                    } else {
                        if (downloadUrlCamera != null) {
                            CategoryRecipes categoryRecipes = new CategoryRecipes(inputCategoryName.getText().toString(),
                                    downloadUrlCamera.toString());
                            String recipeId = databaseReference.push().getKey();
                            databaseReference.child(recipeId).setValue(categoryRecipes);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.data_save), Toast.LENGTH_SHORT).show();
                            imageView.setImageResource(R.drawable.dishes);
                            inputCategoryName.setText("");
                            downloadUrlCamera = null;
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_input),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == REQUEST_IMAGE_CAPTURE || requestCode == GALLERY_REQUEST) {
            photoFromCameraHelper.onActivityResult(resultCode, requestCode, imageReturnedIntent);
        } else if (requestCode == REQUEST_CROP_PICTURE) {
            if (resultCode == RESULT_OK) {
                final ProcessPhotoAsyncTask photoAsyncTask = new ProcessPhotoAsyncTask(AddCategoryRecipeActivity.this, listener);
                photoAsyncTask.execute(pictureCropImageUri);
            }
        }
    }

    private final ProcessPhotoAsyncTask.OnPhotoProcessed listener = new ProcessPhotoAsyncTask.OnPhotoProcessed() {
        @Override
        public void onDataReady(@Nullable ImageCard imageCard) {
            if (imageCard != null) {
                imageView.setImageBitmap(imageCard.getImage());
            }
            progressDialog.setMessage(getResources().getString(R.string.progress_vait));
            progressDialog.show();
            firebaseHelper.saveImage(storageReference, imageCard);
        }
    };

    @Override
    public void onBackPressed() {
        if (!inputCategoryName.getText().toString().equals("") || downloadUrlCamera != null) {
            int backPressedTrue = 1;
            int backPressedTFalse = 0;
            if (backPressed == backPressedTrue) {
                super.onBackPressed();
            } else if (backPressed == backPressedTFalse) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.input_will_lost), Toast.LENGTH_SHORT).show();
                backPressed = backPressedTrue;
            }
        } else {
            super.onBackPressed();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
