package com.exemple.android.cookbook.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.ImageCard;
import com.exemple.android.cookbook.entity.SelectedRecipe;
import com.exemple.android.cookbook.entity.SelectedStepRecipe;
import com.exemple.android.cookbook.entity.StepRecipe;
import com.exemple.android.cookbook.helpers.CheckOnlineHelper;
import com.exemple.android.cookbook.helpers.CropHelper;
import com.exemple.android.cookbook.helpers.DataSourceSQLite;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditRecipeStepActivity extends AppCompatActivity {
    private static final int REQUEST_CROP_PICTURE = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 22;
    private static final int GALLERY_REQUEST = 13;
    private static final String RECIPE_LIST = "recipeList";
    private static final String RECIPE = "recipe";
    private static final String NUMBER_STEP = "mNumberStep";

    private static final String PHOTO = "photo";
    private static final String ID_RECIPE = "id_recipe";
    private static final int INT_EXTRA = 0;
    private static final String DESCRIPTION = "description";

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
    private Context mContext = EditRecipeStepActivity.this;

    private List<SelectedStepRecipe> mSelectedStepRecipes;
    private List<StepRecipe> mStepRecipe = new ArrayList<>();

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


        mIntent = getIntent();

        mActionBar = getSupportActionBar();


        mActionBar.setTitle(getResources().getString(R.string.step) + " " + mNumberStep);

        DataSourceSQLite dataSource = new DataSourceSQLite(this);
        mSelectedStepRecipes = dataSource.readStepRecipe(mIntent.getIntExtra(ID_RECIPE, INT_EXTRA));

        if (mSelectedStepRecipes.isEmpty()) {
            Toast.makeText(mContext, getResources().getString(R.string
                    .no_information_available), Toast.LENGTH_SHORT).show();
        } else {
            mActionBar.setTitle(mSelectedStepRecipes.get(0).getNumberStep());
            mInputNameRecipe.setText(mSelectedStepRecipes.get(0).getTextStep());
            try {
                mImageView.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), Uri
                        .parse(mSelectedStepRecipes.get(0).getPhotoUrlStep())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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


        btnSave.setOnClickListener(onClickListener);
        btnPhotoFromCamera.setOnClickListener(onClickListener);
        btnPhotoFromGallery.setOnClickListener(onClickListener);

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
//                        if (mDownloadUrlCamera != null) {
//                            StepRecipe stepRecipe = new StepRecipe(getResources()
//                                    .getString(R.string.step) + " " + mNumberStep, mInputNameRecipe
//                                    .getText().toString(), mDownloadUrlCamera.toString());
//                            SelectedStepRecipe selectedStepRecipe = new SelectedStepRecipe(stepRecipe,mIntent.getIntExtra(ID_RECIPE,INT_EXTRA));
//                            mSelectedStepRecipes
//                            new DataSourceSQLite(mContext).saveStepsSQLite();
//                            Toast.makeText(mContext, getResources()
//                                    .getString(R.string.data_save), Toast.LENGTH_SHORT).show();
//                            mNumberStep = ++mNumberStep;
//                            mActionBar.setTitle(getResources().getString(R.string.step) + " " + mNumberStep);
//                            mImageView.setImageResource(R.drawable.dishes);
//                            mInputNameRecipe.setText("");
//                            mDownloadUrlCamera = null;
//                        } else {
//                            Toast.makeText(mContext, getResources()
//                                    .getString(R.string.no_photo), Toast.LENGTH_LONG).show();
//                        }
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
