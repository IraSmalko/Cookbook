package com.exemple.android.cookbook.helpers;


import android.content.Context;

import com.exemple.android.cookbook.entity.ForWriterStepsRecipe;
import com.exemple.android.cookbook.entity.Recipe;
import com.exemple.android.cookbook.entity.RecipeForSQLite;

import io.fabric.sdk.android.services.concurrency.AsyncTask;

public class WriterDAtaSQLiteAsyncTask {

    public static class WriterRecipe extends AsyncTask<RecipeForSQLite, Integer, Integer> {

        private Context mContext;
        private OnWriterSQLite mOnWriterSQLite;

        public WriterRecipe(Context context, OnWriterSQLite onWriterSQLite) {
            mContext = context;
            mOnWriterSQLite = onWriterSQLite;
        }

        @Override
        protected Integer doInBackground(RecipeForSQLite... recipes) {
            DataSourceSQLite dataSourceSQLite = new DataSourceSQLite(mContext);
            int  rowID = dataSourceSQLite.saveRecipe(recipes[0]);
            dataSourceSQLite.saveIngredient(recipes[0].getIngredients(), rowID);
            return rowID;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mOnWriterSQLite.onDataReady(integer);
        }

        public interface OnWriterSQLite {
            void onDataReady(Integer integer);
        }
    }

    public static class WriterStepsRecipe extends AsyncTask<ForWriterStepsRecipe, Void, ForWriterStepsRecipe> {

        private Context mContext;

        public WriterStepsRecipe(Context context) {
            mContext = context;
        }

        @Override
        protected ForWriterStepsRecipe doInBackground(ForWriterStepsRecipe... forWriterStepsRecipes) {
            return new DataSourceSQLite(mContext).saveSteps(forWriterStepsRecipes[0]);
        }

        @Override
        protected void onPostExecute(ForWriterStepsRecipe forWriterStepsRecipes) {
            super.onPostExecute(forWriterStepsRecipes);
            new DataSourceSQLite(mContext).loadPhoto(forWriterStepsRecipes);
        }
    }
}
