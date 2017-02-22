package com.exemple.android.cookbook.helpers;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;

public class PhotoLoaderAsyncTask extends AsyncTask<String, Bitmap, Bitmap> {

    private WeakReference<Context> weakContext;
    private PhotoLoadProcessed photoLoadProcessed;

    public PhotoLoaderAsyncTask(Context context, PhotoLoadProcessed photoLoadProcessed) {
        weakContext = new WeakReference<>(context);
        this.photoLoadProcessed = photoLoadProcessed;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String photoUrl = strings[0];
        Context context = weakContext.get();
        Bitmap theBitmap = null;
        try {
            theBitmap = Glide
                    .with(context)
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
        photoLoadProcessed.onBitmapReady(bitmap);
    }

    public interface PhotoLoadProcessed {
        void onBitmapReady(Bitmap bitmap);
    }
}
