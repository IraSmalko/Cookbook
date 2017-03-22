package com.exemple.android.cookbook.helpers;


import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.activities.AddRecipeActivity;
import com.exemple.android.cookbook.activities.AddStepActivity;
import com.exemple.android.cookbook.activities.RecipeActivity;
import com.exemple.android.cookbook.activities.RecipeListActivity;
import com.exemple.android.cookbook.activities.SelectedRecipeActivity;
import com.exemple.android.cookbook.activities.SelectedStepRecipeActivity;
import com.exemple.android.cookbook.activities.ShoppingBasketActivity;
import com.exemple.android.cookbook.activities.ShoppingRecipeActivity;
import com.exemple.android.cookbook.activities.StepRecipeActivity;

import java.util.ArrayList;

public class IntentHelper {

    private static final int REQUEST_CODE = 1234;
    private static final String RECIPE_LIST = "recipeList";
    private static final String RECIPE = "recipe";
    private static final String PHOTO = "photo";
    private static final String DESCRIPTION = "description";
    private static final String USERNAME = "username";
    private static final String ARRAY_LIST_RECIPE = "ArrayListRecipe";
    private static final String ID_RECIPE = "id_recipe";
    private static final String IS_PERSONAL = "isPersonal";

    public IntentHelper() {
    }

    static public void intentAddRecipeActivity(Context context, ArrayList<String> nameRecipesList,
                                               String recipeList) {
        Intent intent = new Intent(context, AddRecipeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(RECIPE_LIST, recipeList);
        intent.putStringArrayListExtra(ARRAY_LIST_RECIPE, nameRecipesList);
        ActivityCompat.startActivity(context, intent, null);
    }

    static public void intentAddStepActivity(Context context, String recipeList, String recipe) {
        Intent intent = new Intent(context, AddStepActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(RECIPE_LIST, recipeList);
        intent.putExtra(RECIPE, recipe);
        ActivityCompat.startActivity(context, intent, null);
    }

    static public void intentRecipeListActivity(Context context, String recipeList) {
        Intent intent = new Intent(context, RecipeListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(RECIPE_LIST, recipeList);
        ActivityCompat.startActivity(context, intent, null);
    }

    static public void startVoiceRecognitionActivity(Context context) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, context.getResources()
                .getString(R.string.voice_recognition_intent));
        ActivityCompat.startActivityForResult(
                (AppCompatActivity) context, intent, REQUEST_CODE, null);
    }

    static public void intentStepRecipeActivity(Context context, String recipe, String photo,
                                                String description, int isPersonal, String recipeList) {
        Intent intent = new Intent(context, StepRecipeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(RECIPE, recipe);
        intent.putExtra(PHOTO, photo);
        intent.putExtra(DESCRIPTION, description);
        intent.putExtra(IS_PERSONAL, isPersonal);
        intent.putExtra(RECIPE_LIST, recipeList);
        ActivityCompat.startActivity(context, intent, null);
    }

    static public void intentRecipeActivity(Context context, String recipe, String photo,
                                            String description, int isPersonal, String recipeList, String username) {
        Intent intent = new Intent(context, RecipeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(RECIPE, recipe);
        intent.putExtra(PHOTO, photo);
        intent.putExtra(DESCRIPTION, description);
        intent.putExtra(IS_PERSONAL, isPersonal);
        intent.putExtra(RECIPE_LIST, recipeList);
        intent.putExtra(USERNAME, username);
        ActivityCompat.startActivity(context, intent, null);
    }

    static public void intentSelectedRecipeActivity(Context context, String recipe, String photo,
                                                    String description, int id_recipe){
        Intent intent = new Intent(context, SelectedRecipeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(RECIPE, recipe);
        intent.putExtra(PHOTO, photo);
        intent.putExtra(DESCRIPTION, description);
        intent.putExtra(ID_RECIPE, id_recipe);
        ActivityCompat.startActivity(context, intent, null);
    }

    static public void intentSelectedRecipeActivity(Context context, String recipe, String photo,
                                                    String description){
        Intent intent = new Intent(context, SelectedRecipeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(RECIPE, recipe);
        intent.putExtra(PHOTO, photo);
        intent.putExtra(DESCRIPTION, description);
        ActivityCompat.startActivity(context, intent, null);
    }

    static public void intentSelectedStepRecipeActivity(Context context, String recipe, String photo,
                                                        String description, int id_recipe){
        Intent intent = new Intent(context, SelectedStepRecipeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(RECIPE, recipe);
        intent.putExtra(PHOTO, photo);
        intent.putExtra(DESCRIPTION, description);
        intent.putExtra(ID_RECIPE, id_recipe);
        ActivityCompat.startActivity(context, intent, null);
    }

    static public void intentShoppingBasketActivity(Context context, String recipe){
        Intent intent = new Intent(context, ShoppingRecipeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(RECIPE, recipe);
        ActivityCompat.startActivity(context, intent, null);
    }
}

