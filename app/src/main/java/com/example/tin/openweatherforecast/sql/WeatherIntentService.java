package com.example.tin.openweatherforecast.sql;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.tin.openweatherforecast.MainActivity;
import com.example.tin.openweatherforecast.models.Weather;

import java.util.ArrayList;


public class WeatherIntentService extends IntentService {

    private static final String TAG = WeatherIntentService.class.getSimpleName();

    ArrayList<Weather> mWeather;

    public WeatherIntentService() {
        super("WeatherIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {


        Log.d(TAG, "IntentService Weather Data: " + mWeather);

        if (intent != null) {

            mWeather = intent.getParcelableArrayListExtra(MainActivity.SQL_WEATHER_DATA);

            ContentValues[] weatherValues = parseWeatherArrayToCv(mWeather);
            //TODO: NOW WE NEED TO ADD THE DATA TO SQL!! & DELETE THE CURRENT DATA IF ANY!!
            //TODO: FIRST COMMIT

            Log.d(TAG, "IntentService Weather Data: " + mWeather);

        }

    }

    private ContentValues[] parseWeatherArrayToCv(ArrayList<Weather> weather) {

        /* ContentValues to save data to SQL */
        ContentValues[] weatherContentValues = new ContentValues[weather.size()];

        /* Using a for loop to cycle through each JsonObject within the listJsonArray */
        for (int i = 0; i < weather.size(); i++) {

            int unixDateTime = weather.get(i).getUnixDateTime();
            String calculateDateTime = weather.get(i).getCalculateDateTime();
            double tempCurrent = weather.get(i).getTempCurrent();
            double tempMin = weather.get(i).getTempMin();
            double tempMax = weather.get(i).getTempMax();
            String weatherDescription = weather.get(i).getWeatherDescription();
            String weatherIcon = weather.get(i).getWeatherIcon();
            double windSpeed = weather.get(i).getWindSpeed();
            double windDegree = weather.get(i).getWindDegree();

            /* Preparing data for SQLite */
            ContentValues weatherValues = new ContentValues();
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_UNIX_DATE, unixDateTime);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_CALC_DATE, calculateDateTime);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_TEMP_CURRENT, tempCurrent);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_TEMP_MIN, tempMin);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_TEMP_MAX, tempMax);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_DESC, weatherDescription);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_ICON_ID, weatherIcon);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_DEGREE, windDegree);

            weatherContentValues[i] = weatherValues;

        }

        return weatherContentValues;
    }


}
