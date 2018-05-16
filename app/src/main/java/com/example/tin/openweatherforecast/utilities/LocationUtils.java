package com.example.tin.openweatherforecast.utilities;

import android.content.Context;

import com.example.tin.openweatherforecast.R;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LocationUtils {

    private static final String TAG = LocationUtils.class.getSimpleName();

    /* Rounds the latitude and longitude to a desired decimal place */

    /**
     * Rounding a lat or lon double to the nearest specified decimal place
     *
     * @param value The lat or long as a double
     * @param places An int containing the number of decimal points to round to
     *
     * @return The lat or long rounded to the determined decimal place if "places" = 2, return in
     * the form "50.00"
     */
    public static double round(double value, int places) {

        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    /**
     * Formating the lat and lon into a unified String
     *
     * @param context Android Context to access preferences and resources
     * @param latLon  String array representing the lat and lon of a location
     *
     * @return A single String that contains the lat and lon in the form "Lat 50.00, Lon -0.00"
     */
    public static String formatLatLon(Context context, String[] latLon) {

        String sharedPrefLat = latLon[0];
        String sharedPrefLon = latLon[1];

        return context.getString(R.string.latitude) + " " + sharedPrefLat + ", "
                + context.getString(R.string.longitude) + " " + sharedPrefLon;
    }
}
