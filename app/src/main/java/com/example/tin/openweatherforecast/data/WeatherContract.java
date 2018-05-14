package com.example.tin.openweatherforecast.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class WeatherContract {

    /* The Authority, this is how your code knows which Content Provider to access */
    public static final String AUTHORITY = "com.example.tin.openweatherforecast";

    /* The base content URI = "content://" + <authority> */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    /*
     * Here we define the possible paths for accessing data in this contract
     * This is the path for the "weather" directory (the weather table)
     */
    public static final String PATH_WEATHER = "weather";

    /* We've made the class private here to prevent anyone accidentally instantiating the contract class */
    private WeatherContract() {
    }

    /*
     * BaseColumns is what creates the automatic Id's for each row.
     * We don't need to create a column for the ID sections as BaseColumns does it automatically
     */
    public static final class WeatherEntry implements BaseColumns {

        /*
         * WeatherEntry content URI = BASE_CONTENT_URI + PATH_WEATHER
         * content://com.example.tin.openweatherforecast
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        /* Table name & Column names */
        public static final String TABLE_NAME = "weather";
        public static final String COLUMN_UNIX_DATE = "unix_date";
        public static final String COLUMN_CALC_DATE = "calc_date";
        public static final String COLUMN_TEMP_CURRENT = "temp_current";
        public static final String COLUMN_TEMP_MIN = "temp_min";
        public static final String COLUMN_TEMP_MAX = "temp_max";
        public static final String COLUMN_WEATHER_DESC = "weather_description";
        //TODO: Look to change this to BitMap, else with an ID we can get the image unless they
        // are all saved into the app...
        public static final String COLUMN_WEATHER_ID = "weather_id";
        public static final String COLUMN_WIND_SPEED = "wind_speed";
        public static final String COLUMN_WIND_DEGREE = "wind_degree";
    }
}
