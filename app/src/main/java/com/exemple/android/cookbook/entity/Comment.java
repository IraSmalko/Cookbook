package com.exemple.android.cookbook.entity;

/**
 * Created by Sakurov on 04.02.2017.
 */

public class Comment {
    private String name;
    private String text;
    private String photoUrl;
    private String Id;
    private float rating;
    private String userId;

    public Comment() {

    }

    public Comment(String text, String name, String photoUrl, float rating, String userId) {
        this.name = name;
        this.text = text;
        this.photoUrl = photoUrl;
        this.rating = rating;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating){
        this.rating = rating;
    }

    public String getUserId() {
        return userId;
    }
}
