package com.exemple.android.cookbook;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectedStepRecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_recipe_activity);

        TextView txtStepRecipe = (TextView) findViewById(R.id.txt_step_recipe);
        ImageView imgStepRecipe = (ImageView) findViewById(R.id.img_step_recipe);
        ActionBar actionBar = getSupportActionBar();
    }
}
