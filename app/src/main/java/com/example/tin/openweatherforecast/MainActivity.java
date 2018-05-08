package com.example.tin.openweatherforecast;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tin.openweatherforecast.adapters.WeatherAdapter;
import com.example.tin.openweatherforecast.models.Weather;
import com.example.tin.openweatherforecast.sql.WeatherIntentService;
import com.example.tin.openweatherforecast.utilities.IntentServiceUtils;
import com.example.tin.openweatherforecast.utilities.NetworkListener;
import com.example.tin.openweatherforecast.utilities.NetworkConnection;
import com.example.tin.openweatherforecast.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    /* Context required to launch IntentService from a non-Activity class */
    Context context;


    /* Strings for the SQL Intent Service */
    public static final String SQL_WEATHER_DATA = "sql_weather_data";

    // Used to check if the device has internet connection
    private ConnectivityManager mConnectionManager;
    private NetworkInfo mNetworkInfo;

    /*
     * Needed to make the wind speed more readable for users in UI
     */
    String WIND_INTRO;
    String WIND_UNIT;
    String DEGREE_SYMBOL;

    /*
     * Needed for the RecyclerView
     */
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Weather> mWeather;

    /*
     * TextViews used to populate the current times weather
     */
    TextView tvTodayDate;
    TextView tvTodayTemp;
    TextView tvTodayDescription;
    TextView tvTodayWindSpeed;
    TextView tvTodayWindDirection;
    ImageView ivTodayIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        tvTodayDate = findViewById(R.id.tV_todayDate);
        tvTodayTemp = findViewById(R.id.tV_todayTemp);
        tvTodayDescription = findViewById(R.id.tV_todayDescription);
        tvTodayWindSpeed = findViewById(R.id.tV_todayWindSpeed);
        tvTodayWindDirection = findViewById(R.id.tV_todayWindDirection);
        ivTodayIcon = findViewById(R.id.iV_todayIcon);

        /* Creating The RecyclerView */
        // This will be used to attach the RecyclerView to the MovieAdapter
        mRecyclerView = findViewById(R.id.rV_weatherList);
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

        // Checking If The Device Is Connected To The Internet
        mConnectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // If the connManager and networkInfo is NOT null, start the login() method
        if (mConnectionManager != null)
            mNetworkInfo = mConnectionManager.getActiveNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isConnected()) {

            getData();

        } else {

            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            //TODO: Check if data exists and is under 24hrs old, if Yes == show SQL data, else show no data screen
            //TODO: Query for SQL data have an if statement that checks the date

        }
    }

    private void getData() {

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
                public void getWeatherArrayList(ArrayList<Weather> weather, ContentValues[] cv) {
                    /* Logging the weather ArrayList to see if it's functioning */
                    Log.i(TAG, "ArrayList Weather: " + weather);

                    WIND_INTRO = getString(R.string.wind_intro);
                    WIND_UNIT = getString(R.string.wind_speed_unit);
                    DEGREE_SYMBOL = getString(R.string.degrees_symbol);

                    /* Populating the current times weather */
                    tvTodayDate.setText(weather.get(0).getCalculateDateTime());
                    tvTodayTemp.setText((String.valueOf(weather.get(0).getTempCurrent() + DEGREE_SYMBOL)));
                    tvTodayDescription.setText(weather.get(0).getWeatherDescription());
                    tvTodayWindSpeed.setText((String.valueOf(WIND_INTRO + weather.get(0).getWindSpeed() + WIND_UNIT)));
                    tvTodayWindDirection.setText((String.valueOf(weather.get(0).getWindDegree())));

                    Picasso.with(MainActivity.this).load(weather.get(0).getWeatherIcon())
                            .into(ivTodayIcon);

                    /*
                     * Connecting the weather ArrayList to the Adapter, and the Adapter to the
                     * RecyclerView
                     */
                    mAdapter = new WeatherAdapter(weather, getApplicationContext(), DEGREE_SYMBOL);
                    mRecyclerView.setAdapter(mAdapter);

                    //TODO: Launch this from NetworkConnection, and put as much of it into IntentServiceUtils
                    // TIP: You'll need to pass this activities context within getResponseFromHttpUrl();
                    /* Save Weather ContentValues to Bundle */
                    Bundle weatherDataBundle = IntentServiceUtils.saveWeatherDataToSql(weather);
                    /* Send Bundle to the SqlIntentService to be saved in SQLite */
                    Intent saveSqlIntent = new Intent(MainActivity.this, WeatherIntentService.class);

                    saveSqlIntent.putExtras(weatherDataBundle);
                    startService(saveSqlIntent);



                    //TODO: Here delete old data from SQL and save new data

                    //TODO: DO THIS NEXT AFTER THE PARK!!!!!
                    /**
                     * if networkConnection == true {download data, then save to SQL within WeatherIntentService}
                     * else if networkConnection == false {display no internet msg, and load SQL}
                     * else if SQL data not existent or older than 24hrs {show no data screen}
                     *
                     *
                     *
                     *
                     *
                     *
                     *
                     *
                     *
                     *
                     */
                    /*
                    * Used to update the adapter when information is there already, for example
                    * if the SQLite data present, and user conencts to wifi, we want the latest
                    * data to overwrite the SQLite data in the adapter and recyclerView
                    */

                }
            });

        } catch (Exception e) {
            /* Server probably invalid */
            e.printStackTrace();
        }
    }


}
