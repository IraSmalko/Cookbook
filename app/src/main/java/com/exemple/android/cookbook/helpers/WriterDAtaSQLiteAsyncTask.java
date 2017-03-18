package com.exemple.android.cookbook.helpers;


import android.content.Context;

import com.exemple.android.cookbook.entity.ForWriterStepsRecipe;
import com.exemple.android.cookbook.entity.Ingredient;
import com.exemple.android.cookbook.entity.Recipe;

import java.util.List;

import io.fabric.sdk.android.services.concurrency.AsyncTask;

public class WriterDAtaSQLiteAsyncTask {

    public static class WriterRecipe extends AsyncTask<Recipe, Integer, Integer> {

        private Context mContext;
        private OnWriterSQLite mOnWriterSQLite;

        public WriterRecipe(Context context, OnWriterSQLite onWriterSQLite) {
            mContext = context;
            mOnWriterSQLite = onWriterSQLite;
        }

        @Override
        protected Integer doInBackground(Recipe... recipes) {
            return new DataSourceSQLite(mContext).saveRecipe(recipes[0].getName(), recipes[0]
                    .getPhotoUrl(), recipes[0].getDescription());
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

    public static class WriterIngredients extends AsyncTask<List<Ingredient>, Void, List<Ingredient>> {

        Context mContext;
        int mIdRecipe;

        public WriterIngredients(Context context, int idRecipe) {
            mContext = context;
            mIdRecipe = idRecipe;
        }

        @Override
        protected List<Ingredient> doInBackground(List<Ingredient>... ingredients) {
            return new DataSourceSQLite(mContext).saveIngredients(ingredients[0], mIdRecipe);
        }

        @Override
        protected void onPostExecute(List<Ingredient> ingredients) {
            super.onPostExecute(ingredients);
        }

    }
}
