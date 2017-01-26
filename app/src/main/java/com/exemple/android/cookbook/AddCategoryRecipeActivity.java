package com.exemple.android.cookbook;


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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.exemple.android.cookbook.supporting.CategoryRecipes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class AddCategoryRecipeActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    static final int GALLERY_REQUEST = 1;
    private final int CAMERA_RESULT = 0;

    private TextView txtDetails;
    private EditText inputCategoryName;
    private Button btnSave;
    private ImageButton btnPhoto_fromGallery, btnPhoto_fromCamera;
    private String recipeId;
    private Bitmap bitmap = null;
    private ImageView imageView;

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Uri downloadUrl;
    private CategoryRecipes categoryRecipes;
    private String pictureImagePath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/n" + ".jpg";

    public void openBackCamera() {
        File file = new File(pictureImagePath);
        Uri outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, CAMERA_RESULT);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_category_recipe);

        txtDetails = (TextView) findViewById(R.id.txt_category_recipe);
        inputCategoryName = (EditText) findViewById(R.id.name);
        btnPhoto_fromGallery = (ImageButton) findViewById(R.id.categoryRecipesPhotoUrlGallery);
        btnPhoto_fromCamera = (ImageButton) findViewById(R.id.categoryRecipesPhotoUrl);
        btnSave = (Button) findViewById(R.id.btn_save);

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
                openBackCamera();
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        databaseReference = firebaseDatabase.getReference("Сategory_Recipes");
        storageReference = firebaseStorage.getReference().child("Photo_Сategory_Recipes");
        firebaseDatabase.getReference("app_title").setValue("Cookbook");

        firebaseDatabase.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "App title updated");

                String appTitle = dataSnapshot.getValue(String.class);

                getSupportActionBar().setTitle(appTitle);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(recipeId)) {

                } else {
                    //updateCategoryRecipes(recipe, photoUrl);
                }
            }
        });

        toggleButton();
    }

    private void toggleButton() {
        if (TextUtils.isEmpty(recipeId)) {
            btnSave.setText("Save");
        } else {
            btnSave.setText("Update");
        }
    }


    private void addUserChangeListener() {
        databaseReference.child(recipeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CategoryRecipes сategoryRecipes = dataSnapshot.getValue(CategoryRecipes.class);

                if (сategoryRecipes == null) {
                    Log.e(TAG, "data is null!");
                    return;
                }

                Log.e(TAG, "data is changed!" + сategoryRecipes.getName() + ", " + сategoryRecipes.getPhotoUrl());
                txtDetails.setText(сategoryRecipes.getName() + ", " + сategoryRecipes.getPhotoUrl());
                inputCategoryName.setText("");
                toggleButton();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }

    public Bitmap getBitmapfromByteArray(byte[] bitmap) {
        return BitmapFactory.decodeByteArray(bitmap , 0, bitmap.length);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        imageView = (ImageView) findViewById(R.id.photo_imageView);
        final String recipe = inputCategoryName.getText().toString();
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    StorageReference photoGalleryRef = storageReference.child(selectedImage.getLastPathSegment());

                    photoGalleryRef.putFile(selectedImage).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            downloadUrl = taskSnapshot.getDownloadUrl();
                            categoryRecipes = new CategoryRecipes(inputCategoryName.getText().toString(), downloadUrl.toString());
                            recipeId = databaseReference.push().getKey();
                            databaseReference.child(recipeId).setValue(categoryRecipes);
                        }
                    });
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            case CAMERA_RESULT:

                if (requestCode == CAMERA_RESULT) {
                    File imgFile = new  File(pictureImagePath);
                        Bitmap cameraBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    cameraBitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
                    byte[] data = baos.toByteArray();
                    final Random random = new Random();

                    UploadTask uploadTask = storageReference.child("Photo_Сategory_Recipes" + String.valueOf(random.nextInt())).putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrlCamera = taskSnapshot.getDownloadUrl();
                            categoryRecipes = new CategoryRecipes(inputCategoryName.getText().toString(), downloadUrlCamera.toString());
                            recipeId = databaseReference.push().getKey();
                            databaseReference.child(recipeId).setValue(categoryRecipes);
                        }
                    });
                    imageView.setImageBitmap(cameraBitmap);
                }
        }
    }

}
