package com.exemple.android.cookbook.activities;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
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

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;

    private String username;
    private String photoUrl;
    private GoogleApiClient googleApiClient;

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String ANONYMOUS = "anonymous";
    private String RECIPE_LIST = "recipeList";
    private static final int REQUEST_CODE = 1234;

    private RecyclerView recyclerView;
    private CategoryRecipeRecyclerAdapter recyclerAdapter;
    private List<CategoryRecipes> categoryRecipesList = new ArrayList<>();
    private SensorManager sensorManager;
    private Sensor sensor;

    private NavigationView navigationView;
    private TextView userNameTV;
    private CircleImageView userPhotoIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //        AUTHENTICATION
        View headerLayout = navigationView.getHeaderView(0);
        userNameTV = (TextView) headerLayout.findViewById(R.id.textViewForUserName);
        userPhotoIV = (CircleImageView) headerLayout.findViewById(R.id.imageViewForUserPhoto);

        if (firebaseUser == null) {
            username = ANONYMOUS;
        } else {
            username = firebaseUser.getDisplayName();
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        userRefresh();


        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Сategory_Recipes");

        recyclerView = (RecyclerView) findViewById(R.id.recipeListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    CategoryRecipes categoryRecipes = postSnapshot.getValue(CategoryRecipes.class);
                    categoryRecipesList.add(categoryRecipes);
                }
                if (firebaseUser != null) {
                    new FirebaseHelper(new FirebaseHelper.OnUserCategoryRecipe() {
                        @Override
                        public void OnGet(List<CategoryRecipes> category) {
                            categoryRecipesList = category;
                            recyclerAdapter = new CreaterRecyclerAdapter(getApplicationContext())
                                    .createRecyclerAdapter(category);
                            recyclerView.setAdapter(recyclerAdapter);
                        }
                    }).getUserCategoryRecipe(categoryRecipesList, username, firebaseDatabase);
                } else {
                    recyclerAdapter = new CreaterRecyclerAdapter(getApplicationContext())
                            .createRecyclerAdapter(categoryRecipesList);
                    recyclerView.setAdapter(recyclerAdapter);
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

        for (CategoryRecipes categoryRecipes : categoryRecipesList) {
            String name = categoryRecipes.getName().toLowerCase();
            if (name.contains(newText))
                newList.add(categoryRecipes);
        }
        recyclerAdapter.updateAdapter(newList);
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
            if (firebaseUser != null) {
                firebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(googleApiClient);
                username = ANONYMOUS;
                firebaseUser = null;
                userRefresh();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor,
                SensorManager.SENSOR_DELAY_NORMAL);

    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
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
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Toast.makeText(getApplicationContext(), matches.get(0),
                    Toast.LENGTH_LONG).show();
            if (matches.contains(getResources().getString(R.string.example_voice_recognition))) {
                Intent intent = new Intent(getApplicationContext(), RecipeListActivity.class);
                intent.putExtra(RECIPE_LIST, getResources().getString(R.string.example_voice_recognition));
                startActivity(intent);
            }
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
        if (firebaseUser == null) {
            navigationView.getMenu().findItem(R.id.nav_sign_in).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_sign_out).setVisible(false);
            username = ANONYMOUS;
            userNameTV.setText(username);
            userPhotoIV.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.a));
        } else {
            navigationView.getMenu().findItem(R.id.nav_sign_in).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_sign_out).setVisible(true);
            username = firebaseUser.getDisplayName();
            if (firebaseUser.getPhotoUrl() != null) {
                photoUrl = firebaseUser.getPhotoUrl().toString();
            }
            userNameTV.setText(username);
            Glide.with(this).load(photoUrl).into(userPhotoIV);
        }
    }
}
