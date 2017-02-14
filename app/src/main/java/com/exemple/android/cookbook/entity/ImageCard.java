package com.exemple.android.cookbook.entity;


import android.graphics.Bitmap;

public class ImageCard {
    private byte[] bytesImage;
    private Bitmap image;

    public ImageCard(byte[] bytesImage, Bitmap image) {
        this.bytesImage = bytesImage;
        this.image = image;
    }

    public byte[] getBytesImage() {
        return bytesImage;
    }

    public Bitmap getImage() {
        return image;
    }
}
