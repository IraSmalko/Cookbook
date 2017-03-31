package com.exemple.android.cookbook.helpers;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.widget.Toast;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.ForWriterStepsRecipe;
import com.exemple.android.cookbook.entity.Ingredient;
import com.exemple.android.cookbook.entity.RecipeForSQLite;
import com.exemple.android.cookbook.entity.SelectedRecipe;
import com.exemple.android.cookbook.entity.SelectedStepRecipe;
import com.exemple.android.cookbook.entity.StepRecipe;

import java.util.ArrayList;
import java.util.List;

public class DataSourceSQLite {

    private static final String RECIPE = "recipe";
    private static final String PHOTO = "photo";
    private static final String ID_RECIPE = "id_recipe";
    private static final String TEXT_STEP = "text_step";
    private static final String PHOTO_STEP = "photo_step";
    private static final String NUMBER_STEP = "number_step";
    private static final String ID = "id";
    private static final String INGREDIENT_NAME = "ingredient_name";
    private static final String INGREDIENT_QUANTITY = "ingredient_quantity";
    private static final String INGREDIENT_UNIT = "ingredient_unit";
    private static final String IN_BASKET = "in_basket";
    private static final String IN_SAVED = "in_saved";

    public static final int REQUEST_BASKET = 117;
    public static final int REQUEST_SAVED = 127;

    private SQLiteDatabase mDatabase;
    private DBHelper mDBHelper;
    private int mIterator = 0;
    private Context mContext;
    private List<SelectedStepRecipe> mSelectedStepRecipes = new ArrayList<>();
    private List<Ingredient> mRecipeIngredients = new ArrayList<>();
    private ForWriterStepsRecipe mWriterStepsRecipe;
    private String mPathPhotoStep;

    public DataSourceSQLite(Context context) {
        mDBHelper = new DBHelper(context);
        mContext = context;
    }

    public void open() {
        mDatabase = mDBHelper.getWritableDatabase();
    }

    private void close() {
        mDBHelper.close();
    }

    public int saveRecipe(RecipeForSQLite recipe) {
        open();
        ContentValues cvRecipe = new ContentValues();

        cvRecipe.put(RECIPE, recipe.getName());
        cvRecipe.put(PHOTO, recipe.getPhotoUrl());
        cvRecipe.put(IN_SAVED, recipe.getIsInSaved());
        cvRecipe.put(IN_BASKET, recipe.getIsInBasket());
        long rowID = mDatabase.insertOrThrow(DBHelper.TABLE_RECIPE, null, cvRecipe);

        close();
        return (int) rowID;
    }

    public void saveIngredient(List<Ingredient> ingredients, int idRecipe) {
        open();
        for (Ingredient ingredient : ingredients) {
            ContentValues cvIngredient = new ContentValues();
            cvIngredient.put(ID_RECIPE, idRecipe);
            cvIngredient.put(INGREDIENT_NAME, ingredient.getName());
            cvIngredient.put(INGREDIENT_QUANTITY, ingredient.getQuantity());
            cvIngredient.put(INGREDIENT_UNIT, ingredient.getUnit());
            mDatabase.insertOrThrow(DBHelper.TABLE_INGREDIENTS_RECIPE, null, cvIngredient);
        }
        close();
    }

    public void saveStepsSQLite(List<StepRecipe> stepRecipe, int idRecipe) {
        loadPhoto(new ForWriterStepsRecipe(stepRecipe, mPathPhotoStep, idRecipe, mIterator));
    }

    public ForWriterStepsRecipe saveSteps(ForWriterStepsRecipe forWriterStepsRecipe) {
        open();
        mIterator = forWriterStepsRecipe.getIterator();
        ContentValues cvStepRecipe = new ContentValues();
        cvStepRecipe.put(ID_RECIPE, forWriterStepsRecipe.getIdRecipe());
        cvStepRecipe.put(NUMBER_STEP, forWriterStepsRecipe.getStepRecipes().get(mIterator).getNumberStep());
        cvStepRecipe.put(TEXT_STEP, forWriterStepsRecipe.getStepRecipes().get(mIterator).getTextStep());
        cvStepRecipe.put(PHOTO_STEP, forWriterStepsRecipe.getPathPhotoStep());
        mDatabase.insertOrThrow(DBHelper.TABLE_STEP_RECIPE, null, cvStepRecipe);
        forWriterStepsRecipe.setIterator(forWriterStepsRecipe.getIterator() + 1);
        close();
        return forWriterStepsRecipe;
    }

