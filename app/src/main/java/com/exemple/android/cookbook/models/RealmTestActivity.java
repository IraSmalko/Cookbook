package com.exemple.android.cookbook.models;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.exemple.android.cookbook.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.realm.Realm;

public class RealmTestActivity extends AppCompatActivity {

    Realm mRealm;

    DatabaseReference mFirebaseDatabaseReference;

    LinearLayout mLinearLayout;

    Recipe recipe;
    FirebaseRecipe firebaseRecipe;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realm_test);

        mLinearLayout = (LinearLayout) findViewById(R.id.linearlayout);

        button = (Button) findViewById(R.id.button);

        mRealm = Realm.getDefaultInstance();

        mRealm.beginTransaction();
        mRealm.deleteAll();
        mRealm.commitTransaction();

        String RECIPE_CHILD = "Recipe_lists/Супи/Розсольник";

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mFirebaseDatabaseReference.child(RECIPE_CHILD).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    firebaseRecipe = dataSnapshot.getValue(FirebaseRecipe.class);
                    Log.d("LOG", firebaseRecipe.getPhotoUrl());
                    button.setVisibility(View.VISIBLE);
                    mRealm.beginTransaction();
                    Recipe newRecipe = mRealm.createObject(Recipe.class);
                    newRecipe.setRecipe(firebaseRecipe);
                    mRealm.commitTransaction();
                    newRecipe.savePhoto(RealmTestActivity.this, mRealm);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Recipe realmRecipe : mRealm.allObjects(Recipe.class)) {
                    TextView textView = new TextView(RealmTestActivity.this);
                    textView.setText(realmRecipe.getRecipeName());
                    mLinearLayout.addView(textView);
                    ImageView imageView = new ImageView(RealmTestActivity.this);
                    imageView.setImageBitmap(realmRecipe.getPhoto());
                    mLinearLayout.addView(imageView);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
