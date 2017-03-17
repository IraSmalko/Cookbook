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
    private static final String IS_PERSONAL = "isPersonal";
    private static final int INT_EXTRA = 0;

    private Intent mIntent;
    private List<StepRecipe> mStepRecipe = new ArrayList<>();
    private List<StepRecipe> mPersonalStepRecipe = new ArrayList<>();
    private TextView mTxtStepRecipe;
    private ImageView mImgStepRecipe;
    private ActionBar mActionBar;
    private Context mContext = StepRecipeActivity.this;
    private int mIterator = 0;
    private String mReference;
    private String mUsername;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_recipe_activity);

        mTxtStepRecipe = (TextView) findViewById(R.id.txtStepRecipe);
        mImgStepRecipe = (ImageView) findViewById(R.id.imgStepRecipe);
        mActionBar = getSupportActionBar();

        mIntent = getIntent();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        mReference = "Step_recipe/" + mIntent.getStringExtra(RECIPE_LIST) + "/" + mIntent.getStringExtra(RECIPE);
        DatabaseReference databaseReference = firebaseDatabase.getReference().child(mReference);

        if (firebaseUser != null) {
            mUsername = firebaseUser.getDisplayName();
            mReference = mUsername + "/" + mReference;
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    StepRecipe step = postSnapshot.getValue(StepRecipe.class);
                    mStepRecipe.add(step);
                }
                if (mUsername != null) {
                    new FirebaseHelper(new FirebaseHelper.OnStepRecipes() {
                        @Override
                        public void OnGet(List<StepRecipe> stepRecipes) {
                            mStepRecipe.addAll(stepRecipes);
                            updateData(mIterator);
                            if (mStepRecipe.isEmpty()) {
                                Toast.makeText(mContext, getResources().getString(R
                                        .string.no_information_available), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).getStepsRecipe(mReference);
                } else if (mStepRecipe.size() != 0) {
                    updateData(mIterator);
                } else {
                    Toast.makeText(mContext, getResources().getString(R
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
                updateData(++mIterator);
            }
        });
    }

    public void updateData(int i) {
        if (i < mStepRecipe.size()) {
            mActionBar.setTitle(mStepRecipe.get(i).getNumberStep());
            mTxtStepRecipe.setText(mStepRecipe.get(i).getTextStep());
            Glide.with(mContext).load(mStepRecipe.get(i).getPhotoUrlStep()).into(mImgStepRecipe);
        } else {
            IntentHelper.intentRecipeActivity(mContext, mIntent.getStringExtra(RECIPE), mIntent
                    .getStringExtra(PHOTO), mIntent.getStringExtra(DESCRIPTION), mIntent
                    .getIntExtra(IS_PERSONAL, INT_EXTRA), mIntent.getStringExtra(RECIPE_LIST), mUsername);
        }
    }

    @Override
    public void onBackPressed() {
        if(mIterator == 0) {
            IntentHelper.intentRecipeActivity(mContext, mIntent.getStringExtra(RECIPE), mIntent
                    .getStringExtra(PHOTO), mIntent.getStringExtra(DESCRIPTION), mIntent
                    .getIntExtra(IS_PERSONAL, INT_EXTRA), mIntent.getStringExtra(RECIPE_LIST), mUsername);
        }else {
            updateData(--mIterator);
        }
    }
}
