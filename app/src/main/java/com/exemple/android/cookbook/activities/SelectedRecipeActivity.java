package com.exemple.android.cookbook.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.adapters.IngredientsAdapter;
import com.exemple.android.cookbook.entity.Ingredient;
import com.exemple.android.cookbook.helpers.DataSourceSQLite;
import com.exemple.android.cookbook.helpers.IntentHelper;
import com.exemple.android.cookbook.helpers.SwipeHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectedRecipeActivity extends AppCompatActivity {

    private static final int INT_EXTRA = 0;
    private static final String RECIPE = "recipe";
    private static final String DESCRIPTION = "description";
    private static final String PHOTO = "photo";
    private static final String ID_RECIPE = "id_recipe";

    private List<Ingredient> mRecipeIngredients = new ArrayList<>();
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_activity);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Button btnDetailRecipe = (Button) findViewById(R.id.btnDetailRecipe);
        ActionBar actionBar = getSupportActionBar();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerIngredients);

        mIntent = getIntent();

        actionBar.setTitle(mIntent.getStringExtra(RECIPE));

        try {
            imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(),
                    Uri.parse(mIntent.getStringExtra(PHOTO))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnDetailRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentHelper.intentSelectedStepRecipeActivity(getApplicationContext(), mIntent
                        .getStringExtra(RECIPE), mIntent.getStringExtra(PHOTO), mIntent.getIntExtra(ID_RECIPE, INT_EXTRA));
            }
        });

        DataSourceSQLite dataSource = new DataSourceSQLite(this);
        mRecipeIngredients = dataSource.readRecipeIngredients(mIntent.getIntExtra(ID_RECIPE, INT_EXTRA));
        IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(getApplicationContext(), mRecipeIngredients);
        recyclerView.setAdapter(ingredientsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_recipe_selected, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit_recipe) {
            Intent intent = mIntent;
            intent.setClass(this, EditRecipeActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SelectedRecipeListActivity.class));
    }
}
