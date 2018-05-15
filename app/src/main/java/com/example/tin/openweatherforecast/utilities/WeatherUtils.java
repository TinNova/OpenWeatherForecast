package com.example.tin.openweatherforecast.utilities;

import android.content.Context;
import android.util.Log;

import com.example.tin.openweatherforecast.R;

/*
 * Contains utilities for:
 * - Passing the correct weather Icon image
 */
public class WeatherUtils {

    private static final String TAG = WeatherUtils.class.getSimpleName();

    /*
     * Formatting temperature to a whole number
     *
     * @param context     Android Context to access preferences and resources
     * @param temperature Temperature in degrees Celsius (°C)
     *
     * @return Formatted temperature String in the following form:
     * "21°"
     */
    public static String formatTemperature(Context context, double temperature) {

        String roundedTemp = context.getString(R.string.format_temperature, temperature);

        return roundedTemp + context.getString(R.string.degrees_symbol);
    }

    public static String formatWindSpeed(Context context, double windSpeed) {

        String roundWindSpeed = context.getString(R.string.format_temperature, windSpeed);

        return context.getString(R.string.wind_intro) + " " + roundWindSpeed
                + context.getString(R.string.wind_speed_unit);
    }

    /**
     * This method uses the wind direction in degrees to determine compass direction as a
     * String. (eg NW) The method will return the wind String in the following form: "2 km/h SW"

     * @param degrees   Degrees as measured on a compass, NOT temperature degrees!
     *                  See https://www.mathsisfun.com/geometry/degrees.html
     *
     * @return Wind String in the following form: "SW"
     */
    public static String formatWindDirection(double degrees) {

        /*
         * You know what's fun? Writing really long if/else statements with tons of possible
         * conditions. Seriously, try it!
         */
        String direction = "Unknown";
        if (degrees >= 337.5 || degrees < 22.5) {
            direction = "N";
        } else if (degrees >= 22.5 && degrees < 67.5) {
            direction = "NE";
        } else if (degrees >= 67.5 && degrees < 112.5) {
            direction = "E";
        } else if (degrees >= 112.5 && degrees < 157.5) {
            direction = "SE";
        } else if (degrees >= 157.5 && degrees < 202.5) {
            direction = "S";
        } else if (degrees >= 202.5 && degrees < 247.5) {
            direction = "SW";
        } else if (degrees >= 247.5 && degrees < 292.5) {
            direction = "W";
        } else if (degrees >= 292.5 && degrees < 337.5) {
            direction = "NW";
        }

        return direction;
    }

    /*
     * Helper method that provides the small icon resource that corresponds to the icon id return by the
     * OpenWeatherMap API.
     * 
     * @param weatherIconId from OpenWeatherMap API response
     *                  See http://openweathermap.org/weather-conditions for a list of all IDs
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getSmallArtResourceIdForWeatherCondition(int weatherIconId) {

        /*
         * Based on weather code data for Open Weather Map.
         */
        if (weatherIconId >= 200 && weatherIconId <= 232) {
            return R.drawable.ic_storm;
        } else if (weatherIconId >= 300 && weatherIconId <= 321) {
            return R.drawable.ic_light_rain;
        } else if (weatherIconId >= 500 && weatherIconId <= 504) {
            return R.drawable.ic_rain;
        } else if (weatherIconId == 511) {
            return R.drawable.ic_snow;
        } else if (weatherIconId >= 520 && weatherIconId <= 531) {
            return R.drawable.ic_rain;
        } else if (weatherIconId >= 600 && weatherIconId <= 622) {
            return R.drawable.ic_snow;
        } else if (weatherIconId >= 701 && weatherIconId <= 761) {
            return R.drawable.ic_fog;
        } else if (weatherIconId == 761 || weatherIconId == 771 || weatherIconId == 781) {
            return R.drawable.ic_storm;
        } else if (weatherIconId == 800) {
            return R.drawable.ic_clear;
        } else if (weatherIconId == 801) {
            return R.drawable.ic_light_clouds;
        } else if (weatherIconId >= 802 && weatherIconId <= 804) {
            return R.drawable.ic_cloudy;
        } else if (weatherIconId >= 900 && weatherIconId <= 906) {
            return R.drawable.ic_storm;
        } else if (weatherIconId >= 958 && weatherIconId <= 962) {
            return R.drawable.ic_storm;
        } else if (weatherIconId >= 951 && weatherIconId <= 957) {
            return R.drawable.ic_clear;
        }

        Log.e(TAG, "Unknown Weather: " + weatherIconId);
        return R.drawable.ic_storm;
    }

    /*
     * Helper method that provides the large icon resource that corresponds to the icon id return by the
     * OpenWeatherMap API.
     * 
     * @param weatherIconId from OpenWeatherMap API response
     *                  See http://openweathermap.org/weather-conditions for a list of all IDs
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getLargeArtResourceIdForWeatherCondition(int weatherIconId) {

        /*
         * Based on weather code data for Open Weather Map.
         */
        if (weatherIconId >= 200 && weatherIconId <= 232) {
            return R.drawable.art_storm;
        } else if (weatherIconId >= 300 && weatherIconId <= 321) {
            return R.drawable.art_light_rain;
        } else if (weatherIconId >= 500 && weatherIconId <= 504) {
            return R.drawable.art_rain;
        } else if (weatherIconId == 511) {
            return R.drawable.art_snow;
        } else if (weatherIconId >= 520 && weatherIconId <= 531) {
            return R.drawable.art_rain;
        } else if (weatherIconId >= 600 && weatherIconId <= 622) {
            return R.drawable.art_snow;
        } else if (weatherIconId >= 701 && weatherIconId <= 761) {
            return R.drawable.art_fog;
        } else if (weatherIconId == 761 || weatherIconId == 771 || weatherIconId == 781) {
            return R.drawable.art_storm;
        } else if (weatherIconId == 800) {
            return R.drawable.art_clear;
        } else if (weatherIconId == 801) {
            return R.drawable.art_light_clouds;
        } else if (weatherIconId >= 802 && weatherIconId <= 804) {
            return R.drawable.art_clouds;
        } else if (weatherIconId >= 900 && weatherIconId <= 906) {
            return R.drawable.art_storm;
        } else if (weatherIconId >= 958 && weatherIconId <= 962) {
            return R.drawable.art_storm;
        } else if (weatherIconId >= 951 && weatherIconId <= 957) {
            return R.drawable.art_clear;
        }

        Log.e(TAG, "Unknown Weather: " + weatherIconId);
        return R.drawable.art_storm;
    }
}
