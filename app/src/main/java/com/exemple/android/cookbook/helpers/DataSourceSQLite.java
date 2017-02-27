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

    private SQLiteDatabase mDatabase;
    private DBHelper mDBHelper;
    private int mIterator = 0;
    private Context mContext;
    private List<SelectedStepRecipe> mSelectedStepRecipes = new ArrayList<>();
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

    public int saveRecipe(String recipe, String path, String description) {
        open();
        ContentValues cvRecipe = new ContentValues();

        cvRecipe.put(RECIPE, recipe);
        cvRecipe.put(PHOTO, path);
        cvRecipe.put(DESCRIPTION, description);
        long rowID = mDatabase.insertOrThrow(DBHelper.TABLE_RECIPE, null, cvRecipe);

        close();
        return (int) rowID;
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
                            bitmap, Environment.getExternalStorageDirectory().getAbsolutePath(), null);
                    mWriterStepsRecipe.setPathPhotoStep(mPathPhotoStep);
                    new WriterDAtaSQLiteAsyncTask.WriterStepsRecipe(mContext)
                            .execute(mWriterStepsRecipe);
                }
            }).execute(mWriterStepsRecipe.getStepRecipes().get(mIterator).getPhotoUrlStep());
        } else {
            close();
        }
    }

    public List<SelectedStepRecipe> readStepRecipe(int idRecipe) {
        open();
        SelectedStepRecipe sSR = new SelectedStepRecipe();
        Cursor c = mDatabase.rawQuery("SELECT * FROM step_recipe WHERE id_recipe" + " == " + idRecipe, null);

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
}
