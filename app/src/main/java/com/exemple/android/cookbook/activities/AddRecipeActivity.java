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
import com.exemple.android.cookbook.entity.ImageCard;
import com.exemple.android.cookbook.entity.Recipe;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AddRecipeActivity extends AppCompatActivity {

    private static final int REQUEST_CROP_PICTURE = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 22;
    private static final int GALLERY_REQUEST = 13;

    private EditText inputNameRecipe, inputIngredients;
    private ImageView imageView;
    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri downloadUrlCamera;
    private ArrayList<String> nameRecipesList = new ArrayList<>();
    private int backPressed = 0;
    private Intent intent;
    private Uri pictureCropImageUri;
    private PhotoFromCameraHelper photoFromCameraHelper;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_recipe);

        imageView = (ImageView) findViewById(R.id.photoImageView);
        inputNameRecipe = (EditText) findViewById(R.id.name);
        inputIngredients = (EditText) findViewById(R.id.addIngredients);
        ImageButton btnPhotoFromGallery = (ImageButton) findViewById(R.id.categoryRecipesPhotoUrlGallery);
        ImageButton btnPhotoFromCamera = (ImageButton) findViewById(R.id.categoryRecipesPhotoUrlCamera);
        Button btnSave = (Button) findViewById(R.id.btnSave);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.progress_dialog_title));

        photoFromCameraHelper = new PhotoFromCameraHelper(AddRecipeActivity.this, new PhotoFromCameraHelper.OnPhotoPicked() {
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

        intent = getIntent();
        nameRecipesList = intent.getStringArrayListExtra("ArrayListRecipe");
        databaseReference = firebaseDatabase.getReference().child("Recipe_lists/" + intent.getStringExtra("recipeList"));
        storageReference = firebaseStorage.getReference().child("Recipe" + "/" + intent.getStringExtra("recipeList"));

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
                    if (inputNameRecipe.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_recipe_name),
                                Toast.LENGTH_SHORT).show();
                    } else if (inputIngredients.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_ingredients),
                                Toast.LENGTH_SHORT).show();
                    } else if (!nameRecipesList.contains(inputNameRecipe.getText().toString())) {
                        if (downloadUrlCamera != null) {
                            Recipe recipes = new Recipe(inputNameRecipe.getText().toString(),
                                    downloadUrlCamera.toString(), inputIngredients.getText().toString());
                            String recipeId = databaseReference.push().getKey();
                            databaseReference.child(recipeId).setValue(recipes);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.data_save),
                                    Toast.LENGTH_SHORT).show();
                            imageView.setImageResource(R.drawable.dishes);
                            Intent intentAddStepActivity = new Intent(getApplicationContext(), AddStepActivity.class);
                            intentAddStepActivity.putExtra("recipeList", intent.getStringExtra("recipeList"));
                            intentAddStepActivity.putExtra("recipe", inputNameRecipe.getText().toString());
                            inputNameRecipe.setText("");
                            inputIngredients.setText("");
                            startActivity(intentAddStepActivity);
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_photo),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.name_recipe_exists),
                                Toast.LENGTH_SHORT).show();
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
                final ProcessPhotoAsyncTask photoAsyncTask = new ProcessPhotoAsyncTask(AddRecipeActivity.this, listener);
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
        Intent intent1 = new Intent(this, RecipeListActivity.class);
        intent1.putExtra("recipeList", intent.getStringExtra("recipeList"));
        if (!inputNameRecipe.getText().toString().equals("") || downloadUrlCamera != null
                || !inputIngredients.getText().toString().equals("")) {
            int backPressedTrue = 1;
            int backPressedTFalse = 0;
            if (backPressed == backPressedTrue) {
                startActivity(intent1);
            } else if (backPressed == backPressedTFalse) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.input_will_lost), Toast.LENGTH_SHORT).show();
                backPressed = backPressedTrue;
            } else {
                startActivity(intent1);
            }
        } else {
            startActivity(intent1);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}

