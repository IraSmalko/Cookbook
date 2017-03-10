package com.exemple.android.cookbook.activities;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.adapters.CategoryRecipeRecyclerAdapter;
import com.exemple.android.cookbook.entity.CategoryRecipes;
import com.exemple.android.cookbook.helpers.CreaterRecyclerAdapter;
import com.exemple.android.cookbook.helpers.FirebaseHelper;
import com.exemple.android.cookbook.helpers.SwipeHelper;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnQueryTextListener,
        SensorEventListener,
        GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;

    private String mUsername;
    private String mPhotoUrl;
    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String ANONYMOUS = "anonymous";
    private static final int VOICE_REQUEST_CODE = 1234;

    private RecyclerView mRecyclerView;
    private SwipeHelper mSwipeHelper;
    private CategoryRecipeRecyclerAdapter mRecyclerAdapter;
    private List<CategoryRecipes> mCategoryRecipesList = new ArrayList<>();
    private List<CategoryRecipes> mPublicCategoryRecipes = new ArrayList<>();
    private List<CategoryRecipes> mForVoice = new ArrayList<>();
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private NavigationView mNavigationView;
    private TextView mUserNameTV;
    private CircleImageView mUserPhotoIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddCategoryRecipeActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        //        AUTHENTICATION
        View headerLayout = mNavigationView.getHeaderView(0);
        mUserNameTV = (TextView) headerLayout.findViewById(R.id.textViewForUserName);
        mUserPhotoIV = (CircleImageView) headerLayout.findViewById(R.id.imageViewForUserPhoto);

        if (mFirebaseUser == null) {
            mUsername = ANONYMOUS;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        userRefresh();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = mFirebaseDatabase.getReference("Сategory_Recipes");

        mRecyclerView = (RecyclerView) findViewById(R.id.recipeListRecyclerView);
        mSwipeHelper = new SwipeHelper(mRecyclerView, getApplicationContext());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    CategoryRecipes categoryRecipes = postSnapshot.getValue(CategoryRecipes.class);
                    mCategoryRecipesList.add(categoryRecipes);
                }
                mPublicCategoryRecipes = mCategoryRecipesList;
                if (mFirebaseUser != null) {
                    new FirebaseHelper(new FirebaseHelper.OnUserCategoryRecipe() {
                        @Override
                        public void OnGet(List<CategoryRecipes> category) {
                            category.addAll(mCategoryRecipesList);
                            mForVoice = category;
                            mRecyclerAdapter = new CreaterRecyclerAdapter(getApplicationContext())
                                    .createRecyclerAdapter(category);
                            mRecyclerView.setAdapter(mRecyclerAdapter);
                            mSwipeHelper.attachSwipeCategory(mPublicCategoryRecipes);
                        }
                    }).getUserCategoryRecipe(mUsername, mFirebaseDatabase);
                } else {
                    mRecyclerAdapter = new CreaterRecyclerAdapter(getApplicationContext())
                            .createRecyclerAdapter(mCategoryRecipesList);
                    mRecyclerView.setAdapter(mRecyclerAdapter);
                    mSwipeHelper.attachSwipeCategory(mCategoryRecipesList);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<CategoryRecipes> newList = new ArrayList<>();

        for (CategoryRecipes categoryRecipes : mCategoryRecipesList) {
            String name = categoryRecipes.getName().toLowerCase();
            if (name.contains(newText))
                newList.add(categoryRecipes);
        }
        mRecyclerAdapter.updateAdapter(newList);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.selected) {
            startActivity(new Intent(this, SelectedRecipeListActivity.class));
        } else if (id == R.id.nav_sign_in) {
            Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
            startActivity(intent);
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
//        if (sensorEvent.values[0] == 0) {
//            IntentHelper.startVoiceRecognitionActivity(getApplicationContext());
//            // near
//        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_REQUEST_CODE) {
            mForVoice = null != mForVoice ? mForVoice : mCategoryRecipesList;
            new VoiceRecognitionHelper(getApplicationContext()).onActivityResult(resultCode, data, mForVoice);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
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
}
