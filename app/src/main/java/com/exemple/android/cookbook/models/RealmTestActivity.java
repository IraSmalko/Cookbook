package com.exemple.android.cookbook.models;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.Ingredient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

public class RealmTestActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_realm_test);

        mLinearLayout = (LinearLayout) findViewById(R.id.linearlayout);

        mRealm = Realm.getDefaultInstance();

        String RECIPE_CHILD = "Recipe_lists/" + "Супи" + "/" + "Розсольник";
        String INGREDIENTS_CHILD = "Recipe_lists/" + "Супи" + "/" + "Розсольник" + "/ingredients";
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mFirebaseDatabaseReference.child(INGREDIENTS_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    for (DataSnapshot dataTemp : dataSnapshot.getChildren()) {
                        ingredients.add(dataTemp.getValue(Ingredient.class));
                        realmIngredients.add(dataTemp.getValue(RealmIngredient.class));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        List<String> ingredientNames = new ArrayList<>();
        for (int i = 1; i <= ingredients.size(); i++) {
            ingredientNames.add(ingredients.get(i).getName());

        }

        Glide.with(getApplicationContext())
                .load("https://firebasestorage.googleapis.com/v0/b/cookbook-6cce5.appspot.com/o/%D0%A1%D1%83%D0%BF%D0%B8%2F%D0%A0%D0%BE%D0%B7%D1%81%D0%BE%D0%BB%D1%8C%D0%BD%D0%B8%D0%BA.jpg?alt=media&token=860829a8-4a55-4651-bc3b-22da0773f5f4")
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(660, 480) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        photo = resource;
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        resource.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byteArray = stream.toByteArray();
                    }
                });

        mRealm.beginTransaction();
        for (int i = 1; i <= 5; i++) {
            RealmRecipe realmRecipe = mRealm.createObject(RealmRecipe.class);
            realmRecipe.setRecipe("Розсольник", "", realmIngredients);
        }
        mRealm.commitTransaction();

        RealmResults<RealmRecipe> recipes = mRealm.where(RealmRecipe.class).findAll();

        if (!recipes.isEmpty()) {
            for (int i = recipes.size() - 1; i >= 0; i--) {
                TextView textView = new TextView(this);
                textView.setText(recipes.get(i).getRecipeName());
                mLinearLayout.addView(textView);
            }
        }

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRealm.beginTransaction();
                RealmImage image = mRealm.createObject(RealmImage.class);
                image.setImage(byteArray);
                mRealm.commitTransaction();
            }
        });
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] byTE = mRealm.allObjects(RealmImage.class).get(1).getImage();
                Bitmap bitmap = BitmapFactory.decodeByteArray(byTE, 0, byTE.length);
                ImageView imageView = new ImageView(RealmTestActivity.this);
                imageView.setImageBitmap(bitmap);
                mLinearLayout.addView(imageView);
                startActivity(new Intent(RealmTestActivity.this, RealmTest2Activity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
