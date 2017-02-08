package com.exemple.android.cookbook.supporting;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table recipe ("
                + "id integer primary key autoincrement,"
                + "recipe text,"
                + "photo text,"
                + "description text" + ");");

        db.execSQL("create table step_recipe ("
                + "id integer primary key autoincrement,"
                + "id_recipe integer,"
                + "number_step text,"
                + "text_step text,"
                + "photo_step text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
