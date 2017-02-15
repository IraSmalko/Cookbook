package com.exemple.android.cookbook.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.StepRecipe;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Random;

public class AddStepActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_RESULT = 0;
    private static final int PIC_CROP = 2;

    private EditText inputNameRecipe;
    private ImageView imageView;
    private ProgressDialog progressDialog;
    private ActionBar actionBar;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri downloadUrlCamera;
    private int numberStep = 1;
    private Intent intent;
    private String pictureImagePath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/n" + ".jpg";
    private String pictureCropImagePath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/4" + ".jpg";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_step_activity);

        imageView = (ImageView) findViewById(R.id.photoImageView);
        inputNameRecipe = (EditText) findViewById(R.id.name);
        ImageButton btnPhotoFromGallery = (ImageButton) findViewById(R.id.categoryRecipesPhotoUrlGallery);
        ImageButton btnPhotoFromCamera = (ImageButton) findViewById(R.id.categoryRecipesPhotoUrlCamera);
        Button btnSave = (Button) findViewById(R.id.btnSave);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        intent = getIntent();
        databaseReference = firebaseDatabase.getReference()
                .child("Step_recipe/" + intent.getStringExtra("recipeList") + "/" + intent.getStringExtra("recipe"));

        storageReference = firebaseStorage.getReference().child("Step_Recipes" + "/"
                + intent.getStringExtra("recipeList") + "/" + intent.getStringExtra("recipe"));
        firebaseDatabase.getReference("app_title").setValue("Cookbook");


        actionBar = getSupportActionBar();

        if (savedInstanceState != null && savedInstanceState.containsKey("numberStep")) {
            numberStep = savedInstanceState.getInt("numberStep");
        }
        actionBar.setTitle(getResources().getString(R.string.step) + numberStep);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.progress_dialog_title));

        btnPhotoFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });

        btnPhotoFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoFromCamera();
            }
        });

        if (isOnline()) {
            btnSave.setOnClickListener(oclBtnSave);
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_online), Toast.LENGTH_SHORT).show();
        }
    }

    public void photoFromCamera() {
        File file = new File(pictureImagePath);
        Uri outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, CAMERA_RESULT);
    }

    private void performCrop(Uri picUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(this.getPackageManager()) != null) {
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
        }
    }

    View.OnClickListener oclBtnSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (inputNameRecipe.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_description_step), Toast.LENGTH_SHORT).show();
            } else {
                if (downloadUrlCamera != null) {
                    StepRecipe stepRecipe = new StepRecipe(getResources().getString(R.string.step) + numberStep, inputNameRecipe.getText().toString(), downloadUrlCamera.toString());
                    String recipeId = databaseReference.push().getKey();
                    databaseReference.child(recipeId).setValue(stepRecipe);

                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.data_save), Toast.LENGTH_SHORT).show();
                    numberStep = ++numberStep;
                    actionBar.setTitle(getResources().getString(R.string.step) + numberStep);
                    imageView.setImageResource(R.drawable.dishes);
                    inputNameRecipe.setText("");
                    downloadUrlCamera = null;
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_photo), Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    performCrop(selectedImage);
                }
            case CAMERA_RESULT:

                if (requestCode == CAMERA_RESULT) {
                    performCrop(Uri.fromFile(new File(pictureImagePath)));
                }
            case PIC_CROP:
                if (requestCode == PIC_CROP) {
                    if (imageReturnedIntent != null) {

                        File imgFile = new File(pictureCropImagePath);
                        Bitmap selectedBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();
                        final Random random = new Random();
                        progressDialog.show();

                        UploadTask uploadTask = storageReference.child("Photo_Step" + String.valueOf(random.nextInt())).putBytes(data);
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
                        imageView.setImageBitmap(selectedBitmap);
                    }
                }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (numberStep > 1) {
            outState.putInt("numberStep", numberStep);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(this, AddRecipeActivity.class);
        intent1.putExtra("recipeList", intent.getStringExtra("recipeList"));
        startActivity(intent1);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
