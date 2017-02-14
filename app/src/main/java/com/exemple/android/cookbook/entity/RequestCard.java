package com.exemple.android.cookbook.entity;


import android.net.Uri;

public class RequestCard {
    private Uri photoUri;
    private int request;

    public RequestCard(Uri photoUri, int request){
        this.photoUri = photoUri;
        this.request = request;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public int getRequest() {
        return request;
    }
}
