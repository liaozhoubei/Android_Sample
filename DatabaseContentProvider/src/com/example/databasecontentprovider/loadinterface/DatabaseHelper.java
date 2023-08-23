package com.example.databasecontentprovider.loadinterface;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Bei on 2016/2/24.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "load.db";
    public static final int VERSION = 1;
    public static final String USER_TABLE_NAME = "user";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String LOGIN_STATE = "login_state";
    public static final String LOGIN_TIME = "login_time";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + USER_TABLE_NAME + "(" + USERNAME + " VARCHAR(20) not null, " + PASSWORD + " VARCHAR(60) not null, " + LOGIN_STATE + " VARCHAR,"  + LOGIN_TIME + " VARCHAR);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
