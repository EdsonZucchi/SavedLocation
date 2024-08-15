package com.example.savedlocation.infra;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocationDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "locations.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_LOCATIONS = "locations";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_LOCATIONS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_LATITUDE + " REAL, " +
                    COLUMN_LONGITUDE + " REAL" +
                    ");";

    public LocationDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        onCreate(db);
    }
}
