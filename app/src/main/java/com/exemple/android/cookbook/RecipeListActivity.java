package com.exemple.android.cookbook;


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

import com.exemple.android.cookbook.supporting.CategoryRecipes;
import com.exemple.android.cookbook.supporting.OnItemClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecipeListActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener {

    private String RECIPE_LIST = "recipeList";
    private String RECIPE = "recipe";
    private String PHOTO_URL = "photo";

    private List<CategoryRecipes> categoryRecipesList = new ArrayList<>();
    MyAdapter myAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_list_activity);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddRecipeActivity.class));
            }
        });

        Intent intent = getIntent();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(intent.getStringExtra(RECIPE_LIST));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recipeListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        myAdapter = new MyAdapter(this, categoryRecipesList);
        recyclerView.setAdapter(myAdapter);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    CategoryRecipes categoryRecipes = postSnapshot.getValue(CategoryRecipes.class);

                    categoryRecipesList.add(categoryRecipes);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        myAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(CategoryRecipes categoryRecipes) {
                Intent intent = new Intent(getApplicationContext(), RecipeActivity.class);
                intent.putExtra(RECIPE, categoryRecipes.getName());
                intent.putExtra(PHOTO_URL, categoryRecipes.photoUrl);
                startActivity(intent);
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
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<CategoryRecipes> newList = new ArrayList<>();

        for (CategoryRecipes categoryRecipes : categoryRecipesList) {
            String name = categoryRecipes.getName().toLowerCase();
            if (name.contains(newText))
                newList.add(categoryRecipes);
        }
        myAdapter.setFilter(newList);
        return true;
    }

}
