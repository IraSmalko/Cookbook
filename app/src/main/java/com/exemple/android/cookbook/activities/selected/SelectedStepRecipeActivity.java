package com.exemple.android.cookbook.activities.selected;


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

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.realm.RealmRecipe;
import com.exemple.android.cookbook.entity.realm.RealmStepRecipe;
import com.exemple.android.cookbook.helpers.IntentHelper;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class SelectedStepRecipeActivity extends AppCompatActivity {

    private static final int INT_EXTRA = 0;
    private static final String RECIPE = "recipe";
    private static final String PHOTO = "photo";
    private static final String DESCRIPTION = "description";
    private static final String ID_RECIPE = "id_recipe";

    private List<RealmStepRecipe> mSelectedStepRecipes  = new ArrayList<>();
    private int mIndex = 0;
    private ActionBar mActionBar;
    private TextView mTxtStepRecipe;
    private ImageView mImgStepRecipe;
    private Intent mIntent;
    private Context mContext = SelectedStepRecipeActivity.this;

    private Realm mRealm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_recipe_activity);

        mTxtStepRecipe = (TextView) findViewById(R.id.txtStepRecipe);
        mImgStepRecipe = (ImageView) findViewById(R.id.imgStepRecipe);
        mActionBar = getSupportActionBar();

        mIntent = getIntent();

        mRealm = Realm.getDefaultInstance();

        mSelectedStepRecipes = mRealm.where(RealmRecipe.class)
                .equalTo("recipeName",mIntent.getStringExtra(RECIPE))
                .findAll().get(0).getRecipeSteps();

        if (mSelectedStepRecipes.isEmpty()) {
            Toast.makeText(mContext, getResources().getString(R.string
                    .no_information_available), Toast.LENGTH_SHORT).show();
        } else {
            mActionBar.setTitle(mSelectedStepRecipes.get(0).getStepNumber());
            mTxtStepRecipe.setText(mSelectedStepRecipes.get(0).getStepText());
            mImgStepRecipe.setImageBitmap(mSelectedStepRecipes.get(0).getStepPhoto());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_step);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIndex = ++mIndex;
                updateData(mIndex);
            }
        });
    }

    public void updateData(int i) {
        if (i < mSelectedStepRecipes.size()) {
            mActionBar.setTitle(mSelectedStepRecipes.get(i).getStepNumber());
            mTxtStepRecipe.setText(mSelectedStepRecipes.get(i).getStepText());
            mImgStepRecipe.setImageBitmap(mSelectedStepRecipes.get(i).getStepPhoto());
        } else {
            IntentHelper.intentSelectedRecipeActivity(mContext, mIntent.getStringExtra(RECIPE), mIntent
                    .getStringExtra(PHOTO), mIntent.getStringExtra(DESCRIPTION), mIntent
                    .getIntExtra(ID_RECIPE, INT_EXTRA));
        }
    }
}
