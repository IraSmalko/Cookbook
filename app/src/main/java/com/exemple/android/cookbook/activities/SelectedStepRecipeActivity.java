package com.exemple.android.cookbook.activities;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exemple.android.cookbook.helpers.DataSourceSQLite;
import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.SelectedStepRecipe;
import com.exemple.android.cookbook.helpers.IntentHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectedStepRecipeActivity extends AppCompatActivity {

    private static final int INT_EXTRA = 0;
    private static final String RECIPE = "recipe";
    private static final String PHOTO = "photo";
    private static final String DESCRIPTION = "description";
    private static final String ID_RECIPE = "id_recipe";

    private List<SelectedStepRecipe> selectedStepRecipes = new ArrayList<>();
    private int index = 0;
    private ActionBar actionBar;
    private TextView txtStepRecipe;
    private ImageView imgStepRecipe;
    private Intent intent;
    private Context context = SelectedStepRecipeActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_recipe_activity);

        txtStepRecipe = (TextView) findViewById(R.id.txtStepRecipe);
        imgStepRecipe = (ImageView) findViewById(R.id.imgStepRecipe);
        actionBar = getSupportActionBar();

        intent = getIntent();
        DataSourceSQLite dataSource = new DataSourceSQLite(this);
        selectedStepRecipes = dataSource.readStepRecipe(intent.getIntExtra(ID_RECIPE, INT_EXTRA));

        if (selectedStepRecipes.isEmpty()) {
            Toast.makeText(context, getResources().getString(R.string
                    .no_information_available), Toast.LENGTH_SHORT).show();
        } else {
            actionBar.setTitle(selectedStepRecipes.get(0).getNumberStep());
            txtStepRecipe.setText(selectedStepRecipes.get(0).getTextStep());
            try {
                imgStepRecipe.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), Uri
                        .parse(selectedStepRecipes.get(0).getPhotoUrlStep())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_step);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index = ++index;
                updateData(index);
            }
        });
    }

    public void updateData(int i) {
        if (i < selectedStepRecipes.size()) {
            actionBar.setTitle(selectedStepRecipes.get(i).getNumberStep());
            txtStepRecipe.setText(selectedStepRecipes.get(i).getTextStep());
            try {
                imgStepRecipe.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), Uri
                        .parse(selectedStepRecipes.get(i).getPhotoUrlStep())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            IntentHelper.intentSelectedRecipeActivity(context, intent.getStringExtra(RECIPE), intent
                    .getStringExtra(PHOTO), intent.getStringExtra(DESCRIPTION), intent
                    .getIntExtra(ID_RECIPE, INT_EXTRA));
        }
    }
}
