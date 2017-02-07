package com.exemple.android.cookbook;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.exemple.android.cookbook.adapters.RecipeRecyclerListAdapter;
import com.exemple.android.cookbook.supporting.DBHelper;
import com.exemple.android.cookbook.supporting.OnItemClickListenerRecipes;
import com.exemple.android.cookbook.supporting.Recipes;

import java.util.ArrayList;
import java.util.List;

public class SelectedListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_list_activity);

        List<Recipes> recipesList = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("recipeActivityTable", null, null, null, null, null, null);

        if (c.moveToFirst()) {
            do {
                int idColIndex = c.getColumnIndex("id");
                int recipeColIndex = c.getColumnIndex("recipe");
                int photoColIndex = c.getColumnIndex("photo");
                int descriptionColIndex = c.getColumnIndex("description");

                recipesList.add(new Recipes(c.getString(recipeColIndex), c.getString(photoColIndex), c.getString(descriptionColIndex)));
            } while (c.moveToNext());
        } else {
            c.close();
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recipeListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecipeRecyclerListAdapter recipeRecyclerAdapter = new RecipeRecyclerListAdapter(this, recipesList);
        recyclerView.setAdapter(recipeRecyclerAdapter);

        recipeRecyclerAdapter.setOnItemClickListener(new OnItemClickListenerRecipes() {
            @Override
            public void onItemClick(Recipes recipes) {
                Intent intent = new Intent(getApplicationContext(), SelectedActivity.class);
                intent.putExtra("recipe", recipes.getName());
                intent.putExtra("photo", recipes.getPhotoUrl());
                intent.putExtra("description", recipes.getDescription());
                startActivity(intent);
            }
        });
    }

}
