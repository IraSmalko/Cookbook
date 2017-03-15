package com.exemple.android.cookbook.activities;


import android.content.Context;
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
import com.exemple.android.cookbook.helpers.CreaterRecyclerAdapter;
import com.exemple.android.cookbook.helpers.FirebaseHelper;
import com.exemple.android.cookbook.helpers.IntentHelper;
import com.exemple.android.cookbook.helpers.SwipeHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecipeListActivity extends BaseActivity {

    private static final String RECIPE_LIST = "recipeList";

    private List<Recipe> mRecipesList = new ArrayList<>();
    private List<Recipe> mPublicListRecipes = new ArrayList<>();
    private RecipeRecyclerListAdapter mRecipeRecyclerAdapter;
    private SwipeHelper mSwipeHelper;
    private Intent mIntent;
    private RecyclerView mRecyclerView;
    private Context mContext = RecipeListActivity.this;
    private FirebaseDatabase mFirebaseDatabase;
    private String mReference;
    private String mUsername;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> nameRecipesList = new ArrayList<>();
                for (int i = 0; i < mRecipesList.size(); i++) {
                    nameRecipesList.add(mRecipesList.get(i).getName());
                }
                IntentHelper.intentAddRecipeActivity(mContext, nameRecipesList, mIntent
                        .getStringExtra(RECIPE_LIST));
            }
        });

        mIntent = getIntent();

        mReference = "Recipe_lists/" + mIntent.getStringExtra(RECIPE_LIST);
        DatabaseReference databaseReference = mFirebaseDatabase.getReference().child(mReference);

        if (firebaseUser != null) {
            mUsername = firebaseUser.getDisplayName();
            mReference = mUsername + "/" + mReference;
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recipeListRecyclerView);
        mSwipeHelper = new SwipeHelper(mRecyclerView, getApplicationContext());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mRecipesList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Recipe recipes = postSnapshot.getValue(Recipe.class);
                    mRecipesList.add(recipes);
                }
                mPublicListRecipes = mRecipesList;
                if (mUsername != null) {
                    new FirebaseHelper(new FirebaseHelper.OnUserRecipes() {
                        @Override
                        public void OnGet(List<Recipe> recipes) {
                            recipes.addAll(mRecipesList);
                            mRecipeRecyclerAdapter = new CreaterRecyclerAdapter(getApplicationContext())
                                    .createRecyclerAdapter(recipes, mIntent.getStringExtra(RECIPE_LIST), mUsername);
                            mRecyclerView.setAdapter(mRecipeRecyclerAdapter);
                            mSwipeHelper.attachSwipeRecipe(mPublicListRecipes);
                        }
                    }).getUserRecipe(mFirebaseDatabase, mReference);
                } else {
                    mRecipeRecyclerAdapter = new CreaterRecyclerAdapter(getApplicationContext())
                            .createRecyclerAdapter(mRecipesList, mIntent.getStringExtra(RECIPE_LIST), mUsername);
                    mRecyclerView.setAdapter(mRecipeRecyclerAdapter);
                    mSwipeHelper.attachSwipeRecipe(mPublicListRecipes);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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
}
