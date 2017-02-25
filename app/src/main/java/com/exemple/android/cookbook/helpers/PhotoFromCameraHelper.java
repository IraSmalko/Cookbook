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

    private Context context;
    @NonNull
    private OnPhotoPicked onPhotoPickedListener;

    private Uri filePath;

    public PhotoFromCameraHelper(Context context, @NonNull OnPhotoPicked onPhotoPickedListener) {
        this.context = context;
        this.onPhotoPickedListener = onPhotoPickedListener;
    }

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(context.getPackageManager()) != null) {

            filePath = createFileUri();

            if (filePath != null) {
                List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    context.grantUriPermission(packageName, filePath, Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);
                ActivityCompat.startActivityForResult(
                        (AppCompatActivity) context, intent, REQUEST_IMAGE_CAPTURE, null);
            }
        }
    }

    private Uri createFileUri()  {
        File file = new File(context.getCacheDir(), "photo.jpg");

        return FileProvider.getUriForFile(context, "com.exemple.android.cookbook", file);
    }

    public void pickPhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        ActivityCompat.startActivityForResult(
                (AppCompatActivity) context, photoPickerIntent, GALLERY_REQUEST, null);
    }

    public void onActivityResult(int resultCode, int requestCode, Intent data) {
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Uri photoUri = data.getData();
                    onPhotoPickedListener.onPicked(photoUri);
                }
            case REQUEST_IMAGE_CAPTURE:

                if (resultCode == Activity.RESULT_OK) {
                    onPhotoPickedListener.onPicked(filePath);
                }
        }
    }

    public interface OnPhotoPicked {
        void onPicked(Uri photoUri);
    }

}
