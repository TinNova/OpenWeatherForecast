package com.example.tin.openweatherforecast.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherDbHelper extends SQLiteOpenHelper {

    /* The name of the database as it will be saved on the users Android Device */
    private static final String DATABASE_NAME = "open_weather_forecast.db";
    private static final int DATABASE_VERSION = 9;

    /* Constructor that takes a context and calls the parent constructor */
    public WeatherDbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " +
                WeatherContract.WeatherEntry.TABLE_NAME + " (" +
                WeatherContract.WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                WeatherContract.WeatherEntry.COLUMN_UNIX_DATE + " REAL NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_CALC_DATE + " TEXT NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_TEMP_CURRENT + " DOUBLE NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_TEMP_MIN + " REAL NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_TEMP_MAX + " REAL NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_WEATHER_DESC + " TEXT NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_ICON_ID + " TEXT NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_WIND_DEGREE + " REAL NOT NULL" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    /*
     * This database is only a cache for online data, so its upgrade policy is simply to discard
     * the data and call through to onCreate to recreate the table.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherContract.WeatherEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
