package com.exemple.android.cookbook.activities;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.adapters.SelectedRecipeListRealmAdapter;
import com.exemple.android.cookbook.entity.selected.SelectedRecipe;
import com.exemple.android.cookbook.helpers.IntentHelper;
import com.exemple.android.cookbook.helpers.SwipeHelper;
import com.exemple.android.cookbook.entity.realm.RealmRecipe;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class SelectedRecipeListActivity extends AppCompatActivity {

    private static final String RECIPE = "recipe";
    private static final String PHOTO = "photo";
    private static final String DESCRIPTION = "description";
    private static final String ID = "id";

    private Realm mRealm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_list_activity);

//        List<SelectedRecipe> recipesList = new ArrayList<>();
//        DBHelper dbHelper = new DBHelper(this);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        Cursor c = db.query(RECIPE, null, null, null, null, null, null);
//
//        if (c.moveToFirst()) {
//            do {
//                int idColIndex = c.getColumnIndex(ID);
//                int recipeColIndex = c.getColumnIndex(RECIPE);
//                int photoColIndex = c.getColumnIndex(PHOTO);
//                int descriptionColIndex = c.getColumnIndex(DESCRIPTION);
//
//                recipesList.add(new SelectedRecipe(c.getString(recipeColIndex), c
//                        .getString(photoColIndex), c.getString(descriptionColIndex), c.getInt(idColIndex)));
//            } while (c.moveToNext());
//        } else {
//            c.close();
//        }

        mRealm = Realm.getDefaultInstance();

        List<RealmRecipe> recipes = mRealm.where(RealmRecipe.class).equalTo("isInSaved",true).findAll();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recipeListRecyclerView);

        SelectedRecipeListRealmAdapter recipeListRealmAdapter = new SelectedRecipeListRealmAdapter(
                this,
                recipes,
                new SelectedRecipeListRealmAdapter.ItemClickListener() {
            @Override
            public void onItemClick(RealmRecipe item) {
                IntentHelper.intentSelectedRecipeActivity(getApplicationContext(), item
                                .getRecipeName(), item.getRecipePhotoUrl(), item.getRecipeDescription());
            }
        });

        recyclerView.setAdapter(recipeListRealmAdapter);
        new SwipeHelper(recyclerView, getApplicationContext()).attachSwipeSelectedRecipe();

//        for (RealmRecipe recipe: recipes
//             ) {
//            recipesList.add(new SelectedRecipe(recipe.getRecipeName(),recipe.getRecipePhotoUrl(),recipe.getRecipeDescription(),1));
//        }
//
//
//        SelectedRecipeRecyclerListAdapter recipeRecyclerAdapter =
//                new SelectedRecipeRecyclerListAdapter(this, recipesList, new SelectedRecipeRecyclerListAdapter.ItemClickListener() {
//                    @Override
//                    public void onItemClick(SelectedRecipe item) {
//                        IntentHelper.intentSelectedRecipeActivity(getApplicationContext(), item
//                                .getName(), item.getRecipePhotoUrl(), item.getRecipeDescription(), item.getIdRecipe());
//                    }
//                });
//        recyclerView.setAdapter(recipeRecyclerAdapter);
//        new SwipeHelper(recyclerView, getApplicationContext()).attachSwipeSelectedRecipe();
    }


}
