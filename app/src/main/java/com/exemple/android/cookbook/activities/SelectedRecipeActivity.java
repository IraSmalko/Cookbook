package com.exemple.android.cookbook.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.exemple.android.cookbook.R;

import java.io.IOException;

public class SelectedRecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_activity);

        TextView descriptionRecipe = (TextView) findViewById(R.id.textView);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Button btnDetailRecipe = (Button) findViewById(R.id.btn_detail_recipe);
        ActionBar actionBar = getSupportActionBar();

        final Intent intent = getIntent();

        actionBar.setTitle(intent.getStringExtra("recipe"));
        descriptionRecipe.setText(intent.getStringExtra("description"));

        try {
            imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(intent.getStringExtra("photo"))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnDetailRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(), SelectedStepRecipeActivity.class);
                intent1.putExtra("recipe", intent.getStringExtra("recipe"));
                intent1.putExtra("photo", intent.getStringExtra("photo"));
                intent1.putExtra("description", intent.getStringExtra("description"));
                intent1.putExtra("id_recipe", intent.getIntExtra("id_recipe", 0));
                startActivity(intent1);
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SelectedRecipeListActivity.class));
    }
}
