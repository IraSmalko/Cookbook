package com.exemple.android.cookbook.helpers;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.exemple.android.cookbook.R;
import com.exemple.android.cookbook.entity.CategoryRecipes;

import java.util.ArrayList;
import java.util.List;

public class VoiceRecognitionHelper {

    private static final String APP_PREFERENCES = "mysettings";
    private static final String VOICE_RECOGNITION = "VoiceRecognition";

    private SharedPreferences mSharedPreferences;
    private Context mContext;
    private int mStatus;
    private int mOn = 1;

    public VoiceRecognitionHelper(Context context) {
        mContext = context;
    }

    public void startVoiceRecognition() {
        if (getStatusVoiceRecognition() == mOn) {
            if (new CheckOnlineHelper(mContext).isOnline()) {
                IntentHelper.startVoiceRecognitionActivity(mContext);
            }
            Toast.makeText(mContext, mContext.getString(R.string.not_online), Toast.LENGTH_SHORT).show();
        }
    }

    public AlertDialog createdAlertDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_voice_recognition, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(view);
        int off = 0;
        if (getStatusVoiceRecognition() == off) {
            builder.setMessage(mContext.getResources().getString(R.string.on_voice_alert_message));
            mStatus = mOn;
        } else {
            builder.setMessage(mContext.getResources().getString(R.string.off_voice_alert_message));
            mStatus = off;
        }
        builder.setPositiveButton(mContext.getResources().getString(R.string
                .positive_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                saveStatusVoiceRecognition(mStatus);
            }
        })
                .setNegativeButton(mContext.getResources().getString(R.string
                        .negative_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        TextView info = (TextView) view.findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return builder.create();
    }

    private void saveStatusVoiceRecognition(int status) {
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFERENCES, mContext.MODE_PRIVATE);
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.putInt(VOICE_RECOGNITION, status);
        ed.apply();
    }

    private int getStatusVoiceRecognition() {
        int status = 0;
        mSharedPreferences = mContext.getSharedPreferences(APP_PREFERENCES, mContext.MODE_PRIVATE);
        if (mSharedPreferences.contains(VOICE_RECOGNITION)) {
            status = mSharedPreferences.getInt(VOICE_RECOGNITION, status);
        }
        return status;
    }

    public void onActivityResult(int resultCode, Intent data, List<CategoryRecipes> mForVoice) {
        if (resultCode == Activity.RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Toast.makeText(mContext, matches.get(0),
                    Toast.LENGTH_LONG).show();
            for (CategoryRecipes recipeList : mForVoice) {
                if (matches.contains(recipeList.getName().toLowerCase())) {
                    IntentHelper.intentRecipeListActivity(mContext, recipeList.getName());
                }
            }
        }
    }
}
