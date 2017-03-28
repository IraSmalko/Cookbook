package com.exemple.android.cookbook.helpers;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.activities.AddCategoryRecipeActivity;
import com.exemple.android.cookbook.activities.AuthenticationActivity;
import com.exemple.android.cookbook.activities.InfoVRActivity;
import com.exemple.android.cookbook.activities.SelectedRecipeListActivity;
import com.exemple.android.cookbook.entity.CategoryRecipes;
import com.exemple.android.cookbook.entity.Recipe;
import com.exemple.android.cookbook.entity.RecipeForSQLite;
import com.exemple.android.cookbook.entity.StepRecipe;

import java.util.ArrayList;
import java.util.List;

public class VoiceRecognitionHelper {

    private static final String APP_PREFERENCES = "mysettings";
    private static final String VOICE_RECOGNITION = "VoiceRecognition";

    private SharedPreferences mSharedPreferences;
    private Context mContext;
    private int mStatus;
    private int mOn = 1;
    private ArrayList<String> mVRResult;
    private List<CategoryRecipes> mForVoice;
    private int mIsPersonal, mIterator;
    private String mRecipeList, mRecipeName, mDescription;

    public VoiceRecognitionHelper(Context context) {
        mContext = context;
    }

    public void startVoiceRecognition() {
        if (getStatusVoiceRecognition() == mOn) {
            if (new CheckOnlineHelper(mContext).isOnline()) {
                IntentHelper.startVoiceRecognitionActivity(mContext);
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.not_online), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public AlertDialog createdAlertDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_voice_recognition, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(view);
        int off = 0;
        if (getStatusVoiceRecognition() == off) {
            builder.setMessage(mContext.getResources().getString(R.string.on_voice_alert_message));
            mStatus = mOn;
        } else {
            builder.setMessage(mContext.getResources().getString(R.string.off_voice_alert_message));
            mStatus = off;
        }
        builder.setPositiveButton(mContext.getResources().getString(R.string
                .positive_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                saveStatusVoiceRecognition(mStatus);
            }
        })
                .setNegativeButton(mContext.getResources().getString(R.string
                        .negative_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        TextView info = (TextView) view.findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.startActivity(mContext, new Intent(mContext, InfoVRActivity.class), null);
            }
        });
        return builder.create();
    }

    private void saveStatusVoiceRecognition(int status) {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFERENCES, mContext.MODE_PRIVATE);
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.putInt(VOICE_RECOGNITION, status);
        ed.apply();
    }

    private int getStatusVoiceRecognition() {
        int status = 0;
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFERENCES, mContext.MODE_PRIVATE);
        if (mSharedPreferences.contains(VOICE_RECOGNITION)) {
            status = mSharedPreferences.getInt(VOICE_RECOGNITION, status);
        }
        return status;
    }

    public void getDataForVR() {
        new FirebaseHelper(new FirebaseHelper.OnGetCategoryListForVR() {
            @Override
            public void OnGet(List<CategoryRecipes> forVoice) {
                mForVoice = forVoice;
                for (CategoryRecipes recipeList : mForVoice) {
                    if (mVRResult.contains(recipeList.getName().toLowerCase())) {
                        IntentHelper.intentRecipeListActivity(mContext, recipeList.getName());
                    } else {
                        new FirebaseHelper(new FirebaseHelper.OnGetRecipeListForVR() {
                            @Override
                            public void OnGet(List<Recipe> recipeForVoice, String recipeList) {
                                for (Recipe recipe : recipeForVoice) {
                                    if (mVRResult.contains(recipe.getName().toLowerCase())) {
                                        IntentHelper.intentRecipeActivity(mContext, recipe.getName(), recipe
                                                .getPhotoUrl(), recipe.getIsPersonal(), recipeList,
                                                new FirebaseHelper().getUsername());
                                    }
                                }
                            }
                        }).getRecipeListForVR(recipeList.getName(), mContext);
                    }
                }
            }
        }).getCategoryListForVR();

//            for (SelectedRecipe recipe : new DataSourceSQLite(mContext).getRecipe()) {
//                if (mVRResult.contains(recipe.getName().toLowerCase())) {
//                    IntentHelper.intentSelectedRecipeActivity(mContext, recipe.getName(), recipe
//                            .getPhotoUrl(), recipe.getDescription(), recipe.getIdRecipe());
//                }
    }

    public void getLocalDataForVR(ArrayList<String> vRResult) {
        if (vRResult.contains(mContext.getResources().getString(R.string.saved_vr))) {
            ActivityCompat.startActivity(mContext, new Intent(mContext, SelectedRecipeListActivity.class), null);
        } else if (vRResult.contains(mContext.getResources().getString(R.string.authorization_vr))) {
            ActivityCompat.startActivity(mContext, new Intent(mContext, AuthenticationActivity.class), null);
        } else if (vRResult.contains(mContext.getResources().getString(R.string.add_category_vr))) {
            ActivityCompat.startActivity(mContext, new Intent(mContext, AddCategoryRecipeActivity.class), null);
        }
    }

    public void addRecipeFromVR(ArrayList<String> vRResult, ArrayList<String> categoryList) {
        if (vRResult.contains(mContext.getResources().getString(R.string.add_recipe_vr))) {
            IntentHelper.intentAddRecipeActivity(mContext, categoryList, null);
        }
    }

    private void saveRecipeFromVR(Recipe recipe, String recipeList) {
        mIsPersonal = recipe.getIsPersonal();
        mRecipeList = recipeList;
        mRecipeName = recipe.getName();

        if (new CheckOnlineHelper(mContext).isOnline()) {
            Glide.with(mContext)
                    .load(recipe.getPhotoUrl())
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(660, 480) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
                                    resource, Environment.getExternalStorageDirectory().getAbsolutePath(), null);
                            new WriterDAtaSQLiteAsyncTask.WriterRecipe(mContext, new WriterDAtaSQLiteAsyncTask.WriterRecipe.OnWriterSQLite() {
                                @Override
                                public void onDataReady(Integer integer) {
                                    new FirebaseHelper().getStepsRecipe(mContext, integer, mIsPersonal, mRecipeList,
                                            mRecipeName, new FirebaseHelper().getUsername());
                                }
                            }).execute(new RecipeForSQLite(mRecipeName, path, 0, null));
                        }
                    });
        } else {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.not_online), Toast.LENGTH_SHORT).show();
        }
    }

    public int nextStepVR(int iterator, List<StepRecipe> stepRecipe, Recipe recipe, String recipeList,
                          ActionBar actionBar, TextView textView, ImageView imageView) {
        int noSteps = -1;
        if (iterator < stepRecipe.size() && iterator != noSteps) {
            actionBar.setTitle(stepRecipe.get(iterator).getNumberStep());
            textView.setText(stepRecipe.get(iterator).getTextStep());
            Glide.with(mContext).load(stepRecipe.get(iterator).getPhotoUrlStep()).into(imageView);
        } else {
            IntentHelper.intentRecipeActivity(mContext, recipe.getName(), recipe.getPhotoUrl(), recipe
                    .getIsPersonal(), recipeList, new FirebaseHelper().getUsername());
        }
        return iterator;
    }

    public void onActivityResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            mVRResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Toast.makeText(mContext, mVRResult.get(0), Toast.LENGTH_LONG).show();

            getDataForVR();
        }
    }

    public void onActivityResult(int resultCode, Intent data, Recipe recipe, String recipeList) {
        if (resultCode == Activity.RESULT_OK) {
            mVRResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Toast.makeText(mContext, mVRResult.get(0), Toast.LENGTH_LONG).show();
            if (mVRResult.contains(mContext.getResources().getString(R.string.detail_vr))) {
                IntentHelper.intentStepRecipeActivity(mContext, recipe.getName(), recipe
                        .getPhotoUrl(), recipe.getIsPersonal(), recipeList);
            } else if (mVRResult.contains(mContext.getResources().getString(R.string.save_vr)) ||
                    mVRResult.contains(mContext.getResources().getString(R.string.save_recipe_vr))) {
                saveRecipeFromVR(recipe, recipeList);
            } else {
                getDataForVR();
            }
        }
    }

    public int onActivityResult(int resultCode, Intent data, Recipe recipe, String recipeList,
                                int iterator, List<StepRecipe> stepRecipe, ActionBar actionBar,
                                TextView textView, ImageView imageView) {
        if (resultCode == Activity.RESULT_OK) {
            mVRResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Toast.makeText(mContext, mVRResult.get(0), Toast.LENGTH_LONG).show();
            if (mVRResult.contains(mContext.getResources().getString(R.string.save_vr))
                    || mVRResult.contains(mContext.getResources().getString(R.string.save_recipe_vr))) {
                saveRecipeFromVR(recipe, recipeList);
            } else if (mVRResult.contains(mContext.getResources().getString(R.string.next_step))) {
                mIterator = nextStepVR(++iterator, stepRecipe, recipe, recipeList, actionBar, textView, imageView);
            } else if (mVRResult.contains(mContext.getResources().getString(R.string.step_back))
                    || mVRResult.contains(mContext.getResources().getString(R.string.previous_step))) {
                mIterator = nextStepVR(--iterator, stepRecipe, recipe, recipeList, actionBar, textView, imageView);
            } else {
                getDataForVR();
            }
        }
        return mIterator;
    }
}
