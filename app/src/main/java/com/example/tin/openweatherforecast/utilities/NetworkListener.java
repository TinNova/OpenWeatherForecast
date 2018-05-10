package com.example.tin.openweatherforecast.utilities;

import android.content.ContentValues;

import com.example.tin.openweatherforecast.models.Weather;

import java.util.ArrayList;

public interface NetworkListener {

    void getWeatherArrayList (ArrayList<Weather> weather);
}
