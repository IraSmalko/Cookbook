package com.exemple.android.cookbook;

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
import com.exemple.android.cookbook.supporting.DBHelper;

public class RecipeActivity extends AppCompatActivity {

    private String RECIPE = "recipe";
    private String PHOTO_URL = "photo";
    private String DESCRIPTION = "description";

    TextView descriptionRecipe;
    ImageView imageView;
    private DBHelper dbHelper;
    private Intent intent;
    Bitmap theBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        dbHelper = new DBHelper(this);
        descriptionRecipe = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        Button btnDetailRecipe = (Button) findViewById(R.id.btn_detail_recipe);
        ListView comments = (ListView) findViewById(R.id.list_view);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        EditText editText = (EditText) findViewById(R.id.editTextComent);
        Button saveComments = (Button) findViewById(R.id.save_comments);
        ActionBar actionBar = getSupportActionBar();

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

        actionBar.setTitle(intent.getStringExtra(RECIPE));

        Glide.with(this)
                .load(intent.getStringExtra(PHOTO_URL))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(660, 480) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        imageView.setImageBitmap(resource);
                        theBitmap = resource;
                    }
                });

        descriptionRecipe.setText(intent.getStringExtra(DESCRIPTION));

        btnDetailRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentStepRecipeActivity = new Intent(getApplicationContext(), StepRecipeActivity.class);
                intentStepRecipeActivity.putExtra(RECIPE, intent.getStringExtra(RECIPE));
                intentStepRecipeActivity.putExtra(PHOTO_URL, intent.getStringExtra(PHOTO_URL));
                intentStepRecipeActivity.putExtra(DESCRIPTION, intent.getStringExtra(DESCRIPTION));
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

            ContentValues cv = new ContentValues();
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                    theBitmap, Environment.getExternalStorageDirectory().getAbsolutePath(), null);

            cv.put(RECIPE, intent.getStringExtra(RECIPE));
            cv.put(PHOTO_URL, path);
            cv.put(DESCRIPTION, intent.getStringExtra(DESCRIPTION));

            long rowID = db.insert("recipeActivityTable", null, cv);

            dbHelper.close();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
