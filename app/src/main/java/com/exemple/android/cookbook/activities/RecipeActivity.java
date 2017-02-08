package com.exemple.android.cookbook.activities;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
    private Bitmap theBitmap;
    private Bitmap photoStep;
    private List<StepRecipe> stepRecipe = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        TextView descriptionRecipe = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        Button btnDetailRecipe = (Button) findViewById(R.id.btn_detail_recipe);
        ListView comments = (ListView) findViewById(R.id.list_view);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        EditText editText = (EditText) findViewById(R.id.editTextComent);
        Button saveComments = (Button) findViewById(R.id.save_comments);
        ActionBar actionBar = getSupportActionBar();
        dbHelper = new DBHelper(this);

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.starFullySelected), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(ContextCompat.getColor(this, R.color.starPartiallySelected), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(ContextCompat.getColor(this, R.color.starNotSelected), PorterDuff.Mode.SRC_ATOP);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                Toast.makeText(getApplicationContext(), "рейтинг: " + String.valueOf(rating),
                        Toast.LENGTH_LONG).show();
            }
        });

        intent = getIntent();

        actionBar.setTitle(intent.getStringExtra("recipe"));
        Glide.with(this)
                .load(intent.getStringExtra("photo"))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(660, 480) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        imageView.setImageBitmap(resource);
                        theBitmap = resource;
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
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cvRecipe = new ContentValues();
            ContentValues cvStepRecipe = new ContentValues();

            String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                    theBitmap, Environment.getExternalStorageDirectory().getAbsolutePath(), null);

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference(intent.getStringExtra("recipe"));

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        StepRecipe step = postSnapshot.getValue(StepRecipe.class);
                        stepRecipe.add(step);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            cvRecipe.put("recipe", intent.getStringExtra("recipe"));
            cvRecipe.put("photo", path);
            cvRecipe.put("description", intent.getStringExtra("description"));
            long rowID = db.insertOrThrow("recipe", null, cvRecipe);

            for (int i = 0; i < stepRecipe.size(); i++) {
                Glide.with(this)
                        .load(stepRecipe.get(i).getPhotoUrlStep())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>(660, 480) {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                photoStep = resource;
                            }
                        });
                String pathPhotoStep = MediaStore.Images.Media.insertImage(getContentResolver(),
                        photoStep, Environment.getExternalStorageDirectory().getAbsolutePath(), null);

                cvStepRecipe.put("id_recipe", rowID);
                cvStepRecipe.put("number_step", stepRecipe.get(i).getNumberStep());
                cvStepRecipe.put("text_step", stepRecipe.get(i).getTextStep());
                cvStepRecipe.put("photo_step", pathPhotoStep);
                db.insertOrThrow("step_recipe", null, cvStepRecipe);
            }
            dbHelper.close();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
