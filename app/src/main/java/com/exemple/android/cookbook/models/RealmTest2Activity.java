package com.exemple.android.cookbook.models;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.Ingredient;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

public class RealmTest2Activity extends AppCompatActivity {

    Realm mRealm;
    DatabaseReference mFirebaseDatabaseReference;

    List<Ingredient> ingredients = new ArrayList<>();
    RealmList<RealmIngredient> realmIngredients;
    LinearLayout mLinearLayout;
    Bitmap photo;
    byte[] byteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realm_test2);

        mLinearLayout = (LinearLayout) findViewById(R.id.linearlayout2);

        mRealm = Realm.getDefaultInstance();

        RealmResults<RealmRecipe> recipes = mRealm.where(RealmRecipe.class).findAll();

        if (!recipes.isEmpty()) {
            for (int i = recipes.size() - 1; i >= 0; i--) {
                TextView textView = new TextView(this);
                textView.setText(recipes.get(i).getRecipeName());
                mLinearLayout.addView(textView);
            }
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
