package com.example.tin.openweatherforecast;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.tin.openweatherforecast.Adapters.WeatherAdapter;
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
    private RecyclerView.Adapter mAdapter;
    // Public because it is used in CompanyDetailActivity to addToDatabase
    private ArrayList<Weather> mWeather;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Creating The RecyclerView */
        // This will be used to attach the RecyclerView to the MovieAdapter
        mRecyclerView = (RecyclerView) findViewById(R.id.rV_weatherList);
        // This will improve performance by stating that changes in the content will not change
        // the child layout size in the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        /*
         * A LayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView as well as determining the policy for when to recycle item views that
         * are no longer visible to the user.
         */
        LinearLayoutManager mLinearLayoutManager =
                new LinearLayoutManager(this);
        // Set the mRecyclerView to the layoutManager so it can handle the positioning of the items
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        // Creating the mTheCompanies ArrayList<> to avoid a null exception
        mWeather = new ArrayList<>();

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
                    // Logging the weather ArrayList to see if it's functioning
                    Log.i(TAG, "ArrayList Weather: " + weather);

                    connectAdapterToWeatherData(weather);

                    /*
                    * Used to update the adapter when information is there already, for example
                    * if the SQLite data present, and user conencts to wifi, we want the latest
                    * data to overwrite the SQLite data in the adapter and recyclerView
                    */
                    //mAdapter.notifyDataSetChanged();

                }
            });

        } catch (Exception e) {
            /* Server probably invalid */
            e.printStackTrace();
        }
    }


    public void connectAdapterToWeatherData(ArrayList<Weather> arrayList) {

        mAdapter = new WeatherAdapter(arrayList, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

    }
}
