package com.exemple.android.cookbook;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {

    private List<CategoryRecipes> categoryRecipesList = new ArrayList<>();
    private String RECIPE = "recipe";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_activity);

        Intent intent = getIntent();
        String a = intent.getStringExtra(RECIPE);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(a);

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
                startActivity(intent);
            }
        });
    }

}
