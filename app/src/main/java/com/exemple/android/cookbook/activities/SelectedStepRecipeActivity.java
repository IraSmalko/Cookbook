package com.exemple.android.cookbook.activities;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.SelectedStepRecipe;
import com.exemple.android.cookbook.supporting.DBHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectedStepRecipeActivity extends AppCompatActivity {

    private List<SelectedStepRecipe> selectedStepRecipes = new ArrayList<>();
    private int index = 0;
    private ActionBar actionBar;
    private TextView txtStepRecipe;
    private ImageView imgStepRecipe;
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_recipe_activity);

        txtStepRecipe = (TextView) findViewById(R.id.txt_step_recipe);
        imgStepRecipe = (ImageView) findViewById(R.id.img_step_recipe);
        actionBar = getSupportActionBar();

        intent = getIntent();
       int bvb = intent.getIntExtra("id_recipe", 0);

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM step_recipe WHERE id_recipe" + " == " + intent.getIntExtra("id_recipe", 0), null);

        if (c.moveToFirst()) {
            do {
                int idColIndex = c.getColumnIndex("id_recipe");
                int numberStepColIndex = c.getColumnIndex("number_step");
                int textStepColIndex = c.getColumnIndex("text_step");
                int photoStepColIndex = c.getColumnIndex("photo_step");

                selectedStepRecipes.add(new SelectedStepRecipe(c.getString(numberStepColIndex), c.getString(textStepColIndex), c.getString(photoStepColIndex), c.getInt(idColIndex)));
            } while (c.moveToNext());
            actionBar.setTitle(selectedStepRecipes.get(0).getNumberStep());
            txtStepRecipe.setText(selectedStepRecipes.get(0).getTextStep());
            try {
                imgStepRecipe.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(selectedStepRecipes.get(0).getPhotoUrlStep())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            c.close();
            Toast.makeText(getApplicationContext(), "Інформація відсутня", Toast.LENGTH_SHORT).show();
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
                imgStepRecipe.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(selectedStepRecipes.get(i).getPhotoUrlStep())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Intent intent1 = new Intent(this, SelectedRecipeActivity.class);
            intent1.putExtra("recipe", intent.getStringExtra("recipe"));
            intent1.putExtra("photo", intent.getStringExtra("photo"));
            intent1.putExtra("description", intent.getStringExtra("description"));
            intent1.putExtra("id_recipe", intent.getIntExtra("id_recipe", 0));
            startActivity(intent1);
        }
    }
}
