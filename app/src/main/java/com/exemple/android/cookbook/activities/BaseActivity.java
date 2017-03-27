package com.exemple.android.cookbook.activities;


import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.activities.selected.SelectedRecipeListActivity;
import com.exemple.android.cookbook.activities.shopping.ShoppingBasketActivity;
import com.exemple.android.cookbook.entity.firebase.FirebaseRecipe;
import com.exemple.android.cookbook.helpers.VoiceRecognitionHelper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SensorEventListener,
        GoogleApiClient.OnConnectionFailedListener,
        SearchView.OnQueryTextListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int VOICE_REQUEST_CODE = 1234;
    public static final String ANONYMOUS = "anonymous";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private String mPhotoUrl;
    private GoogleApiClient mGoogleApiClient;

    private NavigationView mNavigationView;
    private TextView mUserNameTV;
    private CircleImageView mUserPhotoIV;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        View headerLayout = mNavigationView.getHeaderView(0);
        mUserNameTV = (TextView) headerLayout.findViewById(R.id.textViewForUserName);
        mUserPhotoIV = (CircleImageView) headerLayout.findViewById(R.id.imageViewForUserPhoto);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        userRefresh();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.selected) {
            startActivity(new Intent(getApplicationContext(), SelectedRecipeListActivity.class));
        } else if (id == R.id.nav_sign_in) {
            int SIGN_IN_REQUEST = 120;
            Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
            startActivityForResult(intent, SIGN_IN_REQUEST);
        } else if (id == R.id.nav_sign_out) {
            if (mFirebaseUser != null) {
                Log.d("USER", mFirebaseUser.toString());
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                Log.d("USER", mFirebaseUser.toString());
                mUsername = ANONYMOUS;
                mFirebaseUser = null;
                userRefresh();
            }
        } else if (id == R.id.nav_basket) {
            startActivity(new Intent(this, ShoppingBasketActivity.class));
        } else if (id == R.id.nav_share) {
            testingMethod();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void userRefresh() {
        if (mFirebaseUser == null) {
            mNavigationView.getMenu().findItem(R.id.nav_sign_in).setVisible(true);
            mNavigationView.getMenu().findItem(R.id.nav_sign_out).setVisible(false);
            mUsername = ANONYMOUS;
            mUserNameTV.setText(mUsername);
            mUserPhotoIV.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.a));
        } else {
            mNavigationView.getMenu().findItem(R.id.nav_sign_in).setVisible(false);
            mNavigationView.getMenu().findItem(R.id.nav_sign_out).setVisible(true);
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
            mUserNameTV.setText(mUsername);
            Glide.with(this).load(mPhotoUrl).into(mUserPhotoIV);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);

    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.values[0] == 0) {
            new VoiceRecognitionHelper(this).startVoiceRecognition();
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.voice_recognition:
                new VoiceRecognitionHelper(this).createdAlertDialog().show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    protected abstract int getLayoutResource();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_REQUEST_CODE) {
            new VoiceRecognitionHelper(getApplicationContext()).onActivityResult(resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void testingMethod() {
//        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
//        String CHILD_TEST = "test";
//
//        mFirebaseDatabaseReference.child(CHILD_TEST).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<FirebaseRecipe> recipes = new ArrayList<>();
//                HashMap<String, FirebaseRecipe> hashRecipes = new HashMap<String, FirebaseRecipe>();
//                GenericTypeIndicator<HashMap<String, FirebaseRecipe>> t =
//                        new GenericTypeIndicator<HashMap<String, FirebaseRecipe>>() {
//                        };
//                hashRecipes = dataSnapshot.getValue(t);
//                recipes.addAll(hashRecipes.values());
//                Log.d("Lop", "" + dataSnapshot.getChildrenCount());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

//        final String stepPhoto = "https://firebasestorage.googleapis.com/v0/b/cookbook-6cce5.appspot.com/o/Support%2Fstep_image.png?alt=media&token=fc2a77de-60d6-4489-b083-405de36c1302";
//        final String recipePhoto = "https://firebasestorage.googleapis.com/v0/b/cookbook-6cce5.appspot.com/o/Photo_%D0%A1ategory_Recipes%2FPhoto_%D0%A1ategory_Recipes-905908165?alt=media&token=235f3ee3-4654-4947-9c38-d125e8712d21";
//
//        mFirebaseDatabaseReference.child(CHILD_TEST).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                DatabaseReference ref = dataSnapshot.getRef();
//                DatabaseReference newRef = ref.push();
//                newRef.setValue(new FirebaseRecipe("Test Recipe", "Description of Recipe", recipePhoto));
//                List<FirebaseIngredient> ingredients = new ArrayList<>();
//                List<FirebaseStepRecipe> steps = new ArrayList<>();
//                for (int i = 1; i <= 3; i++) {
//                    ingredients.add(new FirebaseIngredient("ingredient " + i, (float) Math.random(), "kg"));
//                    steps.add(new FirebaseStepRecipe("Step " + i, stepPhoto, "Description of step " + i));
//                    newRef.child("ingredients").push().setValue(ingredients.get(i - 1));
//                    newRef.getRef().child("steps").push().setValue(steps.get(i - 1));
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
    }
}
