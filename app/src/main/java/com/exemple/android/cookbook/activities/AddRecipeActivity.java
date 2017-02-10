package com.exemple.android.cookbook.activities;


import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.Recipe;
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
import java.util.ArrayList;
import java.util.Random;

public class AddRecipeActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_RESULT = 0;
    private static final int PIC_CROP = 2;

    private EditText inputNameRecipe, inputIngredients;
    private ImageView imageView;
    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri downloadUrlCamera;
    private String pictureImagePath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/n" + ".jpg";
    private ArrayList<String> nameRecipesList = new ArrayList<>();
    private int backPressed = 0;
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_recipe);

        imageView = (ImageView) findViewById(R.id.photo_imageView);
        inputNameRecipe = (EditText) findViewById(R.id.name);
        inputIngredients = (EditText) findViewById(R.id.add_ingredients);
        ImageButton btnPhoto_fromGallery = (ImageButton) findViewById(R.id.categoryRecipesPhotoUrlGallery);
        ImageButton btnPhoto_fromCamera = (ImageButton) findViewById(R.id.categoryRecipesPhotoUrl);
        Button btnSave = (Button) findViewById(R.id.btn_save);


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        intent = getIntent();
        nameRecipesList = intent.getStringArrayListExtra("ArrayListRecipe");
        databaseReference = firebaseDatabase.getReference().child("Recipe_lists/" + intent.getStringExtra("recipeList"));

        storageReference = firebaseStorage.getReference().child("Photo_Recipes");
        firebaseDatabase.getReference("app_title").setValue("Cookbook");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Завантаження");

        btnPhoto_fromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });

        btnPhoto_fromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoFromCamera();
            }
        });

        if (isOnline()) {
            btnSave.setOnClickListener(oclBtnSave);
        } else {
            Toast.makeText(getApplicationContext(), "Відсутній доступ до інтернету!", Toast.LENGTH_SHORT).show();
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
        try {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/4" + ".jpg");
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
            if (inputNameRecipe.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "Додайте ім'я рецепта!", Toast.LENGTH_SHORT).show();
            } else if (inputIngredients.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "Додайте інгридієнти!", Toast.LENGTH_SHORT).show();
            } else if (!nameRecipesList.contains(inputNameRecipe.getText().toString())) {
                if (downloadUrlCamera != null) {
                    Recipe recipes = new Recipe(inputNameRecipe.getText().toString(), downloadUrlCamera.toString(), inputIngredients.getText().toString());
                    String recipeId = databaseReference.push().getKey();
                    databaseReference.child(recipeId).setValue(recipes);
                    Toast.makeText(getApplicationContext(), "Дані збережено.", Toast.LENGTH_SHORT).show();
                    imageView.setImageResource(R.drawable.dishes);
                    Intent intent = new Intent(getApplicationContext(), AddStepActivity.class);
                    intent.putExtra("recipeList", inputNameRecipe.getText().toString());
                    inputNameRecipe.setText("");
                    inputIngredients.setText("");
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Додайте фото!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Iм'я рецепта вже існує!", Toast.LENGTH_SHORT).show();
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

                        File imgFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/4" + ".jpg");
                        Bitmap selectedBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();
                        final Random random = new Random();
                        progressDialog.show();

                        UploadTask uploadTask = storageReference.child("Photo_Recipes" + String.valueOf(random.nextInt())).putBytes(data);
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
                                progressDialog.setMessage("Зачекайте, будь ласка");
                            }
                        });
                        imageView.setImageBitmap(selectedBitmap);
                    }
                }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(this, RecipeListActivity.class);
        intent1.putExtra("recipeList", intent.getStringExtra("recipeList"));
        if (!inputNameRecipe.getText().toString().equals("") || downloadUrlCamera != null
                || !inputIngredients.getText().toString().equals("")) {
            if (backPressed == 1) {
                startActivity(intent1);
            } else if (backPressed == 0) {
                Toast.makeText(getApplicationContext(), "Введені дані буде втрачено!", Toast.LENGTH_SHORT).show();
                backPressed = 1;
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

