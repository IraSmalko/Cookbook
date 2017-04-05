package com.exemple.android.cookbook.activities;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.adapters.SelectedRecipeRecyclerListAdapter;
import com.exemple.android.cookbook.entity.SelectedRecipe;
import com.exemple.android.cookbook.helpers.DataSourceSQLite;
import com.exemple.android.cookbook.helpers.IntentHelper;
import com.exemple.android.cookbook.helpers.SwipeHelper;

public class SelectedRecipeListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_list_activity);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recipeListRecyclerView);
        TextView textView = (TextView) findViewById(R.id.error_loading);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        SelectedRecipeRecyclerListAdapter recipeRecyclerAdapter =
                new SelectedRecipeRecyclerListAdapter(this,
                        new DataSourceSQLite(this).getRecipes(DataSourceSQLite.REQUEST_SAVED),
                        new SelectedRecipeRecyclerListAdapter.ItemClickListener() {
                            @Override
                            public void onItemClick(SelectedRecipe item) {
                                IntentHelper.intentSelectedRecipeActivity(getApplicationContext(), item
                                        .getName(), item.getPhotoUrl(), item.getIdRecipe());
                            }
                        });
        recyclerView.setAdapter(recipeRecyclerAdapter);
        new SwipeHelper(recyclerView, getApplicationContext()).attachSwipeSelectedRecipe();

        if(recipeRecyclerAdapter.getItemCount() == 0){
            textView.setVisibility(View.VISIBLE);
        }
    }
}
