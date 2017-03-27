package com.exemple.android.cookbook.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.adapters.IngredientsAdapter;
import com.exemple.android.cookbook.entity.ImageCard;
import com.exemple.android.cookbook.entity.Ingredient;
import com.exemple.android.cookbook.entity.Recipe;
import com.exemple.android.cookbook.helpers.CheckOnlineHelper;
import com.exemple.android.cookbook.helpers.CropHelper;
import com.exemple.android.cookbook.helpers.FirebaseHelper;
import com.exemple.android.cookbook.helpers.IntentHelper;
import com.exemple.android.cookbook.helpers.PhotoFromCameraHelper;
import com.exemple.android.cookbook.helpers.ProcessPhotoAsyncTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AddRecipeActivity extends AppCompatActivity {

    private static final int REQUEST_CROP_PICTURE = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 22;
    private static final int GALLERY_REQUEST = 13;
    private static final String RECIPE_LIST = "recipeList";
    private static final String ARRAY_LIST_RECIPE = "ArrayListRecipe";

    private EditText mInputNameRecipe, mInputIngredients, mQuantity, mUnit;
    private ImageView mImageView;
    private ProgressDialog mProgressDialog;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference, mDatabaseReferenceIngredients;
    private StorageReference mStorageReference;
    private Uri mDownloadUrlCamera;
    private List<String> mNameRecipesList = new ArrayList<>();
    private List<Ingredient> mIngredients = new ArrayList<>();
    private int mBackPressed = 0;
    private Intent mIntent;
    private PhotoFromCameraHelper mPhotoFromCameraHelper;
    private FirebaseHelper mFirebaseHelper;
    private CropHelper mCropHelper;
    private Context mContext = AddRecipeActivity.this;
    private RecyclerView mRecyclerView;
    private IngredientsAdapter mIngredientsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_recipe);

        mImageView = (ImageView) findViewById(R.id.photoImageView);
        mInputNameRecipe = (EditText) findViewById(R.id.name);
        mInputIngredients = (EditText) findViewById(R.id.nameIngredients);
        mQuantity = (EditText) findViewById(R.id.quantity);
        mUnit = (EditText) findViewById(R.id.unit);
        ImageButton plusIngredient = (ImageButton) findViewById(R.id.plus);
        ImageButton btnPhotoFromGallery = (ImageButton) findViewById(R.id.categoryRecipesPhotoUrlGallery);
        ImageButton btnPhotoFromCamera = (ImageButton) findViewById(R.id.categoryRecipesPhotoUrlCamera);
        Button btnSave = (Button) findViewById(R.id.btnSave);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerIngredients);

        mIngredientsAdapter = new IngredientsAdapter(getApplicationContext(), mIngredients, new IngredientsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Ingredient item) {
                Toast.makeText(mContext, getResources().getString(R.string.no_ingredients), Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(mIngredientsAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getResources().getString(R.string.progress_dialog_title));

        mPhotoFromCameraHelper = new PhotoFromCameraHelper(mContext, new PhotoFromCameraHelper.OnPhotoPicked() {
            @Override
            public void onPicked(Uri photoUri) {
                mCropHelper.cropImage(photoUri);
            }
        });

        mFirebaseHelper = new FirebaseHelper(new FirebaseHelper.OnSaveImage() {
            @Override
            public void OnSave(Uri photoUri) {
                mDownloadUrlCamera = photoUri;
                mProgressDialog.dismiss();
            }
        });

        mCropHelper = new CropHelper(mContext, new CropHelper.OnCrop() {
            @Override
            public void onCrop(Uri cropImageUri) {
                final ProcessPhotoAsyncTask photoAsyncTask = new ProcessPhotoAsyncTask(mContext, listener);
                photoAsyncTask.execute(cropImageUri);
            }
        });

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            String username = firebaseUser.getDisplayName();

            mIntent = getIntent();
            mNameRecipesList = mIntent.getStringArrayListExtra(ARRAY_LIST_RECIPE);
            mDatabaseReference = mFirebaseDatabase.getReference().child(username + "/Recipe_lists/" + mIntent
                    .getStringExtra(RECIPE_LIST));
            mStorageReference = firebaseStorage.getReference().child(username + "/Recipe" + "/" + mIntent
                    .getStringExtra(RECIPE_LIST));

            boolean isOnline = new CheckOnlineHelper(mContext).isOnline();
            if (isOnline) {
                btnSave.setOnClickListener(onClickListener);
                btnPhotoFromCamera.setOnClickListener(onClickListener);
                btnPhotoFromGallery.setOnClickListener(onClickListener);
                plusIngredient.setOnClickListener(onClickListener);
            } else {
                Toast.makeText(mContext, getResources().getString(R.string.not_online), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.unauthorized_user), Toast
                    .LENGTH_SHORT).show();
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.categoryRecipesPhotoUrlGallery:
                    mPhotoFromCameraHelper.pickPhoto();
                    break;
                case R.id.categoryRecipesPhotoUrlCamera:
                    mPhotoFromCameraHelper.takePhoto();
                    break;
                case R.id.plus:
                    if (mInputIngredients.getText().toString().equals("") || mQuantity.getText()
                            .toString().equals("") || mUnit.getText().toString().equals("")) {
                        Toast.makeText(mContext, getResources().getString(R.string.fill_fields), Toast.LENGTH_SHORT).show();
                    } else {
                        mIngredients.add(new Ingredient(mInputIngredients.getText().toString(), Float
                                .valueOf(mQuantity.getText().toString()), mUnit.getText().toString()));
mIngredientsAdapter.updateAdapter(mIngredients);
                        mInputIngredients.setText("");
                        mQuantity.setText("");
                        mUnit.setText("");
                    }
                    break;
                case R.id.btnSave:
                    if (mInputNameRecipe.getText().toString().equals("")) {
                        Toast.makeText(mContext, getResources().getString(R.string.no_recipe_name), Toast.LENGTH_SHORT).show();
                    } else if (mIngredients == null && mInputNameRecipe.getText().toString().equals("")
                            || mQuantity.getText().toString().equals("") || mUnit.getText().toString().equals("")) {
                        Toast.makeText(mContext, getResources().getString(R.string.no_ingredients), Toast.LENGTH_SHORT).show();
                    } else if (!mNameRecipesList.contains(mInputNameRecipe.getText().toString())) {
                        if (mDownloadUrlCamera != null) {
                            Recipe recipes = new Recipe(mInputNameRecipe.getText().toString(),
                                    mDownloadUrlCamera.toString(), mInputIngredients.getText().toString(), 0);
                            mDatabaseReferenceIngredients = mFirebaseDatabase.getReference().child(new FirebaseHelper()
                                    .getUsername() + "/Ingredient/" + mIntent.getStringExtra(RECIPE_LIST) + "/" + mInputNameRecipe
                                    .getText().toString());
                            if (!mInputIngredients.getText().toString().equals("") && !mQuantity.getText()
                                    .toString().equals("") && !mUnit.getText().toString().equals("")) {
                                Ingredient ingredient = new Ingredient(mInputIngredients.getText().toString(), Float
                                        .valueOf(mQuantity.getText().toString()), mUnit.getText().toString());
                                String id = mDatabaseReferenceIngredients.push().getKey();
                                mDatabaseReferenceIngredients.child(id).setValue(ingredient);
                            }
                            if (mIngredients != null) {
                                for (Ingredient ingredient : mIngredients) {
                                    String id = mDatabaseReferenceIngredients.push().getKey();
                                    mDatabaseReferenceIngredients.child(id).setValue(ingredient);
                                }
                            }
                            String recipeId = mDatabaseReference.push().getKey();
                            mDatabaseReference.child(recipeId).setValue(recipes);
                            Toast.makeText(mContext, getResources().getString(R.string.data_save), Toast.LENGTH_SHORT).show();
                            mImageView.setImageResource(R.drawable.dishes);
                            IntentHelper.intentAddStepActivity(mContext, mIntent
                                    .getStringExtra(RECIPE_LIST), mInputNameRecipe.getText().toString());
                        } else {
                            Toast.makeText(mContext, getResources().getString(R.string.no_photo), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.name_recipe_exists), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == REQUEST_IMAGE_CAPTURE || requestCode == GALLERY_REQUEST) {
            mPhotoFromCameraHelper.onActivityResult(resultCode, requestCode, imageReturnedIntent);
        } else if (requestCode == REQUEST_CROP_PICTURE) {
            if (resultCode == RESULT_OK) {
                mCropHelper.onActivityResult(resultCode, requestCode);
            }
        }
    }

    private final ProcessPhotoAsyncTask.OnPhotoProcessed listener = new ProcessPhotoAsyncTask.OnPhotoProcessed() {
        @Override
        public void onDataReady(@Nullable ImageCard imageCard) {
            if (imageCard != null) {
                mImageView.setImageBitmap(imageCard.getImage());
            }
            mProgressDialog.setMessage(getResources().getString(R.string.progress_vait));
            mProgressDialog.show();
            mFirebaseHelper.saveImage(mStorageReference, imageCard);
        }
    };

    @Override
    public void onBackPressed() {
        if (!mInputNameRecipe.getText().toString().equals("") || mDownloadUrlCamera != null
                || !mInputIngredients.getText().toString().equals("")) {
            int backPressedTrue = 1;
            int backPressedTFalse = 0;
            if (mBackPressed == backPressedTrue) {
                IntentHelper.intentRecipeListActivity(mContext, mIntent.getStringExtra(RECIPE_LIST));
            } else if (mBackPressed == backPressedTFalse) {
                Toast.makeText(mContext, getResources().getString(R.string.input_will_lost), Toast.LENGTH_SHORT).show();
                mBackPressed = backPressedTrue;
            } else {
                IntentHelper.intentRecipeListActivity(mContext, mIntent.getStringExtra(RECIPE_LIST));
            }
        } else {
            IntentHelper.intentRecipeListActivity(mContext, mIntent.getStringExtra(RECIPE_LIST));
        }
    }
}

