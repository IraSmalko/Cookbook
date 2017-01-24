package com.exemple.android.cookbook;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.exemple.android.cookbook.supporting.CategoryRecipes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddRecipeActivity extends AppCompatActivity{

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView txtDetails;
    private EditText inputName, inputPhotoUrl, inputIngredients;
    private Button btnSave;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private String recipeId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_recipe);

        txtDetails = (TextView) findViewById(R.id.txt_category_recipe);
        inputName = (EditText) findViewById(R.id.name);
        inputPhotoUrl = (EditText) findViewById(R.id.photoUrl);
        inputIngredients = (EditText) findViewById(R.id.ingredients);
        btnSave = (Button) findViewById(R.id.btn_save);

        mFirebaseInstance = FirebaseDatabase.getInstance();

        mFirebaseDatabase = mFirebaseInstance.getReference("Сategory_Recipes");

        mFirebaseInstance.getReference("app_title").setValue("Cookbook");

        mFirebaseInstance.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "App title updated");

                String appTitle = dataSnapshot.getValue(String.class);

                getSupportActionBar().setTitle(appTitle);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String recipe = inputName.getText().toString();
                String photoUrl = inputPhotoUrl.getText().toString();
                String description = inputIngredients.getText().toString();

                if (TextUtils.isEmpty(recipeId)) {
                    createUser(recipe, photoUrl);
                } else {
                    updateCategoryRecipes(recipe, photoUrl);
                }
            }
        });

        toggleButton();
    }

    // Changing button text
    private void toggleButton() {
        if (TextUtils.isEmpty(recipeId)) {
            btnSave.setText("Save");
        } else {
            btnSave.setText("Update");
        }
    }

    private void createUser(String name, String photoUrl) {
        if (TextUtils.isEmpty(recipeId)) {
            recipeId = mFirebaseDatabase.push().getKey();
        }

        CategoryRecipes user = new CategoryRecipes(name, photoUrl);

        mFirebaseDatabase.child(recipeId).setValue(user);

        addUserChangeListener();
    }

    private void addUserChangeListener() {
        mFirebaseDatabase.child(recipeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CategoryRecipes сategoryRecipes = dataSnapshot.getValue(CategoryRecipes.class);

                if (сategoryRecipes == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + сategoryRecipes.getName() + ", " + сategoryRecipes.getPhotoUrl());

                txtDetails.setText(сategoryRecipes.getName() + ", " + сategoryRecipes.getPhotoUrl());

                inputPhotoUrl.setText("");
                inputName.setText("");

                toggleButton();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }

    private void updateCategoryRecipes(String name, String photoUrl) {
        // updating the user via child nodes
        if (!TextUtils.isEmpty(name))
            mFirebaseDatabase.child(recipeId).child("name").setValue(name);

//        if (!TextUtils.isEmpty(photoUrl))
//            mFirebaseDatabase.child(userId).child("photoUrl").setValue(photoUrl);
    }
}
