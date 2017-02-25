package com.exemple.android.cookbook.helpers;


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import com.exemple.android.cookbook.entity.ImageCard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class ProcessPhotoAsyncTask extends AsyncTask<Uri, Void, ImageCard> {

    private WeakReference<Context> weakContext;
    private OnPhotoProcessed onPhotoProcessedListener;

    public ProcessPhotoAsyncTask(Context context, OnPhotoProcessed onPhotoProcessedListener) {
        weakContext = new WeakReference<>(context);
        this.onPhotoProcessedListener = onPhotoProcessedListener;
    }

    @Override
    protected ImageCard doInBackground(Uri... uris) {
        Uri photo = uris[0];
        Context context = weakContext.get();
        Bitmap selectedBitmap = null;
        if (context != null) {
            ContentResolver resolver = context.getContentResolver();
            try {
                selectedBitmap = MediaStore.Images.Media.getBitmap(resolver, photo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytesImage = baos.toByteArray();

        return new ImageCard(bytesImage, selectedBitmap);
    }

    @Override
    protected void onPostExecute(ImageCard imageCard) {
        super.onPostExecute(imageCard);
        onPhotoProcessedListener.onDataReady(imageCard);
    }

    public interface OnPhotoProcessed {
        void onDataReady(@Nullable ImageCard imageCard);
    }

}
