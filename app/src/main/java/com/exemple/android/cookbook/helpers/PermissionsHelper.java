package com.exemple.android.cookbook.helpers;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.exemple.android.cookbook.R;

public class PermissionsHelper {
    private static final int READ_EXTERNAL_STORAGE_REQUEST = 15;
    private static final int CAMERA_PERMISSION_REQUEST = 14;

    private Context mContext;
    private int mPermissionRequest;
    private String mPermission;

    public PermissionsHelper() {
    }

    public PermissionsHelper(Context context) {
        mContext = context;
    }

    public void showPermissionDialog(int request) {
        mPermissionRequest = request == READ_EXTERNAL_STORAGE_REQUEST ? READ_EXTERNAL_STORAGE_REQUEST
                : CAMERA_PERMISSION_REQUEST;
        mPermission = mPermissionRequest == READ_EXTERNAL_STORAGE_REQUEST ? Manifest.permission.READ_EXTERNAL_STORAGE
                : Manifest.permission.CAMERA;
        new AlertDialog.Builder(mContext)
                .setMessage(mContext.getResources().getString(R.string.give_permission))
                .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions((Activity) mContext,
                                new String[]{mPermission}, mPermissionRequest);
                    }
                })
                .setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        openPermissionSettings();
                    }
                })
                .create()
                .show();
    }

    private void openPermissionSettings() {
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + mContext.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        ActivityCompat.startActivity(mContext, intent, null);
    }
}
