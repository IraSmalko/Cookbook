package com.exemple.android.cookbook.models;

import android.graphics.Bitmap;

import io.realm.RealmObject;

/**
 * Created by Sakurov on 14.03.2017.
 */

public class RealmImage extends RealmObject {
    byte[] image;

    public void setImage(byte[] image) {
        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }
}
