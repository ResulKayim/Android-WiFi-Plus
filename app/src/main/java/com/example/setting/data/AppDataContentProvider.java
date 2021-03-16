package com.example.setting.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AppDataContentProvider extends ContentProvider {

    SQLiteDatabase database;

    private static final UriMatcher MATCHER;

    static {
        MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        MATCHER.addURI(DatabaseContract.AppDataEntry.AUTHORITY, DatabaseContract.AppDataEntry.PATH, 1);
        MATCHER.addURI(DatabaseContract.WifiNameEntry.WAUTHORITY, DatabaseContract.WifiNameEntry.WPATH, 2);
    }


    @Override
    public boolean onCreate() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        database = databaseHelper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor result = null;
        switch (MATCHER.match(uri)) {
            case 1:
                String[] colums = new String[]{DatabaseContract.AppDataEntry._ID, DatabaseContract.AppDataEntry.COLUMN_NAME, DatabaseContract.AppDataEntry.COLUMN_PACKAGE_NAME};
                result = database.query(DatabaseContract.AppDataEntry.TABLE_NAME, colums, selection, selectionArgs, null, null, null);
                break;
            case 2:
                String[] wifiColums = new String[]{DatabaseContract.WifiNameEntry._ID, DatabaseContract.WifiNameEntry.COLUMN_NAME, DatabaseContract.WifiNameEntry.COLUMN_ISALWAYS};
                result = database.query(DatabaseContract.WifiNameEntry.TABLE_NAME, wifiColums, selection, selectionArgs, null, null, null);
        }
        return result;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        switch (MATCHER.match(uri)) {
            case 1:
                long resulrId = database.insert(DatabaseContract.AppDataEntry.TABLE_NAME, null, values);
                return ContentUris.withAppendedId(DatabaseContract.AppDataEntry.CONTENT, resulrId);
            case 2:
                long resultId = database.insert(DatabaseContract.WifiNameEntry.TABLE_NAME, null, values);
                return ContentUris.withAppendedId(DatabaseContract.WifiNameEntry.WCONTENT, resultId);
        }

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int effectedItem = 0;

        switch (MATCHER.match(uri)) {
            case 1:
                effectedItem = database.delete(DatabaseContract.AppDataEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case 2:
                effectedItem = database.delete(DatabaseContract.WifiNameEntry.TABLE_NAME, selection, selectionArgs);

        }
        return effectedItem;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
