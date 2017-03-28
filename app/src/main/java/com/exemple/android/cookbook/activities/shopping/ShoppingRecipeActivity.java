package com.exemple.android.cookbook.activities.shopping;

import com.exemple.android.cookbook.adapters.ShoppingAdapters;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.firebase.RecipeIngredient;
import com.exemple.android.cookbook.entity.realm.RealmIngredient;
import com.exemple.android.cookbook.entity.realm.RealmRecipe;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;


/**
 * Created by Sakurov on 12.03.2017.
 */

public class ShoppingRecipeActivity extends AppCompatActivity {

    private static final String RECIPE_LIST = "recipeList";
    private static final String RECIPE = "recipe";
    private static final String PHOTO = "photo";
    private static final String DESCRIPTION = "description";
    private static final String USERNAME = "username";
    private static final String IS_PERSONAL = "isPersonal";
    private static final Long ANIMATION_DELAY = 700L;

    private String INGREDIENTS_CHILD;

    private List<RecipeIngredient> mIngredientsShop = new ArrayList<>();
    private List<RecipeIngredient> mIngredientsBasket = new ArrayList<>();

    private DatabaseReference mFirebaseDatabaseReference;

    private TextView mRecipeNameTextView;
    private RecyclerView mShopIngredientsRecyclerView;
    private RecyclerView mBasketIngredientsRecyclerView;

    public Intent mIntent;

    private Realm mRealm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);


        mRecipeNameTextView = (TextView) findViewById(R.id.recipeNameTV);
        mShopIngredientsRecyclerView = (RecyclerView) findViewById(R.id.shop_ingredients_RV);
        mBasketIngredientsRecyclerView = (RecyclerView) findViewById(R.id.basket_ingredients_RV);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mIntent = getIntent();

        mRecipeNameTextView.setText(mIntent.getStringExtra(RECIPE));

        mRealm = Realm.getDefaultInstance();

        List<RealmIngredient> ingredients = mRealm.where(RealmRecipe.class)
                .equalTo("recipeName", mIntent.getStringExtra(RECIPE))
                .findAll()
                .get(0)
                .getIngredients();

        for (RealmIngredient ingredient: ingredients
             ) {
            mIngredientsShop.add(new RecipeIngredient(ingredient));
        }

//        INGREDIENTS_CHILD = "Recipe_lists/" + mIntent.getStringExtra(RECIPE_LIST) + "/" + mIntent.getStringExtra(RECIPE) + "/ingredients";
//        mFirebaseDatabaseReference.child(INGREDIENTS_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    mIngredientsShop.add(data.getValue(RealmIngredient.class));
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        mShopIngredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBasketIngredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        (new ShoppingAdapters(mIngredientsShop, mIngredientsBasket))
                .setShoppingAdaptersToRecyclers(mShopIngredientsRecyclerView, mBasketIngredientsRecyclerView);

    }


}
