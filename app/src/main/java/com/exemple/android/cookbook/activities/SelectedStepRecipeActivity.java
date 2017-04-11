package com.exemple.android.cookbook.activities;


import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.Recipe;
import com.exemple.android.cookbook.entity.SelectedStepRecipe;
import com.exemple.android.cookbook.helpers.DataSourceSQLite;
import com.exemple.android.cookbook.helpers.IntentHelper;
import com.exemple.android.cookbook.helpers.VoiceRecognitionHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectedStepRecipeActivity extends AppCompatActivity
        implements SensorEventListener {

    private static final int INT_EXTRA = 0;
    private static final int VOICE_REQUEST_CODE = 1234;
    private static final String RECIPE = "recipe";
    private static final String PHOTO = "photo";
    private static final String ID_RECIPE = "id_recipe";
    private static final String NUMBER_STEP = "numberStep";

    private List<SelectedStepRecipe> mSelectedStepRecipes = new ArrayList<>();
    private int mIterator = 0;
    private ActionBar mActionBar;
    private TextView mTxtStepRecipe;
    private ImageView mImgStepRecipe;
    private Intent mIntent;
    private Context mContext = SelectedStepRecipeActivity.this;

    private SensorManager mSensorManager;
    private Sensor mSensor;


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
        DataSourceSQLite dataSource = new DataSourceSQLite(this);
        mSelectedStepRecipes = dataSource.readStepRecipe(mIntent.getIntExtra(ID_RECIPE, INT_EXTRA));

        if (savedInstanceState != null && savedInstanceState.containsKey(NUMBER_STEP)) {
            mIterator = savedInstanceState.getInt(NUMBER_STEP);
            updateData(mIterator);
        } else if (mSelectedStepRecipes.isEmpty()) {
            Toast.makeText(mContext, getResources().getString(R.string
                    .no_information_available), Toast.LENGTH_SHORT).show();
        } else {
            updateData(mIterator);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_step);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIterator = ++mIterator;
                updateData(mIterator);
            }
        });

        FloatingActionButton fabBack = (FloatingActionButton) findViewById(R.id.fab_step_back);
        fabBack.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           onBackPressed();
                                       }
                                   }
        );
    }

    public void updateData(int i) {
        if (i < mSelectedStepRecipes.size()) {
            mActionBar.setTitle(mSelectedStepRecipes.get(i).getNumberStep());
            mTxtStepRecipe.setText(mSelectedStepRecipes.get(i).getTextStep());
            Glide.with(mContext).load(mSelectedStepRecipes.get(i).getPhotoUrlStep()).placeholder(ContextCompat
                    .getDrawable(mContext, R.drawable.step_image)).dontTransform().into(mImgStepRecipe);
        } else {
            IntentHelper.intentSelectedRecipeActivity(mContext, mIntent.getStringExtra(RECIPE), mIntent
                    .getStringExtra(PHOTO), mIntent.getIntExtra(ID_RECIPE, INT_EXTRA));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_recipe_selected, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit_recipe) {
            Intent intent = mIntent;
            intent.setClass(this, EditRecipeStepActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
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
            mIterator = new VoiceRecognitionHelper(getApplicationContext()).onActivityResult(resultCode, data,
                    new Recipe(mIntent.getStringExtra(RECIPE), mIntent.getStringExtra(PHOTO), 0), mIterator,
                    mSelectedStepRecipes, mActionBar, mTxtStepRecipe, mImgStepRecipe, mIntent.getIntExtra(ID_RECIPE, INT_EXTRA));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (mIterator == 0) {
            IntentHelper.intentSelectedRecipeActivity(mContext, mIntent.getStringExtra(RECIPE), mIntent
                    .getStringExtra(PHOTO), mIntent.getIntExtra(ID_RECIPE, INT_EXTRA));
        } else {
            updateData(--mIterator);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mIterator > 0) {
            outState.putInt(NUMBER_STEP, mIterator);
        }
    }
}
