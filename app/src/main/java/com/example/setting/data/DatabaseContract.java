package com.example.setting.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract implements BaseColumns {



    public static final class AppDataEntry {

        public static final String AUTHORITY = "com.example.setting.appdatacontentprovider";
        public static final String PATH = "apptable";
        public static final Uri BASE_CONTENT = Uri.parse("content://" + AUTHORITY);
        public static final Uri CONTENT = Uri.withAppendedPath(BASE_CONTENT, PATH);

        public static final int VERSION = 1;
        public static final String DB_NAME = "appdata.db";

        public static final String TABLE_NAME = "apptable";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PACKAGE_NAME = "packagename";
    }

    public static final class WifiNameEntry{

        public static final String WAUTHORITY = "com.example.setting.appdatacontentprovider";
        public static final String WPATH = "wifitable";
        public static final Uri WBASE_CONTENT = Uri.parse("content://" + WAUTHORITY);
        public static final Uri WCONTENT = Uri.withAppendedPath(WBASE_CONTENT, WPATH);

        public static final String TABLE_NAME = "wifitable";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ISALWAYS = "isalways";
    }

}
