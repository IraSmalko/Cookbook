package com.exemple.android.cookbook.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.exemple.android.cookbook.entity.RecipeForSQLite;
import com.exemple.android.cookbook.entity.SelectedStepRecipe;
import com.exemple.android.cookbook.helpers.CropHelper;
import com.exemple.android.cookbook.helpers.DataSourceSQLite;
import com.exemple.android.cookbook.helpers.IntentHelper;
import com.exemple.android.cookbook.helpers.PhotoFromCameraHelper;
import com.exemple.android.cookbook.helpers.ProcessPhotoAsyncTask;
import com.exemple.android.cookbook.helpers.SwipeHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditRecipeActivity extends AppCompatActivity {
    private static final int REQUEST_CROP_PICTURE = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 22;
    private static final int GALLERY_REQUEST = 13;
    private static final String RECIPE_LIST = "recipeList";

    private static final String RECIPE = "recipe";
    private static final String PHOTO = "photo";
    private static final String ID_RECIPE = "id_recipe";
    private static final int INT_EXTRA = 0;

    private EditText mInputNameRecipe, mInputIngredients, mQuantity, mUnit;
    private ImageView mImageView;
    private ProgressDialog mProgressDialog;

    private Uri mDownloadUrlCamera;
    private List<Ingredient> mIngredients = new ArrayList<>();
    private int mBackPressed = 0;
    private Intent mIntent;
    private int mIdRecipe;
    private PhotoFromCameraHelper mPhotoFromCameraHelper;
    private CropHelper mCropHelper;
    private Context mContext = EditRecipeActivity.this;
    private RecyclerView mRecyclerView;
    private IngredientsAdapter mIngredientsAdapter;

    private List<SelectedStepRecipe> mSelectedStepsRecipe;

    private String newPhotoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

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

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getResources().getString(R.string.progress_dialog_title));

        mIntent = getIntent();
        mIdRecipe = mIntent.getIntExtra(ID_RECIPE, INT_EXTRA);
        newPhotoPath = mIntent.getStringExtra(PHOTO);
        mInputNameRecipe.setText(mIntent.getStringExtra(RECIPE));

        try {
            mImageView.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(),
                    Uri.parse(mIntent.getStringExtra(PHOTO))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        DataSourceSQLite dataSource = new DataSourceSQLite(this);
        mIngredients = dataSource.readRecipeIngredients(mIdRecipe);
        mSelectedStepsRecipe = dataSource.readStepRecipe(mIdRecipe);

        Log.d("LOGG", mIngredients.toString());


        mPhotoFromCameraHelper = new PhotoFromCameraHelper(mContext, new PhotoFromCameraHelper.OnPhotoPicked() {
            @Override
            public void onPicked(Uri photoUri) {
                mCropHelper.cropImage(photoUri);
            }
        });

        mCropHelper = new CropHelper(mContext, new CropHelper.OnCrop() {
            @Override
            public void onCrop(Uri cropImageUri) {
                final ProcessPhotoAsyncTask photoAsyncTask = new ProcessPhotoAsyncTask(mContext, listener);
                photoAsyncTask.execute(cropImageUri);
                mDownloadUrlCamera = cropImageUri;
            }
        });

        btnSave.setOnClickListener(onClickListener);
        btnPhotoFromCamera.setOnClickListener(onClickListener);
        btnPhotoFromGallery.setOnClickListener(onClickListener);
        plusIngredient.setOnClickListener(onClickListener);

        mIngredientsAdapter = new IngredientsAdapter(this,
                mIngredients,
                true);

        mRecyclerView.setAdapter(mIngredientsAdapter);
        new SwipeHelper(mRecyclerView, this).attachSwipeIngredients();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private final ProcessPhotoAsyncTask.OnPhotoProcessed listener = new ProcessPhotoAsyncTask.OnPhotoProcessed() {
        @Override
        public void onDataReady(@Nullable ImageCard imageCard) {
            if (imageCard != null) {
                mImageView.setImageBitmap(imageCard.getImage());
                newPhotoPath = MediaStore.Images.Media.insertImage(
                        getContentResolver(),
                        imageCard.getImage(),
                        getApplicationContext().getCacheDir().getAbsolutePath(),
                        null);
            }
        }
    };

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
                        mIngredients.add(new Ingredient(mInputIngredients.getText().toString(),
                                Float.valueOf(mQuantity.getText().toString()),
                                mUnit.getText().toString()));
                        mIngredientsAdapter.updateAdapter(mIngredients);
                        mInputIngredients.setText("");
                        mQuantity.setText("");
                        mUnit.setText("");
                    }
                    break;
                case R.id.btnSave:
                    DataSourceSQLite dataSource = new DataSourceSQLite(mContext);
                    dataSource.replaceRecipe(new RecipeForSQLite(
                            mInputNameRecipe.getText().toString(),
                            newPhotoPath,
                            0,
                            mIngredientsAdapter.getItems(),
                            1,
                            0), mIdRecipe);
                    Toast.makeText(mContext, getResources()
                            .getString(R.string.data_save), Toast.LENGTH_SHORT).show();
                    new IntentHelper().intentEditRecipeStepActivity(mContext,
                            mInputNameRecipe.getText().toString(),
                            newPhotoPath, mIdRecipe);
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

    @Override
    public void onBackPressed() {
        IntentHelper.intentSelectedRecipeActivity(mContext, mIntent.getStringExtra(RECIPE), mIntent
                .getStringExtra(PHOTO), mIntent.getIntExtra(ID_RECIPE, INT_EXTRA));
    }
}
