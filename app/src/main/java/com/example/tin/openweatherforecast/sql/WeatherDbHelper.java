package com.example.tin.openweatherforecast.sql;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tin on 07/05/2018.
 */

public class WeatherDbHelper extends SQLiteOpenHelper {

    /* The name of the database as it will be saved on the users Android Device */
    private static final String DATABASE_NAME = "open_weather_forecast.db";
    private static final int DATABASE_VERSION = 1;

    /* Constructor that takes a context and calls the parent constructor */
    public WeatherDbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //TODO: Is "DOUBLE" the correct thing to have here? Maybe REAL is better, search for the answer
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " +
                WeatherContract.WeatherEntry.TABLE_NAME + " (" +
                WeatherContract.WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                WeatherContract.WeatherEntry.COLUMN_UNIX_DATE + " DOUBLE NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_CALC_DATE + " TEXT NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_TEMP_CURRENT + " DOUBLE NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_TEMP_MIN + " DOUBLE NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_TEMP_MAX + " DOUBLE NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_WEATHER_DESC + " TEXT NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_ICON_ID + " TEXT NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED + " DOUBLE NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_WIND_DEGREE + " DOUBLE NOT NULL" +
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