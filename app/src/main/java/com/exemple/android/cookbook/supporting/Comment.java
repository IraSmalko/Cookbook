package com.exemple.android.cookbook.supporting;

/**
 * Created by Sakurov on 04.02.2017.
 */

public class Comment {
    private String name;
    private String text;
    private String photoUrl;
    private String Id;

    public Comment() {

    }

    public Comment(String text, String name, String photoUrl) {
        this.name = name;
        this.text = text;
        this.photoUrl = photoUrl;
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

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}