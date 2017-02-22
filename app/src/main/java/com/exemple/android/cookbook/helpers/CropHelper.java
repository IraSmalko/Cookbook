package com.exemple.android.cookbook.helpers;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import com.android.camera.CropImageIntentBuilder;

import java.io.File;

public class CropHelper {

    private static final int REQUEST_CROP_PICTURE = 2;
    private Context context;
    private OnCrop onCropListener;
    private Uri cropImageUri;

    public CropHelper(Context context, @NonNull OnCrop onCropListener) {
        this.context = context;
        this.onCropListener = onCropListener;
    }

    private Uri createFileUriCrop() {
        File file = new File(context.getCacheDir(), "photoCrop.jpg");

        return FileProvider.getUriForFile(context, "com.exemple.android.cookbook", file);
    }

    public void cropImage(Uri photoUri) {
        cropImageUri = createFileUriCrop();
        CropImageIntentBuilder cropImage = new CropImageIntentBuilder(660, 480, cropImageUri);
        cropImage.setOutlineColor(0xFF03A9F4);
        cropImage.setSourceImage(photoUri);
        ActivityCompat.startActivityForResult((AppCompatActivity) context, cropImage
                .getIntent(context), REQUEST_CROP_PICTURE, null);
    }

    public void onActivityResult(int resultCode, int requestCode) {
        switch (requestCode) {
            case REQUEST_CROP_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    onCropListener.onCrop(cropImageUri);
                }
        }
    }

    public interface OnCrop {
        void onCrop(Uri cropImageUri);
    }
}
