package com.exemple.android.cookbook.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.adapters.RecipeRecyclerListAdapter;
import com.exemple.android.cookbook.entity.Recipe;
import com.exemple.android.cookbook.helpers.IntentHelper;
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

    private List<Recipe> recipesList = new ArrayList<>();
    private RecipeRecyclerListAdapter recipeRecyclerAdapter;
    private Intent intent;
    private RecyclerView recyclerView;
    private Context context = RecipeListActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_list_activity);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> nameRecipesList = new ArrayList<>();
                for (int i = 0; i < recipesList.size(); i++) {
                    nameRecipesList.add(recipesList.get(i).getName());
                }
                IntentHelper.intentAddRecipeActivity(context, nameRecipesList, intent
                        .getStringExtra(RECIPE_LIST));
            }
        });

        intent = getIntent();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference()
                .child("Recipe_lists/" + intent.getStringExtra(RECIPE_LIST));

        recyclerView = (RecyclerView) findViewById(R.id.recipeListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipesList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Recipe recipes = postSnapshot.getValue(Recipe.class);
                    recipesList.add(recipes);
                    recipeRecyclerAdapter = new RecipeRecyclerListAdapter(context, recipesList,
                            new RecipeRecyclerListAdapter.ItemClickListener() {
                                @Override
                                public void onItemClick(Recipe item) {
                                    IntentHelper.intentRecipeActivity(context, item.getName(), item
                                            .getPhotoUrl(), item.getDescription(), intent.getStringExtra(RECIPE_LIST));
                                }
                            });
                    recyclerView.setAdapter(recipeRecyclerAdapter);
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

        for (Recipe recipes : recipesList) {
            String name = recipes.getName().toLowerCase();
            if (name.contains(newText))
                newList.add(recipes);
        }
        recipeRecyclerAdapter.updateAdapter(newList);
        return true;
    }
}
