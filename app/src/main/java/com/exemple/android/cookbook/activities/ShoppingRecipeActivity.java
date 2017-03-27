package com.exemple.android.cookbook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.adapters.ShoppingAdapters;
import com.exemple.android.cookbook.entity.Ingredient;
import com.exemple.android.cookbook.helpers.DataSourceSQLite;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Sakurov on 12.03.2017.
 */

public class ShoppingRecipeActivity extends AppCompatActivity {

    private static final String RECIPE = "recipe";
    private static final String ID_RECIPE = "id_recipe";

    private List<Ingredient> mIngredientsShop = new ArrayList<>();
    private List<Ingredient> mIngredientsBasket = new ArrayList<>();


    private TextView mRecipeNameTextView;
    private RecyclerView mShopIngredientsRecyclerView;
    private RecyclerView mBasketIngredientsRecyclerView;

    public Intent mIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        mRecipeNameTextView = (TextView) findViewById(R.id.recipeNameTV);
        mShopIngredientsRecyclerView = (RecyclerView) findViewById(R.id.shop_ingredients_RV);
        mBasketIngredientsRecyclerView = (RecyclerView) findViewById(R.id.basket_ingredients_RV);

        mIntent = getIntent();

        mRecipeNameTextView.setText(mIntent.getStringExtra(RECIPE));

        mIngredientsShop = new DataSourceSQLite(this).readRecipeIngredients(mIntent.getIntExtra(ID_RECIPE,0));

        mShopIngredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBasketIngredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        (new ShoppingAdapters(mIngredientsShop, mIngredientsBasket))
                .setShoppingAdaptersToRecyclers(mShopIngredientsRecyclerView, mBasketIngredientsRecyclerView);

    }


}
