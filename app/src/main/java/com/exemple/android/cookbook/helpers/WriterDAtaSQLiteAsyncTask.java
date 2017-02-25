package com.exemple.android.cookbook.helpers;


import android.content.Context;

import com.exemple.android.cookbook.entity.ForWriterStepsRecipe;
import com.exemple.android.cookbook.entity.Recipe;

import io.fabric.sdk.android.services.concurrency.AsyncTask;

public class WriterDAtaSQLiteAsyncTask {

    public static class WriterRecipe extends AsyncTask<Recipe, Integer, Integer> {

        private Context context;
        private OnWriterSQLite onWriterSQLite;

        public WriterRecipe(Context context, OnWriterSQLite onWriterSQLite) {
            this.context = context;
            this.onWriterSQLite = onWriterSQLite;
        }

        @Override
        protected Integer doInBackground(Recipe... recipes) {
            DataSourceSQLite dataSourceSQLite = new DataSourceSQLite(context);
            return dataSourceSQLite.saveRecipe(recipes[0].getName(), recipes[0]
                    .getPhotoUrl(), recipes[0].getDescription());
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            onWriterSQLite.onDataReady(integer);
        }

        public interface OnWriterSQLite {
            void onDataReady(Integer integer);
        }
    }

    public static class WriterStepsRecipe extends AsyncTask<ForWriterStepsRecipe, Void, ForWriterStepsRecipe> {

        private Context context;

        public WriterStepsRecipe(Context context) {
            this.context = context;
        }

        @Override
        protected ForWriterStepsRecipe doInBackground(ForWriterStepsRecipe... forWriterStepsRecipes) {
            DataSourceSQLite dataSourceSQLite = new DataSourceSQLite(context);
            return dataSourceSQLite.saveSteps(forWriterStepsRecipes[0]);
        }

        @Override
        protected void onPostExecute(ForWriterStepsRecipe forWriterStepsRecipes) {
            super.onPostExecute(forWriterStepsRecipes);
            new DataSourceSQLite(context).loadPhoto(forWriterStepsRecipes);
        }
    }
}
