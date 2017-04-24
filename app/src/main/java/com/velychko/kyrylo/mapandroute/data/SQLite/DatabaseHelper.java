package com.velychko.kyrylo.mapandroute.data.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class DatabaseHelper extends SQLiteOpenHelper {


    public static final String DB_NAME = "MapAndRoute.Db";
    public static final int DB_VERSION = 1;

    public static class Places implements BaseColumns {
        public static final String TABLE_NAME = "place";

        public static final String COLUMN_USER_NAME = "user_name";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_FROM_OR_TO = "from_or_to";
        public static final String COLUMN_NAME = "name";
    }

    public static class Users implements BaseColumns {
        public static final String TABLE_NAME = "users";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PASSWORD = "password";
    }

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_PLACES = "CREATE TABLE " + Places.TABLE_NAME +
                " (" +
                Places._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Places.COLUMN_USER_NAME + " TEXT, " +
                Places.COLUMN_LATITUDE + " REAL, " +
                Places.COLUMN_LONGITUDE + " REAL, " +
                Places.COLUMN_FROM_OR_TO + " TEXT, " +
                Places.COLUMN_NAME + " TEXT" + ");";
        db.execSQL(CREATE_TABLE_PLACES);

        String CREATE_TABLE_USERS = "CREATE TABLE " + Users.TABLE_NAME +
                " (" +
                Users._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Users.COLUMN_NAME + " TEXT, " +
                Users.COLUMN_PASSWORD + " TEXT" + ");";
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