    public void loadPhoto(ForWriterStepsRecipe forWriterStepsRecipe) {
        mWriterStepsRecipe = forWriterStepsRecipe;
        if (forWriterStepsRecipe.getIterator() < forWriterStepsRecipe.getStepRecipes().size()) {
            new PhotoLoaderAsyncTask(mContext, new PhotoLoaderAsyncTask.PhotoLoadProcessed() {
                @Override
                public void onBitmapReady(Bitmap bitmap) {
                    mPathPhotoStep = MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
                            bitmap, mContext.getCacheDir().getAbsolutePath(), null);
                    mWriterStepsRecipe.setPathPhotoStep(mPathPhotoStep);
                    new WriterDAtaSQLiteAsyncTask.WriterStepsRecipe(mContext)
                            .execute(mWriterStepsRecipe);
                }
            }).execute(mWriterStepsRecipe.getStepRecipes().get(mWriterStepsRecipe.getIterator()).getPhotoUrlStep());
        } else {
            close();
        }
    }

    public List<Ingredient> readRecipeIngredients(int idRecipe) {
        open();
        Cursor c = mDatabase.rawQuery("SELECT * FROM " + DBHelper.TABLE_INGREDIENTS_RECIPE
                + " WHERE " + ID_RECIPE + " == " + idRecipe, null);

        if (c.moveToFirst()) {
            do {
                int ingredientNameIndex = c.getColumnIndex(INGREDIENT_NAME);
                int ingredientQuantityIndex = c.getColumnIndex(INGREDIENT_QUANTITY);
                int ingredientUnitIndex = c.getColumnIndex(INGREDIENT_UNIT);

                mRecipeIngredients.add(new Ingredient(
                        c.getString(ingredientNameIndex),
                        c.getFloat(ingredientQuantityIndex),
                        c.getString(ingredientUnitIndex)));

            } while (c.moveToNext());
        }
        return mRecipeIngredients;
    }

    public List<SelectedStepRecipe> readStepRecipe(int idRecipe) {
        open();
        SelectedStepRecipe sSR = new SelectedStepRecipe();
        Cursor c = mDatabase.rawQuery("SELECT * FROM " + DBHelper
                .TABLE_STEP_RECIPE + " WHERE " + ID_RECIPE + " == " + idRecipe, null);

        if (c.moveToFirst()) {
            do {
                int idColIndex = c.getColumnIndex(ID_RECIPE);
                int numberStepColIndex = c.getColumnIndex(NUMBER_STEP);
                int textStepColIndex = c.getColumnIndex(TEXT_STEP);
                int photoStepColIndex = c.getColumnIndex(PHOTO_STEP);

                mSelectedStepRecipes.add(new SelectedStepRecipe(c.getString(numberStepColIndex), c
                        .getString(textStepColIndex), c.getString(photoStepColIndex), c.getInt(idColIndex)));
            } while (c.moveToNext());
            sSR.setNumberStep(mSelectedStepRecipes.get(0).getNumberStep());
            sSR.setTextStep(mSelectedStepRecipes.get(0).getTextStep());
            sSR.setPhotoUrlStep(mSelectedStepRecipes.get(0).getPhotoUrlStep());

        } else {
            c.close();
            Toast.makeText(mContext, mContext.getResources().getString(R.string
                    .no_information_available), Toast.LENGTH_SHORT).show();
        }
        return mSelectedStepRecipes;
    }

    public void removeRecipe(int id) {
        open();
        mDatabase.delete(DBHelper.TABLE_RECIPE, "id = " + id, null);
        mDatabase.delete(DBHelper.TABLE_INGREDIENTS_RECIPE, "id_recipe = " + id, null);
        mDatabase.delete(DBHelper.TABLE_STEP_RECIPE, "id_recipe = " + id, null);
        close();
    }

    public List<SelectedRecipe> getRecipe() {
        List<SelectedRecipe> recipesList = new ArrayList<>();
        open();
        Cursor c = mDatabase.query(RECIPE, null, null, null, null, null, null);

        if (c.moveToFirst()) {
            do {
                int idColIndex = c.getColumnIndex(ID);
                int recipeColIndex = c.getColumnIndex(RECIPE);
                int photoColIndex = c.getColumnIndex(PHOTO);

                recipesList.add(new SelectedRecipe(c.getString(recipeColIndex), c
                        .getString(photoColIndex), c.getInt(idColIndex)));
            } while (c.moveToNext());
        } else {
            c.close();
        }
        close();
        return recipesList;
    }

    public List<SelectedRecipe> getRecipes(int requestCode) {


        List<SelectedRecipe> recipesList = new ArrayList<>();
        open();

        String field;

        if (requestCode == REQUEST_BASKET) {
            field = "in_basket = 1";
        } else if (requestCode == REQUEST_SAVED) {
            field = "in_saved = 1";
        } else {
            field = null;
        }
        Cursor c = mDatabase.query(RECIPE, null, field, null, null, null, null);

        if (c.moveToFirst()) {
            do {
                int idColIndex = c.getColumnIndex(ID);
                int recipeColIndex = c.getColumnIndex(RECIPE);
                int photoColIndex = c.getColumnIndex(PHOTO);

                recipesList.add(new SelectedRecipe(c.getString(recipeColIndex), c
                        .getString(photoColIndex), c.getInt(idColIndex)));
            } while (c.moveToNext());
        } else {
            c.close();
        }
        close();
        return recipesList;
    }

}
