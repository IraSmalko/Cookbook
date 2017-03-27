package com.exemple.android.cookbook.activities.main;


import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.Recipe;
import com.exemple.android.cookbook.entity.firebase.FirebaseRecipe;
import com.exemple.android.cookbook.entity.firebase.FirebaseStepRecipe;
import com.exemple.android.cookbook.helpers.FirebaseHelper;
import com.exemple.android.cookbook.helpers.IntentHelper;
import com.exemple.android.cookbook.helpers.VoiceRecognitionHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecipeStepActivity extends AppCompatActivity
        implements SensorEventListener {

    private static final String RECIPE_LIST = "recipeList";
    private static final String RECIPE = "recipe";
    private static final String PHOTO = "photo";
    private static final String DESCRIPTION = "description";
    private static final String IS_PERSONAL = "isPersonal";
    private static final int INT_EXTRA = 0;
    private static final int VOICE_REQUEST_CODE = 1234;

    private Intent mIntent;
    private List<FirebaseStepRecipe> mStepRecipe = new ArrayList<>();
    private TextView mTxtStepRecipe;
    private ImageView mImgStepRecipe;
    private ActionBar mActionBar;
    private Context mContext = RecipeStepActivity.this;
    private int mIterator = 0;
    private String mReference;
    private String mUsername;

    private FirebaseUser mFirebaseUser;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private FirebaseRecipe mFirebaseRecipe;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_recipe_activity);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mTxtStepRecipe = (TextView) findViewById(R.id.txtStepRecipe);
        mImgStepRecipe = (ImageView) findViewById(R.id.imgStepRecipe);
        mActionBar = getSupportActionBar();

        mIntent = getIntent();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        mReference = "Recipe_lists/" + mIntent.getStringExtra(RECIPE_LIST) + "/" + mIntent.getStringExtra(RECIPE);
        DatabaseReference databaseReference = firebaseDatabase.getReference().child(mReference);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mFirebaseRecipe = dataSnapshot.getValue(FirebaseRecipe.class);
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    FirebaseStepRecipe step = postSnapshot.getValue(FirebaseStepRecipe.class);
//                    mStepRecipe.add(step);
//                }
                mStepRecipe.addAll(mFirebaseRecipe.getSteps().values());
                if (mStepRecipe.isEmpty() && mFirebaseUser != null) {
                    mUsername = mFirebaseUser.getDisplayName();
                    mReference = mUsername + "/" + mReference;
                    new FirebaseHelper(new FirebaseHelper.OnStepRecipes() {
                        @Override
                        public void OnGet(List<FirebaseStepRecipe> stepRecipes) {
                            mStepRecipe.addAll(stepRecipes);
                            updateData(mIterator);
                            if (mStepRecipe.isEmpty()) {
                                Toast.makeText(mContext, getResources().getString(R
                                        .string.no_information_available), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).getStepsRecipe(mReference);
                } else if (mStepRecipe.size() != 0) {
                    updateData(mIterator);
                } else {
                    Toast.makeText(mContext, getResources().getString(R
                            .string.no_information_available), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_step);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData(++mIterator);
            }
        });
    }

    public void updateData(int iterator) {
        if (iterator < mStepRecipe.size()) {
            mActionBar.setTitle(mStepRecipe.get(iterator).getStepNumber());
            mTxtStepRecipe.setText(mStepRecipe.get(iterator).getStepText());
            Glide.with(mContext).load(mStepRecipe.get(iterator).getStepPhotoUrl()).into(mImgStepRecipe);
        } else {
            IntentHelper.intentRecipeActivity(mContext, mIntent.getStringExtra(RECIPE), mIntent
                    .getStringExtra(PHOTO), mIntent.getStringExtra(DESCRIPTION), mIntent
                    .getIntExtra(IS_PERSONAL, INT_EXTRA), mIntent.getStringExtra(RECIPE_LIST), mUsername);
        }
    }

    @Override
    public void onBackPressed() {
        if (mIterator == 0) {
            IntentHelper.intentRecipeActivity(mContext, mIntent.getStringExtra(RECIPE), mIntent
                    .getStringExtra(PHOTO), mIntent.getStringExtra(DESCRIPTION), mIntent
                    .getIntExtra(IS_PERSONAL, INT_EXTRA), mIntent.getStringExtra(RECIPE_LIST), mUsername);
        } else {
            updateData(--mIterator);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_REQUEST_CODE) {
            mIterator = new VoiceRecognitionHelper(getApplicationContext())
                    .onActivityResult(resultCode,
                            data,
//                            new FirebaseRecipe(mIntent.getStringExtra(RECIPE),
//                                    mIntent.getStringExtra(PHOTO),
//                                    mIntent.getStringExtra(DESCRIPTION),
//                                    mIntent.getIntExtra(IS_PERSONAL, INT_EXTRA)),
                            mFirebaseRecipe,
                            mIntent.getStringExtra(RECIPE_LIST),
                            mIterator,
                            mStepRecipe,
                            mActionBar,
                            mTxtStepRecipe,
                            mImgStepRecipe);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
