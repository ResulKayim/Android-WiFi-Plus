package com.example.setting.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(@Nullable Context context) {
        super(context, DatabaseContract.AppDataEntry.DB_NAME, null, DatabaseContract.AppDataEntry.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DatabaseContract.AppDataEntry.TABLE_NAME + " ( "
                    + DatabaseContract.AppDataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + DatabaseContract.AppDataEntry.COLUMN_NAME + " TEXT, "
                    + DatabaseContract.AppDataEntry.COLUMN_PACKAGE_NAME + " TEXT )");

        db.execSQL("CREATE TABLE " + DatabaseContract.WifiNameEntry.TABLE_NAME + " ( "
                    + DatabaseContract.WifiNameEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + DatabaseContract.WifiNameEntry.COLUMN_NAME + " TEXT, "
                    + DatabaseContract.WifiNameEntry.COLUMN_ISALWAYS + " INTEGER )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.AppDataEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.WifiNameEntry.TABLE_NAME);
        onCreate(db);
    }
}
