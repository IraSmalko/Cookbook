package com.exemple.android.cookbook;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.exemple.android.cookbook.supporting.StepRecipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StepRecipeActivity extends AppCompatActivity {

    private String RECIPE = "recipe";
    private String PHOTO_URL = "photo";
    private String DESCRIPTION = "description";

    private Intent intent;
    private DatabaseReference databaseReference;
    private List<StepRecipe> stepRecipe = new ArrayList<>();
    private TextView txtStepRecipe;
    private ImageView imgStepRecipe;
    private ActionBar actionBar;
    private int index = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_recipe_activity);

        txtStepRecipe = (TextView) findViewById(R.id.txt_step_recipe);
        imgStepRecipe = (ImageView) findViewById(R.id.img_step_recipe);
        actionBar = getSupportActionBar();

        intent = getIntent();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(intent.getStringExtra(RECIPE));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    StepRecipe step = postSnapshot.getValue(StepRecipe.class);

                    stepRecipe.add(step);
                }
                updateData(index);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


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
        if (i < stepRecipe.size()) {
            actionBar.setTitle(stepRecipe.get(i).getNumberStep());
            txtStepRecipe.setText(stepRecipe.get(i).getTextStep());
            Glide.with(getApplicationContext()).load(stepRecipe.get(i).getPhotoUrlStep()).into(imgStepRecipe);
        } else {
            Intent intentRecipeActivity = new Intent(getApplicationContext(), RecipeActivity.class);
            intentRecipeActivity.putExtra(RECIPE, intent.getStringExtra(RECIPE));
            intentRecipeActivity.putExtra(PHOTO_URL, intent.getStringExtra(PHOTO_URL));
            intentRecipeActivity.putExtra(DESCRIPTION, intent.getStringExtra(DESCRIPTION));
            startActivity(new Intent(intentRecipeActivity));
        }
    }
}