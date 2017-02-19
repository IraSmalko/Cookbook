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
import com.exemple.android.cookbook.supporting.DBHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {

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

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.rating) + String.valueOf(rating),
                        Toast.LENGTH_LONG).show();
            }
        });

        intent = getIntent();

        if (actionBar != null) {
            actionBar.setTitle(intent.getStringExtra("recipe"));
        }
        Glide.with(getApplicationContext())
                .load(intent.getStringExtra("photo"))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(660, 480) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        loadPhotoStep = resource;
                        imageView.setImageBitmap(loadPhotoStep);
                    }
                });

        descriptionRecipe.setText(intent.getStringExtra("description"));

        btnDetailRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentStepRecipeActivity = new Intent(getApplicationContext(), StepRecipeActivity.class);
                intentStepRecipeActivity.putExtra("recipe", intent.getStringExtra("recipe"));
                intentStepRecipeActivity.putExtra("photo", intent.getStringExtra("photo"));
                intentStepRecipeActivity.putExtra("description", intent.getStringExtra("description"));
                intentStepRecipeActivity.putExtra("recipeList", intent.getStringExtra("recipeList"));
                startActivity(new Intent(intentStepRecipeActivity));
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

            cvRecipe.put("recipe", intent.getStringExtra("recipe"));
            cvRecipe.put("photo", path);
            cvRecipe.put("description", intent.getStringExtra("description"));
            long rowID = db.insertOrThrow("recipe", null, cvRecipe);

            idRecipe = (int) rowID;
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference()
                    .child("Step_recipe/" + intent.getStringExtra("recipeList") + "/" + intent.getStringExtra("recipe"));

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
        }else if(id == android.R.id.home) {
            Intent intent1 = new Intent(this, RecipeListActivity.class);
            intent1.putExtra("recipeList", intent.getStringExtra("recipeList"));
            startActivity(intent1);
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
        cvStepRecipe.put("id_recipe", idRecipe);
        cvStepRecipe.put("number_step", stepRecipe.get(iterator).getNumberStep());
        cvStepRecipe.put("text_step", stepRecipe.get(iterator).getTextStep());
        cvStepRecipe.put("photo_step", path);
        db.insertOrThrow("step_recipe", null, cvStepRecipe);
        iterator = ++iterator;
        loadPhoto();
    }

    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(this, RecipeListActivity.class);
        intent1.putExtra("recipeList", intent.getStringExtra("recipeList"));
        startActivity(intent1);
    }
}
