package com.exemple.android.cookbook.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.adapters.SelectedRecipeRecyclerListAdapter;
import com.exemple.android.cookbook.entity.SelectedRecipe;
import com.exemple.android.cookbook.helpers.DataSourceSQLite;
import com.exemple.android.cookbook.helpers.IntentHelper;
import com.exemple.android.cookbook.helpers.SwipeHelper;


public class ShoppingBasketActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_basket);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recipeListInBasketRecyclerView);

        SelectedRecipeRecyclerListAdapter recipeListAdapter =
                new SelectedRecipeRecyclerListAdapter(this,
                        new DataSourceSQLite(this).getRecipes(DataSourceSQLite.REQUEST_BASKET),
                        new SelectedRecipeRecyclerListAdapter.ItemClickListener() {
                            @Override
                            public void onItemClick(SelectedRecipe item) {
                                IntentHelper.intentShoppingBasketActivity(ShoppingBasketActivity.this, item.getName(), item.getIdRecipe());
                            }
                        });

        recyclerView.setAdapter(recipeListAdapter);
        new SwipeHelper(recyclerView, getApplicationContext()).attachSwipeSelectedRecipe();
    }
}
