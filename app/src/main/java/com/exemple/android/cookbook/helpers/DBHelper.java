package com.exemple.android.cookbook.helpers;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String TABLE_RECIPE = "recipe";
    public static final String TABLE_STEP_RECIPE = "step_recipe";
    public static final String TABLE_INGREDIENTS_RECIPE = "ingredients";
    private static final String DATABASE_NAME = "myDB.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_RECIPE + " ("
                + "id integer primary key autoincrement,"
                + "recipe text,"
                + "photo text,"
                + "in_basket numeric,"
                + "in_saved numeric,"
                + "is_edited numeric" + ");");

        db.execSQL("create table " + TABLE_STEP_RECIPE + " ("
                + "id integer primary key autoincrement,"
                + "id_recipe integer,"
                + "number_step text,"
                + "text_step text,"
                + "photo_step text" + ");");

        db.execSQL("create table " + TABLE_INGREDIENTS_RECIPE + " ("
                + "id integer primary key autoincrement,"
                + "id_recipe integer,"
                + "ingredient_name text,"
                + "ingredient_quantity float,"
                + "ingredient_unit text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE);
        onCreate(db);
    }
}
