package com.exemple.android.cookbook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.adapters.CategoryRecipeRecyclerAdapter;
import com.exemple.android.cookbook.entity.CategoryRecipes;
import com.exemple.android.cookbook.helpers.CreaterRecyclerAdapter;
import com.exemple.android.cookbook.helpers.FirebaseHelper;
import com.exemple.android.cookbook.helpers.SwipeHelper;
import com.exemple.android.cookbook.helpers.VoiceRecognitionHelper;
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

    public static final String ANONYMOUS = "anonymous";
    private static final int VOICE_REQUEST_CODE = 1234;

    private FirebaseDatabase mFirebaseDatabase;
    private String mUsername;
    private FirebaseUser mFirebaseUser;

    private RecyclerView mRecyclerView;
    private SwipeHelper mSwipeHelper;
    private CategoryRecipeRecyclerAdapter mRecyclerAdapter;
    private List<CategoryRecipes> mCategoryRecipesList = new ArrayList<>();
    private List<CategoryRecipes> mPublicCategoryRecipes = new ArrayList<>();
    private List<CategoryRecipes> mForVoice = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        if (mFirebaseUser == null) {
            mUsername = ANONYMOUS;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = mFirebaseDatabase.getReference("Сategory_Recipes");

        mRecyclerView = (RecyclerView) findViewById(R.id.recipeListRecyclerView);
        mSwipeHelper = new SwipeHelper(mRecyclerView, getApplicationContext());

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
                            mForVoice = category;
                            mRecyclerAdapter = new CreaterRecyclerAdapter(getApplicationContext())
                                    .createRecyclerAdapter(category);
                            mRecyclerView.setAdapter(mRecyclerAdapter);
                            mSwipeHelper.attachSwipeCategory(mPublicCategoryRecipes);
                        }
                    }).getUserCategoryRecipe(mUsername, mFirebaseDatabase);
                } else {
                    mRecyclerAdapter = new CreaterRecyclerAdapter(getApplicationContext())
                            .createRecyclerAdapter(mCategoryRecipesList);
                    mRecyclerView.setAdapter(mRecyclerAdapter);
                    mSwipeHelper.attachSwipeCategory(mCategoryRecipesList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<CategoryRecipes> newList = new ArrayList<>();

        for (CategoryRecipes categoryRecipes : mCategoryRecipesList) {
            String name = categoryRecipes.getName().toLowerCase();
            if (name.contains(newText))
                newList.add(categoryRecipes);
        }
        mRecyclerAdapter.updateAdapter(newList);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_REQUEST_CODE) {
            mForVoice = null != mForVoice ? mForVoice : mCategoryRecipesList;
            new VoiceRecognitionHelper(getApplicationContext()).onActivityResult(resultCode, data, mForVoice);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
