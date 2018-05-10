package com.example.tin.openweatherforecast.utilities;

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
}
