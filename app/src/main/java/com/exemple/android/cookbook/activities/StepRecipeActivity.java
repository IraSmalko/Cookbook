package com.exemple.android.cookbook.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.StepRecipe;
import com.exemple.android.cookbook.helpers.FirebaseHelper;
import com.exemple.android.cookbook.helpers.IntentHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StepRecipeActivity extends AppCompatActivity {

    private static final String RECIPE_LIST = "recipeList";
    private static final String RECIPE = "recipe";
    private static final String PHOTO = "photo";
    private static final String DESCRIPTION = "description";

    private Intent intent;
    private List<StepRecipe> stepRecipe = new ArrayList<>();
    private TextView txtStepRecipe;
    private ImageView imgStepRecipe;
    private ActionBar actionBar;
    private Context context = StepRecipeActivity.this;
    private int index = 0;
    private String reference;
    private String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_recipe_activity);

        txtStepRecipe = (TextView) findViewById(R.id.txtStepRecipe);
        imgStepRecipe = (ImageView) findViewById(R.id.imgStepRecipe);
        actionBar = getSupportActionBar();

        intent = getIntent();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        reference = "Step_recipe/" + intent.getStringExtra(RECIPE_LIST) + "/" + intent.getStringExtra(RECIPE);
        DatabaseReference databaseReference = firebaseDatabase.getReference().child(reference);

        if (firebaseUser != null) {
            username = firebaseUser.getDisplayName();
            reference = username + "/" + reference;
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    StepRecipe step = postSnapshot.getValue(StepRecipe.class);
                    stepRecipe.add(step);
                }
                if (username != null) {
                    new FirebaseHelper(new FirebaseHelper.OnStepRecipes() {
                        @Override
                        public void OnGet(List<StepRecipe> stepRecipes) {
                            if (stepRecipe.size() != 0) {
                                updateData(index);
                            } else {
                                Toast.makeText(context, getResources().getString(R
                                        .string.no_information_available), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).getStepsRecipe(stepRecipe, reference);
                } else if (stepRecipe.size() != 0) {
                    updateData(index);
                } else {
                    Toast.makeText(context, getResources().getString(R
                            .string.no_information_available), Toast.LENGTH_SHORT).show();
                }
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
            Glide.with(context).load(stepRecipe.get(i).getPhotoUrlStep()).into(imgStepRecipe);
        } else {
            IntentHelper.intentRecipeActivity(context, intent.getStringExtra(RECIPE), intent
                    .getStringExtra(PHOTO), intent.getStringExtra(DESCRIPTION), intent.getStringExtra(RECIPE_LIST), null);
        }
    }
}
