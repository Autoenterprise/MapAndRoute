package com.velychko.kyrylo.mapandroute.data.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import com.velychko.kyrylo.mapandroute.data.SQLite.DataModel.PlaceModel;
import com.velychko.kyrylo.mapandroute.data.SQLite.DataModel.UserModel;
import com.velychko.kyrylo.mapandroute.data.SQLite.DatabaseHelper.*;

public class DatabaseMaster {

    private SQLiteDatabase database;
    private DatabaseHelper dbCreator;

    private static DatabaseMaster instance;

    private DatabaseMaster(Context context) {
        dbCreator = new DatabaseHelper(context);
        if (database == null || !database.isOpen()) {
            database = dbCreator.getWritableDatabase();
        }
    }

    public static DatabaseMaster getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseMaster(context);
        }
        return instance;
    }

    public void addUser(UserModel userModel) {
        ContentValues cv = new ContentValues();
        cv.put(Users.COLUMN_NAME, userModel.name);
        cv.put(Users.COLUMN_PASSWORD, userModel.password);
        long l = database.insert(Users.TABLE_NAME, null, cv);
        l = 0;
    }

    public UserModel getUserByName(String name){
        Cursor cursor = database.query(Users.TABLE_NAME,
                null,
                Users.COLUMN_NAME + "=?",
                new String[]{name},
                null,
                null,
                null,
                "1");
        if (cursor.moveToFirst()){
            UserModel userModel = new UserModel();
//            userModel.id = cursor.getInt(cursor.getColumnIndex(Users._ID));
            userModel.name = cursor.getString(cursor.getColumnIndex(Users.COLUMN_NAME));
            userModel.password = cursor.getString(cursor.getColumnIndex(Users.COLUMN_PASSWORD));

            return userModel;
        } else {
            return null;
        }
    }

    public void addPlace(PlaceModel placeModel, String from_or_to) {
        ContentValues cv = new ContentValues();
        cv.put(Places.COLUMN_USER_NAME, placeModel.userName);
        cv.put(Places.COLUMN_LATITUDE, placeModel.latitude);
        cv.put(Places.COLUMN_LONGITUDE, placeModel.longitude);
        cv.put(Places.COLUMN_NAME, placeModel.name);
        cv.put(Places.COLUMN_FROM_OR_TO, from_or_to);
        long l = database.insert(Places.TABLE_NAME, null, cv);
        l = 0;
    }

    public Cursor getUserPlaces(String userName){
        return database.query(Places.TABLE_NAME,
                null,
                Places.COLUMN_USER_NAME + "=?",
                new String[]{userName},
                null,
                null,
                null);
    }

    @Nullable
    public List<PlaceModel> getAllPlaces() {
        String query = "SELECT * FROM " + Places.TABLE_NAME;
        Cursor cursor = database.rawQuery(query, null);

        List<PlaceModel> _list = new ArrayList<>();

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                PlaceModel placeModel = new PlaceModel();
                placeModel.userName = cursor.getString(cursor.getColumnIndex(Places.COLUMN_USER_NAME));
                placeModel.latitude = cursor.getDouble(cursor.getColumnIndex(Places.COLUMN_LATITUDE));
                placeModel.longitude = cursor.getDouble(cursor.getColumnIndex(Places.COLUMN_LONGITUDE));
                placeModel.name = cursor.getString(cursor.getColumnIndex(Places.COLUMN_NAME));
                _list.add(placeModel);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return _list;
    }

    public int deleteCurrentUserPlaces(String userName) {
        return database.delete(Places.TABLE_NAME, Places.COLUMN_USER_NAME + "=?",
                new String[]{userName});
    }

    public int clearDataBase() {
        return database.delete(Places.TABLE_NAME, null, null);
    }

}
