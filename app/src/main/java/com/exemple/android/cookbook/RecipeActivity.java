package com.exemple.android.cookbook;

import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.ViewTarget;
import com.exemple.android.cookbook.supporting.Recipes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RecipeActivity extends AppCompatActivity {

    private String RECIPE = "recipe";
    private String PHOTO_URL = "photo";
    private String DESCRIPTION = "description";

    private ShareActionProvider mShareActionProvider;

    Intent intent;
    TextView descriptionRecipe;
    Intent mShareIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        descriptionRecipe = (TextView) findViewById(R.id.textView);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
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
        mShareActionProvider = null;
        intent = getIntent();

        final String name = intent.getStringExtra(RECIPE);
        final String description = intent.getStringExtra(DESCRIPTION);

        actionBar.setTitle(intent.getStringExtra(RECIPE));

        Glide.with(this)
                .load(intent.getStringExtra(PHOTO_URL))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(500, 500) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        imageView.setImageBitmap(resource);

//                        Uri photoUri = getLocalBitmapUri(resource);

                        String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                                resource, "Image Description", null);
                        Uri photoUri = Uri.parse(path);

                        mShareIntent = new Intent(android.content.Intent.ACTION_SEND);
                        mShareIntent.putExtra(Intent.EXTRA_TEXT, name + "\n \n" + description);
                        mShareIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
                        mShareIntent.setType("image/*");
                        mShareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

//                        List<ResolveInfo> resolvedInfoActivities = getPackageManager()
//                                .queryIntentActivities(mShareIntent, PackageManager.MATCH_DEFAULT_ONLY);
//                        for (ResolveInfo ri : resolvedInfoActivities) {
//                            grantUriPermission(ri.activityInfo.packageName, photoUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                            Log.d("LOG packages", ri.activityInfo.packageName);
//                        }

                        setShareIntent();
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
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        setShareIntent();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    public Uri getLocalBitmapUri(Bitmap bitmap) {
//        Uri bmpUri = null;
//        try {
//            File file = new File(getExternalCacheDir(),
//                    "share_" + System.currentTimeMillis() + ".jpg");
//            FileOutputStream out = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
//            out.close();
//            Log.d("LOG!!!", file.toString());
//            bmpUri = FileProvider.getUriForFile(this, "com.exemple.android.cookbook.fileprovider", file);
//            Log.d("LOG!!!", bmpUri.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return bmpUri;
//    }

    private void setShareIntent() {
        if (mShareActionProvider != null && mShareIntent != null) {
            mShareActionProvider.setShareIntent(mShareIntent);
        }
    }

}
