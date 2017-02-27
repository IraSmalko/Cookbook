package com.exemple.android.cookbook.helpers;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.util.List;

public class PhotoFromCameraHelper {

    private static final int REQUEST_IMAGE_CAPTURE = 22;
    private static final int GALLERY_REQUEST = 13;

    private Context mContext;
    @NonNull
    private OnPhotoPicked mOnPhotoPickedListener;

    private Uri mFilePath;

    public PhotoFromCameraHelper(Context context, @NonNull OnPhotoPicked onPhotoPickedListener) {
        mContext = context;
        mOnPhotoPickedListener = onPhotoPickedListener;
    }

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {

            mFilePath = createFileUri();

            if (mFilePath != null) {
                List<ResolveInfo> resInfoList = mContext.getPackageManager().queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    mContext.grantUriPermission(packageName, mFilePath, Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mFilePath);
                ActivityCompat.startActivityForResult(
                        (AppCompatActivity) mContext, intent, REQUEST_IMAGE_CAPTURE, null);
            }
        }
    }

    private Uri createFileUri() {
        File file = new File(mContext.getCacheDir(), "photo.jpg");

        return FileProvider.getUriForFile(mContext, "com.exemple.android.cookbook", file);
    }

    public void pickPhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        ActivityCompat.startActivityForResult(
                (AppCompatActivity) mContext, photoPickerIntent, GALLERY_REQUEST, null);
    }

    public void onActivityResult(int resultCode, int requestCode, Intent data) {
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Uri photoUri = data.getData();
                    mOnPhotoPickedListener.onPicked(photoUri);
                }
            case REQUEST_IMAGE_CAPTURE:

                if (resultCode == Activity.RESULT_OK) {
                    mOnPhotoPickedListener.onPicked(mFilePath);
                }
        }
    }

    public interface OnPhotoPicked {
        void onPicked(Uri photoUri);
    }

}
