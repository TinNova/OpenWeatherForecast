package com.example.tin.openweatherforecast;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.tin.openweatherforecast.utilities.NetworkUtils;

import java.net.URL;

/**
 * Created by Tin on 03/05/2018.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            /*
             * The getUrl method will return the URL that we need to get the forecast JSON for the
             * weather. It will decide whether to create a URL based off of the latitude and
             * longitude or off of a simple location as a String.
             */
            URL weatherRequestUrl = NetworkUtils.getUrl(this);

        } catch (Exception e) {
            /* Server probably invalid */
            e.printStackTrace();
        }

    }
}

// TODO: Build NetworkUtilsKo Class
// TODO: Trigger the NetworkUtilsKo Class to retrieve data from MainActivities OnCreate
// TODO: Log the data to see if it works
// TODO: Parse the data

