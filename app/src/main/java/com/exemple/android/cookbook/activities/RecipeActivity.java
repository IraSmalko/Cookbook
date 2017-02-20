package com.exemple.android.cookbook.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.StepRecipe;
import com.exemple.android.cookbook.helpers.IntentHelper;
import com.exemple.android.cookbook.supporting.DBHelper;
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
    private static final String ID_RECIPE = "id_recipe";
    private static final String TEXT_STEP = "text_step";
    private static final String PHOTO_STEP = "photo_step";
    private static final String NUMBER_STEP = "numberStep";
    private static final String STEP_RECIPE = "step_recipe";

    private DBHelper dbHelper;
    private Intent intent;
    private ImageView imageView;
    private Bitmap loadPhotoStep;
    private List<StepRecipe> stepRecipe = new ArrayList<>();
    private SQLiteDatabase db;
    private int idRecipe;
    private String pathPhotoStep;
    private int iterator = 0;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        TextView descriptionRecipe = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        Button btnDetailRecipe = (Button) findViewById(R.id.btnDetailRecipe);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ActionBar actionBar = getSupportActionBar();
        dbHelper = new DBHelper(this);

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
            db = dbHelper.getWritableDatabase();
            ContentValues cvRecipe = new ContentValues();

            String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                    loadPhotoStep, Environment.getExternalStorageDirectory().getAbsolutePath(), null);

            cvRecipe.put(RECIPE, intent.getStringExtra(RECIPE));
            cvRecipe.put(PHOTO, path);
            cvRecipe.put(DESCRIPTION, intent.getStringExtra(DESCRIPTION));
            long rowID = db.insertOrThrow(RECIPE, null, cvRecipe);

            idRecipe = (int) rowID;
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
                    loadPhoto();
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

    public void loadPhoto() {
        if (iterator < stepRecipe.size()) {
            Glide.with(getApplicationContext())
                    .load(stepRecipe.get(iterator).getPhotoUrlStep())
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(660, 480) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            loadPhotoStep = resource;
                            pathPhotoStep = MediaStore.Images.Media.insertImage(getContentResolver(),
                                    loadPhotoStep, Environment.getExternalStorageDirectory().getAbsolutePath(), null);
                            saveSteps(pathPhotoStep);
                        }
                    });
        } else {
            dbHelper.close();
            progressDialog.dismiss();
        }
    }

    public void saveSteps(String path) {
        ContentValues cvStepRecipe = new ContentValues();
        cvStepRecipe.put(ID_RECIPE, idRecipe);
        cvStepRecipe.put(NUMBER_STEP, stepRecipe.get(iterator).getNumberStep());
        cvStepRecipe.put(TEXT_STEP, stepRecipe.get(iterator).getTextStep());
        cvStepRecipe.put(PHOTO_STEP, path);
        db.insertOrThrow(STEP_RECIPE, null, cvStepRecipe);
        iterator = ++iterator;
        loadPhoto();
    }

    @Override
    public void onBackPressed() {
        IntentHelper.intentRecipeListActivity(this, intent.getStringExtra(RECIPE_LIST));
    }
}
