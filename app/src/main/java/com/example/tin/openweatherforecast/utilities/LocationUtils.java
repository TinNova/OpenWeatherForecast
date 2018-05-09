package com.example.tin.openweatherforecast.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Tin on 09/05/2018.
 */

public class LocationUtils {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
