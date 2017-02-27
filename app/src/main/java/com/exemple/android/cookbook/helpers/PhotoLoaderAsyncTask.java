package com.exemple.android.cookbook.helpers;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;

import java.util.concurrent.ExecutionException;

public class PhotoLoaderAsyncTask extends AsyncTask<String, Bitmap, Bitmap> {

    private Context mContext;
    private PhotoLoadProcessed mPhotoLoadProcessed;

    public PhotoLoaderAsyncTask(Context context, PhotoLoadProcessed photoLoadProcessed) {
        mContext = context;
        mPhotoLoadProcessed = photoLoadProcessed;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String photoUrl = strings[0];
        Bitmap theBitmap = null;
        try {
            theBitmap = Glide
                    .with(mContext)
                    .load(photoUrl)
                    .asBitmap()
                    .into(660, 480)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return theBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        mPhotoLoadProcessed.onBitmapReady(bitmap);
    }

    public interface PhotoLoadProcessed {
        void onBitmapReady(Bitmap bitmap);
    }
}
