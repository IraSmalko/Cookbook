package com.exemple.android.cookbook.activities.selected;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.adapters.SelectedRecipeListRealmAdapter;
import com.exemple.android.cookbook.helpers.IntentHelper;
import com.exemple.android.cookbook.helpers.SwipeHelper;
import com.exemple.android.cookbook.entity.realm.RealmRecipe;

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

        mRealm = Realm.getDefaultInstance();

        List<RealmRecipe> recipes = mRealm.where(RealmRecipe.class).equalTo("isInSaved", true).findAll();

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
    }

}
