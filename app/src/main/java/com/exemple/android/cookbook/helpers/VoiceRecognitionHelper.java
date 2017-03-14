package com.exemple.android.cookbook.helpers;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.exemple.android.cookbook.entity.CategoryRecipes;

import java.util.ArrayList;
import java.util.List;

public class VoiceRecognitionHelper {

    private Context mContext;

    public VoiceRecognitionHelper(Context context) {
        mContext = context;
    }

    public void onActivityResult(int resultCode, Intent data, List<CategoryRecipes> mForVoice) {
        if (resultCode == Activity.RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Toast.makeText(mContext, matches.get(0),
                    Toast.LENGTH_LONG).show();
            for (CategoryRecipes recipeList : mForVoice) {
                if (matches.contains(recipeList.getName().toLowerCase())) {
                    IntentHelper.intentRecipeListActivity(mContext, recipeList.getName());
                }
            }
        }
    }

}
