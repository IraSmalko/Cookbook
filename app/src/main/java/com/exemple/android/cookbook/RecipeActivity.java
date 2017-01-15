package com.exemple.android.cookbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.exemple.android.cookbook.supporting.CategoryRecipes;
import com.exemple.android.cookbook.supporting.Recipes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {

    private String RECIPE = "recipe";
    private String PHOTO_URL = "photo";
    private List<Recipes> recipesData = new ArrayList<>();
    TextView descriptionRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        descriptionRecipe = (TextView) findViewById(R.id.textView);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Button btnSaveRecipe = (Button) findViewById(R.id.btn_save_recipe);
        Button btnDetailRecipe = (Button) findViewById(R.id.btn_detail_recipe);
        ListView comments = (ListView) findViewById(R.id.list_view);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        EditText editText = (EditText) findViewById(R.id.editText);
        Button saveComments = (Button) findViewById(R.id.save_comments);
        ActionBar actionBar = getSupportActionBar();

        Intent intent = getIntent();

        actionBar.setTitle(intent.getStringExtra(RECIPE));
        Glide.with(this).load(intent.getStringExtra(PHOTO_URL)).into(imageView);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(intent.getStringExtra(RECIPE));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Recipes recipes = postSnapshot.getValue(Recipes.class);

                    descriptionRecipe.setText(recipes.description);
                    recipesData.add(recipes);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }



}
