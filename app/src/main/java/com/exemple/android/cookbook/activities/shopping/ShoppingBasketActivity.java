package com.exemple.android.cookbook.activities.shopping;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.adapters.SelectedRecipeListRealmAdapter;
import com.exemple.android.cookbook.entity.realm.RealmRecipe;
import com.exemple.android.cookbook.helpers.IntentHelper;
import com.exemple.android.cookbook.helpers.SwipeHelper;

import java.util.List;

import io.realm.Realm;

public class ShoppingBasketActivity extends AppCompatActivity {

    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_basket);

        mRealm = Realm.getDefaultInstance();

        List<RealmRecipe> recipes = mRealm.where(RealmRecipe.class).equalTo("isInBasket", true).findAll();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recipeListInBasketRecyclerView);

        SelectedRecipeListRealmAdapter recipeListRealmAdapter = new SelectedRecipeListRealmAdapter(
                this,
                recipes,
                new SelectedRecipeListRealmAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(RealmRecipe item) {
                        IntentHelper.intentShoppingBasketActivity(ShoppingBasketActivity.this, item.getRecipeName());
                    }
                });

        recyclerView.setAdapter(recipeListRealmAdapter);
        new SwipeHelper(recyclerView, getApplicationContext()).attachSwipeSelectedRecipe();
    }
}
