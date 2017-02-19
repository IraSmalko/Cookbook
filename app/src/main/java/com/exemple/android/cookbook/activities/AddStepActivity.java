package com.exemple.android.cookbook.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.camera.CropImageIntentBuilder;
import com.exemple.android.cookbook.helpers.CheckOnlineHelper;
import com.exemple.android.cookbook.helpers.FirebaseHelper;
import com.exemple.android.cookbook.helpers.PhotoFromCameraHelper;
import com.exemple.android.cookbook.ProcessPhotoAsyncTask;
import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.ImageCard;
import com.exemple.android.cookbook.entity.StepRecipe;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddStepActivity extends AppCompatActivity {

    private static final int REQUEST_CROP_PICTURE = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 22;
    private static final int GALLERY_REQUEST = 13;

    private EditText inputNameRecipe;
    private ImageView imageView;
    private ProgressDialog progressDialog;
    private ActionBar actionBar;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri downloadUrlCamera;
    private int numberStep = 1;
    private Intent intent;
    private Uri pictureCropImageUri;
    private PhotoFromCameraHelper photoFromCameraHelper;
    private FirebaseHelper firebaseHelper;
    private Context context = AddStepActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_step_activity);

        imageView = (ImageView) findViewById(R.id.photoImageView);
        inputNameRecipe = (EditText) findViewById(R.id.name);
        ImageButton btnPhotoFromGallery = (ImageButton) findViewById(R.id.categoryRecipesPhotoUrlGallery);
        ImageButton btnPhotoFromCamera = (ImageButton) findViewById(R.id.categoryRecipesPhotoUrlCamera);
        Button btnSave = (Button) findViewById(R.id.btnSave);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.progress_dialog_title));

        photoFromCameraHelper = new PhotoFromCameraHelper(context, new PhotoFromCameraHelper.OnPhotoPicked() {
            @Override
            public void onPicked(Uri photoUri) {
                pictureCropImageUri = photoFromCameraHelper.createFileUriCrop();
                CropImageIntentBuilder cropImage = new CropImageIntentBuilder(660, 480, pictureCropImageUri);
                cropImage.setOutlineColor(0xFF03A9F4);
                cropImage.setSourceImage(photoUri);
                startActivityForResult(cropImage.getIntent(context), REQUEST_CROP_PICTURE);
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
        databaseReference = firebaseDatabase.getReference().child("Step_recipe/" + intent
                .getStringExtra("recipeList") + "/" + intent.getStringExtra("recipe"));
        storageReference = firebaseStorage.getReference().child("Step_Recipes" + "/" + intent
                .getStringExtra("recipeList") + "/" + intent.getStringExtra("recipe"));

        actionBar = getSupportActionBar();

        if (savedInstanceState != null && savedInstanceState.containsKey("numberStep")) {
            numberStep = savedInstanceState.getInt("numberStep");
        }
        actionBar.setTitle(getResources().getString(R.string.step) + numberStep);

        boolean isOnline = new CheckOnlineHelper(context).isOnline();
        if (isOnline) {
            btnSave.setOnClickListener(onClickListener);
            btnPhotoFromCamera.setOnClickListener(onClickListener);
            btnPhotoFromGallery.setOnClickListener(onClickListener);
        } else {
            Toast.makeText(context, getResources()
                    .getString(R.string.not_online), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context, getResources()
                                .getString(R.string.no_description_step), Toast.LENGTH_SHORT).show();
                    } else {
                        if (downloadUrlCamera != null) {
                            StepRecipe stepRecipe = new StepRecipe(getResources()
                                    .getString(R.string.step) + numberStep, inputNameRecipe
                                    .getText().toString(), downloadUrlCamera.toString());
                            String recipeId = databaseReference.push().getKey();
                            databaseReference.child(recipeId).setValue(stepRecipe);

                            Toast.makeText(context, getResources()
                                    .getString(R.string.data_save), Toast.LENGTH_SHORT).show();
                            numberStep = ++numberStep;
                            actionBar.setTitle(getResources().getString(R.string.step) + numberStep);
                            imageView.setImageResource(R.drawable.dishes);
                            inputNameRecipe.setText("");
                            downloadUrlCamera = null;
                        } else {
                            Toast.makeText(context, getResources()
                                    .getString(R.string.no_photo), Toast.LENGTH_LONG).show();
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
                final ProcessPhotoAsyncTask photoAsyncTask = new ProcessPhotoAsyncTask(context, listener);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int noSaveStepNumber = 1;
        if (numberStep > noSaveStepNumber) {
            outState.putInt("numberStep", numberStep);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(this, AddRecipeActivity.class);
        intent1.putExtra("recipeList", intent.getStringExtra("recipeList"));
        startActivity(intent1);
    }
}
