package com.exemple.android.cookbook.activities;


import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
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
import com.exemple.android.cookbook.helpers.VoiceRecognitionHelper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SensorEventListener,
        GoogleApiClient.OnConnectionFailedListener,
        SearchView.OnQueryTextListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int VOICE_REQUEST_CODE = 1234;
    public static final int SIGN_IN_REQUEST = 19901;
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
            Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
            startActivityForResult(intent, SIGN_IN_REQUEST);
        } else if (id == R.id.nav_sign_out) {
            showSignOutDialog();
        } else if (id == R.id.nav_basket) {
            startActivity(new Intent(this, ShoppingBasketActivity.class));
        } else if (id == R.id.main) {
            startActivity(new Intent(this, MainActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void userRefresh() {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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
        userRefresh();
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
        if (requestCode == SIGN_IN_REQUEST) {
            if (resultCode == RESULT_OK) {
                userRefresh();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    AlertDialog mSignOutDialog;

    public void showSignOutDialog() {

        final AlertDialog.Builder signOutDialog = new AlertDialog.Builder(this);
        signOutDialog.setTitle(getResources().getString(R.string.auth_exit));
        signOutDialog.setMessage(getResources().getString(R.string.auth_sure));
        signOutDialog.setCancelable(true);

        signOutDialog.setPositiveButton(getResources().getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (mFirebaseUser != null) {
                            Log.d("USER", mFirebaseUser.toString());
                            mFirebaseAuth.signOut();
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                            Log.d("USER", mFirebaseUser.toString());
                            mUsername = ANONYMOUS;
                            mFirebaseUser = null;
                            userRefresh();
                            layoutRefreshLogOut();
                        }
                        dialog.dismiss();
                    }
                });
        signOutDialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mSignOutDialog = signOutDialog.create();
        mSignOutDialog.show();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getBoolean("isSignOutDialogShown")) {
            showSignOutDialog();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSignOutDialog != null) {
            outState.putBoolean("isSignOutDialogShown", mSignOutDialog.isShowing());
        }
    }

    public void layoutRefreshLogOut() {

    }

}
