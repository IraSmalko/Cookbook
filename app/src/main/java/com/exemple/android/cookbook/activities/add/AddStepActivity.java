package com.exemple.android.cookbook.activities.add;


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

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.ImageCard;
import com.exemple.android.cookbook.entity.firebase.FirebaseStepRecipe;
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

public class AddStepActivity extends AppCompatActivity {

    private static final int REQUEST_CROP_PICTURE = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 22;
    private static final int GALLERY_REQUEST = 13;
    private static final String RECIPE_LIST = "recipeList";
    private static final String RECIPE = "recipe";
    private static final String NUMBER_STEP = "mNumberStep";

    private EditText mInputNameRecipe;
    private ImageView mImageView;
    private ProgressDialog mProgressDialog;
    private ActionBar mActionBar;

    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private Uri mDownloadUrlCamera;
    private int mNumberStep = 1;
    private Intent mIntent;
    private PhotoFromCameraHelper mPhotoFromCameraHelper;
    private FirebaseHelper mFirebaseHelper;
    private CropHelper mCropHelper;
    private Context mContext = AddStepActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_step_activity);

        mImageView = (ImageView) findViewById(R.id.photoImageView);
        mInputNameRecipe = (EditText) findViewById(R.id.name);
        ImageButton btnPhotoFromGallery = (ImageButton) findViewById(R.id.categoryRecipesPhotoUrlGallery);
        ImageButton btnPhotoFromCamera = (ImageButton) findViewById(R.id.categoryRecipesPhotoUrlCamera);
        Button btnSave = (Button) findViewById(R.id.btnSave);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getResources().getString(R.string.progress_dialog_title));

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            String username = firebaseUser.getDisplayName();

            mIntent = getIntent();
            mDatabaseReference = firebaseDatabase.getReference().child(username + "/Step_recipe/" + mIntent
                    .getStringExtra(RECIPE_LIST) + "/" + mIntent.getStringExtra(RECIPE));
            mStorageReference = firebaseStorage.getReference().child(username + "/Step_Recipes" + "/" + mIntent
                    .getStringExtra(RECIPE_LIST) + "/" + mIntent.getStringExtra(RECIPE));

            mActionBar = getSupportActionBar();

            if (savedInstanceState != null && savedInstanceState.containsKey(NUMBER_STEP)) {
                mNumberStep = savedInstanceState.getInt(NUMBER_STEP);
            }
            mActionBar.setTitle(getResources().getString(R.string.step) + " " + mNumberStep);

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
                    ProcessPhotoAsyncTask photoAsyncTask = new ProcessPhotoAsyncTask(mContext, listener);
                    photoAsyncTask.execute(cropImageUri);
                }
            });

            boolean isOnline = new CheckOnlineHelper(mContext).isOnline();
            if (isOnline) {
                btnSave.setOnClickListener(onClickListener);
                btnPhotoFromCamera.setOnClickListener(onClickListener);
                btnPhotoFromGallery.setOnClickListener(onClickListener);
            } else {
                Toast.makeText(mContext, getResources()
                        .getString(R.string.not_online), Toast.LENGTH_SHORT).show();
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
                case R.id.btnSave:
                    if (mInputNameRecipe.getText().toString().equals("")) {
                        Toast.makeText(mContext, getResources()
                                .getString(R.string.no_description_step), Toast.LENGTH_SHORT).show();
                    } else {
                        if (mDownloadUrlCamera != null) {
                            FirebaseStepRecipe stepRecipe = new FirebaseStepRecipe(getResources()
                                    .getString(R.string.step) + " " + mNumberStep, mInputNameRecipe
                                    .getText().toString(), mDownloadUrlCamera.toString());
                            String recipeId = mDatabaseReference.push().getKey();
                            mDatabaseReference.child(recipeId).setValue(stepRecipe);

                            Toast.makeText(mContext, getResources()
                                    .getString(R.string.data_save), Toast.LENGTH_SHORT).show();
                            mNumberStep = ++mNumberStep;
                            mActionBar.setTitle(getResources().getString(R.string.step) + " " + mNumberStep);
                            mImageView.setImageResource(R.drawable.dishes);
                            mInputNameRecipe.setText("");
                            mDownloadUrlCamera = null;
                        } else {
                            Toast.makeText(mContext, getResources()
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int noSaveStepNumber = 1;
        if (mNumberStep > noSaveStepNumber) {
            outState.putInt(NUMBER_STEP, mNumberStep);
        }
    }

    @Override
    public void onBackPressed() {
        IntentHelper.intentRecipeListActivity(mContext, mIntent.getStringExtra(RECIPE_LIST));
    }
}
