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
    private Context mContext;
    private OnCrop mOnCropListener;
    private Uri mCropImageUri;

    public CropHelper(Context context, @NonNull OnCrop onCropListener) {
        mContext = context;
        mOnCropListener = onCropListener;
    }

    private Uri createFileUriCrop() {
        File file = new File(mContext.getCacheDir(), "photoCrop.jpg");

        return FileProvider.getUriForFile(mContext, "com.exemple.android.cookbook", file);
    }

    public void cropImage(Uri photoUri) {
        mCropImageUri = createFileUriCrop();
        CropImageIntentBuilder cropImage = new CropImageIntentBuilder(660, 480, mCropImageUri);
        cropImage.setOutlineColor(0xFF03A9F4);
        cropImage.setSourceImage(photoUri);
        ActivityCompat.startActivityForResult((AppCompatActivity) mContext, cropImage
                .getIntent(mContext), REQUEST_CROP_PICTURE, null);
    }

    public void onActivityResult(int resultCode, int requestCode) {
        switch (requestCode) {
            case REQUEST_CROP_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    mOnCropListener.onCrop(mCropImageUri);
                }
                break;
        }
    }

    public interface OnCrop {
        void onCrop(Uri cropImageUri);
    }
}
