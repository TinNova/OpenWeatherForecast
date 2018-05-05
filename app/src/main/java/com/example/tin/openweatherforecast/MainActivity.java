package com.example.tin.openweatherforecast;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.tin.openweatherforecast.utilities.NetworkListener;
import com.example.tin.openweatherforecast.utilities.NetworkConnection;
import com.example.tin.openweatherforecast.utilities.NetworkUtils;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();


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
             * Use the String URL "weatherRequestUrl" to retrieve the JSON
             */
            NetworkConnection.getInstance(this).getResponseFromHttpUrl(weatherRequestUrl, new NetworkListener() {
                @Override
                public void getResult(String string) {

                    Log.i(TAG, "JSON Response: " + string);

                }
            });

            //TODO: Create Class to Parse the Response

        } catch (Exception e) {
            /* Server probably invalid */
            e.printStackTrace();
        }

    }
}

