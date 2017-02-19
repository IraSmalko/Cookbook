package com.exemple.android.cookbook.activities;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.adapters.SelectedRecipeRecyclerListAdapter;
import com.exemple.android.cookbook.entity.SelectedRecipe;
import com.exemple.android.cookbook.supporting.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class SelectedRecipeListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_list_activity);

        List<SelectedRecipe> recipesList = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("recipe", null, null, null, null, null, null);

        if (c.moveToFirst()) {
            do {
                int idColIndex = c.getColumnIndex("id");
                int recipeColIndex = c.getColumnIndex("recipe");
                int photoColIndex = c.getColumnIndex("photo");
                int descriptionColIndex = c.getColumnIndex("description");

                recipesList.add(new SelectedRecipe(c.getString(recipeColIndex), c.getString(photoColIndex), c.getString(descriptionColIndex), c.getInt(idColIndex)));
            } while (c.moveToNext());
        } else {
            c.close();
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recipeListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SelectedRecipeRecyclerListAdapter recipeRecyclerAdapter =
                new SelectedRecipeRecyclerListAdapter(this, recipesList, new SelectedRecipeRecyclerListAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(SelectedRecipe item) {
                        Intent intent = new Intent(getApplicationContext(), SelectedRecipeActivity.class);
                        intent.putExtra("recipe", item.getName());
                        intent.putExtra("photo", item.getPhotoUrl());
                        intent.putExtra("description", item.getDescription());
                        intent.putExtra("id_recipe", item.getIdRecipe());
                        startActivity(intent);
                    }
                });
        recyclerView.setAdapter(recipeRecyclerAdapter);
    }

}
