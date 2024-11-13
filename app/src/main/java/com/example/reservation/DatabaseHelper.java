package com.example.reservation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database constants
    public static final String DATABASE_NAME = "reservationsDB";
    public static final int DATABASE_VERSION = 1;

    // Table name
    public static final String TABLE_RESERVATIONS = "reservations";

    // Column names
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CLIENT_NAME = "client_name";
    public static final String COLUMN_FIELD_NUMBER = "field_number";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_TERRAIN_LIST = "terrain_list";

    // SQL statement to create the reservations table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_RESERVATIONS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CLIENT_NAME + " TEXT, " +
                    COLUMN_FIELD_NUMBER + " INTEGER, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_START_TIME + " TEXT, " +
                    COLUMN_DURATION + " INTEGER, " +
                    COLUMN_PRICE + " REAL, " +
                    COLUMN_TERRAIN_LIST + " TEXT" +
                    ");";

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the reservations table
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATIONS);
        onCreate(db);
    }
}