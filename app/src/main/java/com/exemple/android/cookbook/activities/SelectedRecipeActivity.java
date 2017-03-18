package com.exemple.android.cookbook.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.adapters.ShoppingAdapters.IngredientsAdapter;
import com.exemple.android.cookbook.entity.Ingredient;
import com.exemple.android.cookbook.helpers.DataSourceSQLite;
import com.exemple.android.cookbook.helpers.IntentHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;

import java.io.IOException;
import java.util.List;

public class SelectedRecipeActivity extends AppCompatActivity {

    private static final int INT_EXTRA = 0;
    private static final String RECIPE = "recipe";
    private static final String DESCRIPTION = "description";
    private static final String PHOTO = "photo";
    private static final String ID_RECIPE = "id_recipe";

    List<Ingredient> mRecipeIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_activity);

        TextView descriptionRecipe = (TextView) findViewById(R.id.textView);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Button btnDetailRecipe = (Button) findViewById(R.id.btnDetailRecipe);
        RecyclerView ingredientsRecyclerView = (RecyclerView) findViewById(R.id.ingredients_list);

        ActionBar actionBar = getSupportActionBar();

        final Intent intent = getIntent();

        actionBar.setTitle(intent.getStringExtra(RECIPE));
        descriptionRecipe.setText(intent.getStringExtra(DESCRIPTION));

        try {
            imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), Uri
                    .parse(intent.getStringExtra(PHOTO))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnDetailRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentHelper.intentSelectedStepRecipeActivity(getApplicationContext(), intent
                        .getStringExtra(RECIPE), intent.getStringExtra(PHOTO), intent
                        .getStringExtra(DESCRIPTION), intent.getIntExtra(ID_RECIPE, INT_EXTRA));
            }
        });


        DataSourceSQLite dataSource = new DataSourceSQLite(this);
        mRecipeIngredients = dataSource.readRecipeIngredients(intent.getIntExtra(ID_RECIPE, INT_EXTRA));

        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ingredientsRecyclerView.setAdapter(new IngredientsAdapter(mRecipeIngredients));


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SelectedRecipeListActivity.class));
    }
}
