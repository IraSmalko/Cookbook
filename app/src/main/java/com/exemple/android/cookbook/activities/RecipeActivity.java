package com.exemple.android.cookbook.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.exemple.android.cookbook.helpers.DataSourceSQLite;
import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.StepRecipe;
import com.exemple.android.cookbook.helpers.IntentHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {

    private static final String RECIPE_LIST = "recipeList";
    private static final String RECIPE = "recipe";
    private static final String PHOTO = "photo";
    private static final String DESCRIPTION = "description";

    private Intent intent;
    private ImageView imageView;
    private Bitmap loadPhotoStep;
    private List<StepRecipe> stepRecipe = new ArrayList<>();
    private int idRecipe;
    private ProgressDialog progressDialog;
    private DataSourceSQLite dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        TextView descriptionRecipe = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        Button btnDetailRecipe = (Button) findViewById(R.id.btnDetailRecipe);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ActionBar actionBar = getSupportActionBar();

        dataSource = new DataSourceSQLite(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.progress_dialog_title));

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.starFullySelected), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(ContextCompat.getColor(this, R.color.starPartiallySelected), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(ContextCompat.getColor(this, R.color.starNotSelected), PorterDuff.Mode.SRC_ATOP);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                Toast.makeText(RecipeActivity.this, getResources().getString(R.string.rating) + String.valueOf(rating),
                        Toast.LENGTH_LONG).show();
            }
        });

        intent = getIntent();

        if (actionBar != null) {
            actionBar.setTitle(intent.getStringExtra(RECIPE));
        }

        Glide.with(getApplicationContext())
                .load(intent.getStringExtra(PHOTO))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(660, 480) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        loadPhotoStep = resource;
                        imageView.setImageBitmap(loadPhotoStep);
                    }
                });

        descriptionRecipe.setText(intent.getStringExtra(DESCRIPTION));

        btnDetailRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentHelper.intentStepRecipeActivity(getApplicationContext(), intent
                        .getStringExtra(RECIPE), intent.getStringExtra(PHOTO), intent
                        .getStringExtra(DESCRIPTION), intent.getStringExtra(RECIPE_LIST));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.progress_vait));

            String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                    loadPhotoStep, Environment.getExternalStorageDirectory().getAbsolutePath(), null);

            idRecipe = dataSource.saveRecipe(intent.getStringExtra(RECIPE), path, intent.getStringExtra(DESCRIPTION));
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference()
                    .child("Step_recipe/" + intent.getStringExtra(RECIPE_LIST) + "/" + intent.getStringExtra(RECIPE));

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        StepRecipe step = postSnapshot.getValue(StepRecipe.class);
                        stepRecipe.add(step);
                    }
                    new DataSourceSQLite(getApplicationContext()).saveStepsSQLite(stepRecipe, idRecipe);
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            return true;
        } else if (id == android.R.id.home) {
            IntentHelper.intentRecipeListActivity(this, intent.getStringExtra(RECIPE_LIST));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        IntentHelper.intentRecipeListActivity(this, intent.getStringExtra(RECIPE_LIST));
    }

}
