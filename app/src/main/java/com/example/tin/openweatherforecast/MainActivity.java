package com.example.tin.openweatherforecast;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tin.openweatherforecast.Adapters.WeatherAdapter;
import com.example.tin.openweatherforecast.models.Weather;
import com.example.tin.openweatherforecast.utilities.NetworkListener;
import com.example.tin.openweatherforecast.utilities.NetworkConnection;
import com.example.tin.openweatherforecast.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    /*
     * Needed to make the wind speed more readable for users
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

                    WIND_INTRO = getString(R.string.wind_intro);
                    WIND_UNIT = getString(R.string.wind_speed_unit);
                    DEGREE_SYMBOL = getString(R.string.degrees_symbol);

                    // Populating the current times weather
                    tvTodayDate.setText(weather.get(0).getCalculateDateTime());
                    tvTodayTemp.setText((String.valueOf(weather.get(0).getTempCurrent() + DEGREE_SYMBOL)));
                    tvTodayDescription.setText(weather.get(0).getWeatherDescription());
                    tvTodayWindSpeed.setText((String.valueOf(WIND_INTRO + weather.get(0).getWindSpeed() + WIND_UNIT)));
                    tvTodayWindDirection.setText((String.valueOf(weather.get(0).getWindDegree())));

                    Picasso.with(MainActivity.this).load(weather.get(0).getWeatherIcon())
                            .into(ivTodayIcon);

                    // Connecting the weather ArrayList to the Adapter, and the Adapter to the
                    // RecyclerView
                    mAdapter = new WeatherAdapter(weather, getApplicationContext(), DEGREE_SYMBOL);
                    mRecyclerView.setAdapter(mAdapter);


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
