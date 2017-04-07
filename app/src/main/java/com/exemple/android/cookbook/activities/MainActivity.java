package com.exemple.android.cookbook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.adapters.CategoryRecipeRecyclerAdapter;
import com.exemple.android.cookbook.entity.CategoryRecipes;
import com.exemple.android.cookbook.helpers.CheckOnlineHelper;
import com.exemple.android.cookbook.helpers.CreaterRecyclerAdapter;
import com.exemple.android.cookbook.helpers.FirebaseHelper;
import com.exemple.android.cookbook.helpers.SwipeHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends BaseActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseUser mFirebaseUser;

    private RecyclerView mRecyclerView;
    private SwipeHelper mSwipeHelper;
    private TextView mTextView;
    private Button mButton;
    private CategoryRecipeRecyclerAdapter mRecyclerAdapter;
    private List<CategoryRecipes> mCategoryRecipesList = new ArrayList<>();
    private List<CategoryRecipes> mPublicCategoryRecipes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        mRecyclerView = (RecyclerView) findViewById(R.id.recipeListRecyclerView);
        mSwipeHelper = new SwipeHelper(mRecyclerView, getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTextView = (TextView) findViewById(R.id.error_loading);
        mButton = (Button) findViewById(R.id.btn_retry);
        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
        setSupportActionBar(toolbar);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerAdapter = new CreaterRecyclerAdapter(getApplicationContext())
                .createRecyclerAdapter(mCategoryRecipesList);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddCategoryRecipeActivity.class));
            }
        });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = mFirebaseDatabase.getReference("Сategory_Recipes");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    CategoryRecipes categoryRecipes = postSnapshot.getValue(CategoryRecipes.class);
                    mCategoryRecipesList.add(categoryRecipes);
                }
                mPublicCategoryRecipes = mCategoryRecipesList;
                if (mFirebaseUser != null) {
                    new FirebaseHelper(new FirebaseHelper.OnUserCategoryRecipe() {
                        @Override
                        public void OnGet(List<CategoryRecipes> category) {
                            category.addAll(mCategoryRecipesList);
                            mRecyclerAdapter.updateAdapter(category);
                            mSwipeHelper.attachSwipeCategory(mPublicCategoryRecipes);
                            if (mRecyclerAdapter.getItemCount() != 0) {
                                mTextView.setVisibility(View.INVISIBLE);
                                mButton.setVisibility(View.INVISIBLE);
                            }

                        }
                    }).getUserCategoryRecipe(mFirebaseUser.getUid(), mFirebaseDatabase);
                } else {
                    mRecyclerAdapter.updateAdapter(mCategoryRecipesList);
                    mRecyclerView.setAdapter(mRecyclerAdapter);
                    mSwipeHelper.attachSwipeCategory(mCategoryRecipesList);
                    if (mRecyclerAdapter.getItemCount() != 0) {
                        mTextView.setVisibility(View.INVISIBLE);
                        mButton.setVisibility(View.INVISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        if (!new CheckOnlineHelper(this).isOnline() && mRecyclerAdapter.getItemCount() == 0) {
            mTextView.setVisibility(View.VISIBLE);
            mButton.setVisibility(View.VISIBLE);
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mButton.setAnimation(buttonClick);
                    view.startAnimation(buttonClick);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            });
        }
    }


    @Override
    public int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<CategoryRecipes> newList = new ArrayList<>();
        mTextView.setVisibility(View.INVISIBLE);
        if (mCategoryRecipesList.isEmpty()) {
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText(getResources().getString(R.string.not_found));
        } else {
            for (CategoryRecipes categoryRecipes : mCategoryRecipesList) {
                String name = categoryRecipes.getName().toLowerCase();
                if (name.contains(newText)) {
                    newList.add(categoryRecipes);
                }
            }
            if (newList.isEmpty()) {
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText(getResources().getString(R.string.not_found));
            }
            mRecyclerAdapter.updateAdapter(newList);
        }
        return true;
    }
}
