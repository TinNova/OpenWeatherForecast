package com.example.tin.openweatherforecast.data;

import android.util.Log;

import com.example.tin.openweatherforecast.MainActivity;
import com.example.tin.openweatherforecast.utilities.DateUtils;
import com.example.tin.openweatherforecast.utilities.LocationUtils;

public class WeatherSharedPreferencesHelper {

    private static final String TAG = WeatherSharedPreferencesHelper.class.getSimpleName();

    /* sharedPreferences keys */
    final public static String SHARED_PREF_LAT = "shared_pref_lat";
    final public static String SHARED_PREF_LON = "shared_pref_lon";
    final public static String SHARED_PREF_LAST_UPDATE = "shared_pref_last_update";

    /* Setting/Saving data to SharedPreferences */
    public static void setLatLonAndDate(Double lat, Double lon) {

        /* Rounding the lat/lon Doubles to two decimal places */
        MainActivity.weatherSharedPref.edit().putString(SHARED_PREF_LAT, String.valueOf(LocationUtils.round(lat, 2))).apply();
        MainActivity.weatherSharedPref.edit().putString(SHARED_PREF_LON, String.valueOf(LocationUtils.round(lon, 2))).apply();
        MainActivity.weatherSharedPref.edit().putString(SHARED_PREF_LAST_UPDATE, DateUtils.getTodaysDateFormat02()).apply();

        Log.d(TAG, "sharedPreferences Data Saved!");
    }

    /* Getting data from SharedPreferences */
    public static String[] getLatLonAndDate() {
        String lat = MainActivity.weatherSharedPref.getString(SHARED_PREF_LAT, "");
        String lon = MainActivity.weatherSharedPref.getString(SHARED_PREF_LON, "");
        String lastUpdate = MainActivity.weatherSharedPref.getString(SHARED_PREF_LAST_UPDATE, "");

        return new String[]{lat, lon, lastUpdate};
    }
}
