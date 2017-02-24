package com.exemple.android.cookbook.helpers;


import android.content.Context;

import com.exemple.android.cookbook.adapters.CategoryRecipeRecyclerAdapter;
import com.exemple.android.cookbook.adapters.RecipeRecyclerListAdapter;
import com.exemple.android.cookbook.entity.CategoryRecipes;
import com.exemple.android.cookbook.entity.Recipe;

import java.util.List;

public class CreaterRecyclerAdapter {

    private Context context;
    private String recipes;
    private String user;

    public CreaterRecyclerAdapter(Context context) {
        this.context = context;
    }

    public RecipeRecyclerListAdapter createRecyclerAdapter(List<Recipe> recipesList,
                                                           String recipesListIntent, String username) {
        user = username;
        recipes = recipesListIntent;
        return new RecipeRecyclerListAdapter(context, recipesList,
                new RecipeRecyclerListAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(Recipe item) {
                        IntentHelper.intentRecipeActivity(context, item.getName(), item
                                .getPhotoUrl(), item.getDescription(), recipes, user);
                    }
                });
    }

    public CategoryRecipeRecyclerAdapter createRecyclerAdapter(List<CategoryRecipes> categoryRecipesList){
        return new CategoryRecipeRecyclerAdapter(context, categoryRecipesList,
                new CategoryRecipeRecyclerAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(CategoryRecipes item) {
                        IntentHelper.intentRecipeListActivity(context, item.getName());
                    }
                });
    }
}
