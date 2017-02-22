package com.exemple.android.cookbook.helpers;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.ForWriterStepsRecipe;
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
    private List<SelectedStepRecipe> selectedStepRecipes = new ArrayList<>();
    private ForWriterStepsRecipe writerStepsRecipe;
    private String pathPhotoStep;

    public DataSourceSQLite(Context context) {
        dbHelper = new DBHelper(context);
        this.context = context;
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    private void close() {
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
        loadPhoto(new ForWriterStepsRecipe(stepRecipe, pathPhotoStep, idRecipe, iterator));
    }

    public ForWriterStepsRecipe saveSteps(ForWriterStepsRecipe forWriterStepsRecipe) {
        open();
        iterator = forWriterStepsRecipe.getIterator();
        ContentValues cvStepRecipe = new ContentValues();
        cvStepRecipe.put(ID_RECIPE, forWriterStepsRecipe.getIdRecipe());
        cvStepRecipe.put(NUMBER_STEP, forWriterStepsRecipe.getStepRecipes().get(iterator).getNumberStep());
        cvStepRecipe.put(TEXT_STEP, forWriterStepsRecipe.getStepRecipes().get(iterator).getTextStep());
        cvStepRecipe.put(PHOTO_STEP, forWriterStepsRecipe.getPathPhotoStep());
        database.insertOrThrow(DBHelper.TABLE_STEP_RECIPE, null, cvStepRecipe);
        forWriterStepsRecipe.setIterator(forWriterStepsRecipe.getIterator() + 1);
        close();
        return forWriterStepsRecipe;
    }

    public void loadPhoto(ForWriterStepsRecipe forWriterStepsRecipe) {
        this.writerStepsRecipe = forWriterStepsRecipe;
        if (forWriterStepsRecipe.getIterator() < forWriterStepsRecipe.getStepRecipes().size()) {
            new PhotoLoaderAsyncTask(context, new PhotoLoaderAsyncTask.PhotoLoadProcessed() {
                @Override
                public void onBitmapReady(Bitmap bitmap) {
                    pathPhotoStep = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                            bitmap, Environment.getExternalStorageDirectory().getAbsolutePath(), null);
                    writerStepsRecipe.setPathPhotoStep(pathPhotoStep);
                    new WriterDAtaSQLiteAsyncTask.WriterStepsRecipe(context)
                            .execute(writerStepsRecipe);
                }
            }).execute(writerStepsRecipe.getStepRecipes().get(iterator).getPhotoUrlStep());
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
