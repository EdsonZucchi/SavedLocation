package com.example.savedlocation.infra;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LocationDAO {

    private SQLiteDatabase database;
    private LocationDatabase helper;

    public LocationDAO(Context context) {
        helper = new LocationDatabase(context);
        open();
    }

    public void open(){
        database = helper.getWritableDatabase();
    }

    public void close(){
        helper.close();
    }

    public long createLocation(double latitude, double longitude) {
        ContentValues values = new ContentValues();
        values.put(LocationDatabase.COLUMN_LATITUDE, latitude);
        values.put(LocationDatabase.COLUMN_LONGITUDE, longitude);
        return database.insert(LocationDatabase.TABLE_LOCATIONS, null, values);
    }

    public Cursor readAllLocations() {
        String[] columns = {
                LocationDatabase.COLUMN_ID,
                LocationDatabase.COLUMN_LATITUDE,
                LocationDatabase.COLUMN_LONGITUDE
        };
        return database.query(LocationDatabase.TABLE_LOCATIONS, columns, null, null, null, null, null);
    }

    public int updateLocation(Long id, String name, double latitude, double longitude) {
        ContentValues values = new ContentValues();
        values.put(LocationDatabase.COLUMN_LATITUDE, latitude);
        values.put(LocationDatabase.COLUMN_LONGITUDE, longitude);
        return database.update(LocationDatabase.TABLE_LOCATIONS, values, LocationDatabase.COLUMN_ID + " = "+ id, null);
    }

    public int deleteLocation(Long id){
        return database.delete(LocationDatabase.TABLE_LOCATIONS, LocationDatabase.COLUMN_ID + " = " + id, null);
    }

    public int deleteAllLocations(){
        return database.delete(LocationDatabase.TABLE_LOCATIONS, null, null);
    }

}
