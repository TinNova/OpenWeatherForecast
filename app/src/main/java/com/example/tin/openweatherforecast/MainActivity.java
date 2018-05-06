package com.example.tin.openweatherforecast;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.tin.openweatherforecast.models.Weather;
import com.example.tin.openweatherforecast.utilities.NetworkListener;
import com.example.tin.openweatherforecast.utilities.NetworkConnection;
import com.example.tin.openweatherforecast.utilities.NetworkUtils;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    /*
     * Needed for the RecyclerView
     */
    private RecyclerView mRecyclerView;
    private WeatherAdapter mWeatherAdapter;
    // Public because it is used in CompanyDetailActivity to addToDatabase
    public ArrayList<Weather> mWeather = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: Check for an internet connection first, if none, then, if SQL data is less than
        //TODO:... 24hrs old display it, else display a no data screen.
        try {
            /*
             * The getUrl method will return the URL as a String that we need to get the forecast
             * JSON for the weather.
             */
            String weatherRequestUrl = NetworkUtils.getUrl(this);

            /*
             * Use the String URL "weatherRequestUrl" to request the JSON from the server
             * and parse it
             */
            NetworkConnection.getInstance(this).getResponseFromHttpUrl(weatherRequestUrl, new NetworkListener() {
                @Override
                public void getWeatherArrayList(ArrayList<Weather> weather) {

                    Log.i(TAG, "ArrayList Weather: " + weather);

                    // This will efficiently update the adapter with new information
                    adapter.notifyDataSetChanged();

                }
            });


        } catch (Exception e) {
            /* Server probably invalid */
            e.printStackTrace();
        }

    }
}
