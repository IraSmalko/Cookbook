package com.exemple.android.cookbook;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class RecipeListActivity extends AppCompatActivity {

    private List<CategoryRecipes> categoryRecipesList = new ArrayList<>();
    private String RECIPE_LIST = "recipeList";
    private String RECIPE = "recipe";
    private String PHOTO_URL = "photo";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_list_activity);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddRecipeActivity.class));
            }
        });

        Intent intent = getIntent();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(intent.getStringExtra(RECIPE_LIST));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        MyAdapter myAdapter = new MyAdapter(this, categoryRecipesList);
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

}
