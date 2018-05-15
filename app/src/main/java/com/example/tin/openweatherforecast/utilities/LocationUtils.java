package com.example.tin.openweatherforecast.utilities;

import android.content.Context;

import com.example.tin.openweatherforecast.R;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LocationUtils {

    private static final String TAG = LocationUtils.class.getSimpleName();

    /* Rounds the latitude and longitude to a desired decimal place */
    public static double round(double value, int places) {

        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /* Formatting the lat and lon */
    public static String formatLatLon(Context context, String[] latLon) {

        String sharedPrefLat = latLon[0];
        String sharedPrefLon = latLon[1];

        return context.getString(R.string.latitude) + " " + sharedPrefLat + ", "
                + context.getString(R.string.longitude) + " " + sharedPrefLon;
    }
}
