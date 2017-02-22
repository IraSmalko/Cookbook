package com.exemple.android.cookbook.helpers;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.SelectedStepRecipe;
import com.exemple.android.cookbook.entity.StepRecipe;

import java.util.ArrayList;
import java.util.List;

public class DataSourceSQLite {

    private static final String RECIPE = "recipe";
    private static final String PHOTO = "photo";
    private static final String DESCRIPTION = "description";
    private static final String ID_RECIPE = "id_recipe";
    private static final String TEXT_STEP = "text_step";
    private static final String PHOTO_STEP = "photo_step";
    private static final String NUMBER_STEP = "number_step";

    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private int iterator = 0;
    private Context context;
    private List<StepRecipe> stepRecipe = new ArrayList<>();
    private List<SelectedStepRecipe> selectedStepRecipes = new ArrayList<>();
    private int idRecipe;
    private String pathPhotoStep;

    public DataSourceSQLite(Context context) {
        dbHelper = new DBHelper(context);
        this.context = context;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public int saveRecipe(String recipe, String path, String description) {
        open();
        ContentValues cvRecipe = new ContentValues();

        cvRecipe.put(RECIPE, recipe);
        cvRecipe.put(PHOTO, path);
        cvRecipe.put(DESCRIPTION, description);
        long rowID = database.insertOrThrow(DBHelper.TABLE_RECIPE, null, cvRecipe);

        close();
        return (int) rowID;
    }

    public void saveStepsSQLite(List<StepRecipe> stepRecipe, int idRecipe) {
        this.stepRecipe = stepRecipe;
        this.idRecipe = idRecipe;
        loadPhoto();
    }

    public void saveSteps() {
        open();

        ContentValues cvStepRecipe = new ContentValues();
        cvStepRecipe.put(ID_RECIPE, idRecipe);
        cvStepRecipe.put(NUMBER_STEP, stepRecipe.get(iterator).getNumberStep());
        cvStepRecipe.put(TEXT_STEP, stepRecipe.get(iterator).getTextStep());
        cvStepRecipe.put(PHOTO_STEP, pathPhotoStep);
        database.insertOrThrow(DBHelper.TABLE_STEP_RECIPE, null, cvStepRecipe);
        iterator = ++iterator;
        loadPhoto();
    }

    public void loadPhoto() {
        if (iterator < stepRecipe.size()) {
            Glide.with(context)
                    .load(stepRecipe.get(iterator).getPhotoUrlStep())
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(660, 480) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            pathPhotoStep = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                                    resource, Environment.getExternalStorageDirectory().getAbsolutePath(), null);
                            saveSteps();
                        }
                    });
        } else {
            close();
        }
    }

    public List<SelectedStepRecipe> readStepRecipe(int idRecipe) {
        open();
        SelectedStepRecipe sSR = new SelectedStepRecipe();
        Cursor c = database.rawQuery("SELECT * FROM step_recipe WHERE id_recipe" + " == " + idRecipe, null);

        if (c.moveToFirst()) {
            do {
                int idColIndex = c.getColumnIndex(ID_RECIPE);
                int numberStepColIndex = c.getColumnIndex(NUMBER_STEP);
                int textStepColIndex = c.getColumnIndex(TEXT_STEP);
                int photoStepColIndex = c.getColumnIndex(PHOTO_STEP);

                selectedStepRecipes.add(new SelectedStepRecipe(c.getString(numberStepColIndex), c
                        .getString(textStepColIndex), c.getString(photoStepColIndex), c.getInt(idColIndex)));
            } while (c.moveToNext());
            sSR.setNumberStep(selectedStepRecipes.get(0).getNumberStep());
            sSR.setTextStep(selectedStepRecipes.get(0).getTextStep());
            sSR.setPhotoUrlStep(selectedStepRecipes.get(0).getPhotoUrlStep());

        } else {
            c.close();
            Toast.makeText(context, context.getResources().getString(R.string
                    .no_information_available), Toast.LENGTH_SHORT).show();
        }
        return selectedStepRecipes;
    }
}
