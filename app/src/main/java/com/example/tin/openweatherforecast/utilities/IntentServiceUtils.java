package com.example.tin.openweatherforecast.utilities;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;

import com.example.tin.openweatherforecast.MainActivity;
import com.example.tin.openweatherforecast.models.Weather;
import com.example.tin.openweatherforecast.sql.WeatherIntentService;

import java.util.ArrayList;

import static com.example.tin.openweatherforecast.MainActivity.SQL_WEATHER_DATA;

/**
 * Created by Tin on 08/05/2018.
 */

public class IntentServiceUtils {

    /* Method which launches the WeatherIntentService */
    public static Bundle saveWeatherDataToSql(ArrayList<Weather> weather) {

        Bundle sqlIntentBundle = new Bundle();

        if (weather != null) {
            sqlIntentBundle.putParcelableArrayList(SQL_WEATHER_DATA, weather);
        }

        return sqlIntentBundle;

    }
}
