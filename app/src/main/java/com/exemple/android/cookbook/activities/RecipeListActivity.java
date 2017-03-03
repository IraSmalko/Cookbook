package com.exemple.android.cookbook.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class RecipeListActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener {

    private static final String RECIPE_LIST = "recipeList";

    private List<Recipe> mRecipesList = new ArrayList<>();
    private RecipeRecyclerListAdapter mRecipeRecyclerAdapter;
    private SwipeHelper mSwipeHelper;
    private Intent mIntent;
    private RecyclerView mRecyclerView;
    private Context mContext = RecipeListActivity.this;
    private FirebaseDatabase mFirebaseDatabase;
    private String mReference;
    private String mUsername;

    private FirebaseUser mFirebaseUser;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_list_activity);
        mIntent = getIntent();
        final String recipeCategory = mIntent.getStringExtra(RECIPE_LIST);

        Log.d("LOG", "InListCreate");
        Log.d("LOG", recipeCategory);

        mFab = (FloatingActionButton) findViewById(R.id.fab1);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();
//        userRefresh();

        mFab.setOnClickListener(new View.OnClickListener() {
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

        if (mFirebaseUser != null) {
            mUsername = mFirebaseUser.getDisplayName();
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
                if (mUsername != null) {
                    new FirebaseHelper(new FirebaseHelper.OnUserRecipes() {
                        @Override
                        public void OnGet(List<Recipe> recipes) {
                            if (recipes.isEmpty()) {
                                mRecipeRecyclerAdapter = new CreaterRecyclerAdapter(getApplicationContext())
                                        .createRecyclerAdapter(mRecipesList, mIntent.getStringExtra(RECIPE_LIST), mUsername);
                                mRecyclerView.setAdapter(mRecipeRecyclerAdapter);
                            } else {
                                mRecipesList = recipes;
                                mRecipeRecyclerAdapter = new CreaterRecyclerAdapter(getApplicationContext())
                                        .createRecyclerAdapter(recipes, mIntent.getStringExtra(RECIPE_LIST), mUsername);
                                mRecyclerView.setAdapter(mRecipeRecyclerAdapter);
                                mSwipeHelper.attachSwipeRecipe();
                            }
                        }
                    }).getUserRecipe(mRecipesList, mFirebaseDatabase, mReference);
                } else {
                    mRecipeRecyclerAdapter = new CreaterRecyclerAdapter(getApplicationContext())
                            .createRecyclerAdapter(mRecipesList, mIntent.getStringExtra(RECIPE_LIST), mUsername);
                    mRecyclerView.setAdapter(mRecipeRecyclerAdapter);
                    mSwipeHelper.attachSwipeRecipe();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
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

//    public void userRefresh() {
//        if (mFirebaseUser == null) {
//            mFab.setVisibility(View.GONE);
//        }
//    }
}
