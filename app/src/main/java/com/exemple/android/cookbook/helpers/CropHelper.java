package com.exemple.android.cookbook.helpers;


import android.content.Context;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import com.android.camera.CropImageIntentBuilder;

import java.io.File;

public class CropHelper {

    private static final int REQUEST_CROP_PICTURE = 2;
    private Context context;

    public CropHelper(Context context) {
        this.context = context;
    }

    private Uri createFileUriCrop() {
        File file = new File(context.getCacheDir(), "photoCrop.jpg");

        return FileProvider.getUriForFile(context, "com.exemple.android.cookbook", file);
    }

    public Uri cropImage(Uri pictureCropImageUri) {
        Uri photoUri = createFileUriCrop();
        CropImageIntentBuilder cropImage = new CropImageIntentBuilder(660, 480, pictureCropImageUri);
        cropImage.setOutlineColor(0xFF03A9F4);
        cropImage.setSourceImage(photoUri);
        ActivityCompat.startActivityForResult((AppCompatActivity) context, cropImage
                .getIntent(context), REQUEST_CROP_PICTURE, null);
        return photoUri;
    }
}
