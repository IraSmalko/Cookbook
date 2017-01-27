package com.exemple.android.cookbook;


import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import android.widget.Toast;

import com.exemple.android.cookbook.supporting.CategoryRecipes;
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

public class AddCategoryRecipeActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_RESULT = 0;
    private static final int PIC_CROP = 2;

    private EditText inputCategoryName;
    private ImageView imageView;
    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri downloadUrlCamera;
    private String pictureImagePath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/n" + ".jpg";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_category_recipe);

        imageView = (ImageView) findViewById(R.id.photo_imageView);
        inputCategoryName = (EditText) findViewById(R.id.name);
        ImageButton btnPhoto_fromGallery = (ImageButton) findViewById(R.id.categoryRecipesPhotoUrlGallery);
        ImageButton btnPhoto_fromCamera = (ImageButton) findViewById(R.id.categoryRecipesPhotoUrl);
        Button btnSave = (Button) findViewById(R.id.btn_save);

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

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        databaseReference = firebaseDatabase.getReference("Сategory_Recipes");
        storageReference = firebaseStorage.getReference().child("Photo_Сategory_Recipes");
        firebaseDatabase.getReference("app_title").setValue("Cookbook");

        btnSave.setOnClickListener(oclBtnSave);
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
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/4" + ".jpg");
            Uri outputFileUri = Uri.fromFile(file);
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", true);
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 670);
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
                Toast.makeText(getApplicationContext(), "Додайте ім'я категорії!", Toast.LENGTH_SHORT).show();
            }else {
                if (downloadUrlCamera != null) {
                    CategoryRecipes categoryRecipes = new CategoryRecipes(inputCategoryName.getText().toString(), downloadUrlCamera.toString());
                    String recipeId = databaseReference.push().getKey();
                    databaseReference.child(recipeId).setValue(categoryRecipes);
                    Toast.makeText(getApplicationContext(), "Дані збережено.", Toast.LENGTH_SHORT).show();
                    imageView.setImageResource(R.drawable.dishes);
                    inputCategoryName.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "Щось ви робите неправильно, \n зробіть нормально!", Toast.LENGTH_LONG).show();
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

                        File imgFile = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/4" + ".jpg");
                        Bitmap selectedBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();
                        final Random random = new Random();
                        progressDialog.show();

                        UploadTask uploadTask = storageReference.child("Photo_Сategory_Recipes" + String.valueOf(random.nextInt())).putBytes(data);
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
                                progressDialog.setMessage("Зачекайте, будь ласка" );
                            }
                        });
                        imageView.setImageBitmap(selectedBitmap);
                    }
                }
        }
    }
}
