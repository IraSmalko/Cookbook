package com.exemple.android.cookbook;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.exemple.android.cookbook.supporting.DBHelper;

import java.io.IOException;

public class SelectedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_activity);

        TextView descriptionRecipe = (TextView) findViewById(R.id.textView);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Button btnDetailRecipe = (Button) findViewById(R.id.btn_detail_recipe);
        ActionBar actionBar = getSupportActionBar();

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("recipeActivityTable", null, null, null, null, null, null);

        if (c.moveToFirst()) {

            int idColIndex = c.getColumnIndex("id");
            int recipeColIndex = c.getColumnIndex("recipe");
            int photoColIndex = c.getColumnIndex("photo");
            int descriptionColIndex = c.getColumnIndex("description");

            actionBar.setTitle(c.getString(recipeColIndex));
            descriptionRecipe.setText(c.getString(descriptionColIndex));

            try {
                imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(c.getString(photoColIndex))));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            c.close();
        }
    }

}
