package com.exemple.android.cookbook.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.adapters.RecipeRecyclerListAdapter;
import com.exemple.android.cookbook.entity.Recipe;
import com.exemple.android.cookbook.helpers.FirebaseHelper;
import com.exemple.android.cookbook.helpers.IntentHelper;
import com.exemple.android.cookbook.helpers.SwipeHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class RecipeListActivity extends BaseActivity {

    private static final String RECIPE_LIST = "recipeList";
    private List<Recipe> mRecipesList = new ArrayList<>();
    private RecipeRecyclerListAdapter mRecipeRecyclerAdapter;
    private Intent mIntent;
    private String mUsername;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recipeListRecyclerView);
        SwipeHelper swipeHelper = new SwipeHelper(recyclerView, getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mIntent = getIntent();
        final String recipeCategory = mIntent.getStringExtra(RECIPE_LIST);

        Log.d("LOG", "InListCreate");
        Log.d("LOG", recipeCategory);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> nameRecipesList = new ArrayList<>();
                for (int i = 0; i < mRecipesList.size(); i++) {
                    nameRecipesList.add(mRecipesList.get(i).getName());
                }
                IntentHelper.intentAddRecipeActivity(RecipeListActivity.this, nameRecipesList, mIntent
                        .getStringExtra(RECIPE_LIST));
            }
        });

        String reference = "Recipe_lists/" + mIntent.getStringExtra(RECIPE_LIST);
        if (firebaseUser != null) {
            mUsername = firebaseUser.getDisplayName();
        }

        new FirebaseHelper(new FirebaseHelper.OnGetRecipeList() {
            @Override
            public void OnGet(RecipeRecyclerListAdapter recyclerListAdapter, List<Recipe> recipesList) {
                mRecipeRecyclerAdapter = recyclerListAdapter;
                mRecipesList = recipesList;
            }
        }).getRecipeList(reference, getApplicationContext(), mUsername,
                recipeCategory, recyclerView, swipeHelper);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<Recipe> newList = new ArrayList<>();

        for (Recipe recipes : mRecipesList) {
            String name = recipes.getName().toLowerCase();
            if (name.contains(newText))
                newList.add(recipes);
        }
        mRecipeRecyclerAdapter.updateAdapter(newList);
        return true;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.recipe_list_activity;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
