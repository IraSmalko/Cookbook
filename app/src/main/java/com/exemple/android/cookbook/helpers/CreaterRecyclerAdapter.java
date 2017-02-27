package com.exemple.android.cookbook.helpers;


import android.content.Context;

import com.exemple.android.cookbook.adapters.CategoryRecipeRecyclerAdapter;
import com.exemple.android.cookbook.adapters.RecipeRecyclerListAdapter;
import com.exemple.android.cookbook.entity.CategoryRecipes;
import com.exemple.android.cookbook.entity.Recipe;

import java.util.List;

public class CreaterRecyclerAdapter {

    private Context mContext;
    private String mRecipes;
    private String mUser;

    public CreaterRecyclerAdapter(Context context) {
        mContext = context;
    }

    public RecipeRecyclerListAdapter createRecyclerAdapter(List<Recipe> recipesList,
                                                           String recipesListIntent, String username) {
        mUser = username;
        mRecipes = recipesListIntent;
        return new RecipeRecyclerListAdapter(mContext, recipesList,
                new RecipeRecyclerListAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(Recipe item) {
                        IntentHelper.intentRecipeActivity(mContext, item.getName(), item
                                .getPhotoUrl(), item.getDescription(), mRecipes, mUser);
                    }
                });
    }

    public CategoryRecipeRecyclerAdapter createRecyclerAdapter(List<CategoryRecipes> categoryRecipesList) {
        return new CategoryRecipeRecyclerAdapter(mContext, categoryRecipesList,
                new CategoryRecipeRecyclerAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(CategoryRecipes item) {
                        IntentHelper.intentRecipeListActivity(mContext, item.getName());
                    }
                });
    }
}
