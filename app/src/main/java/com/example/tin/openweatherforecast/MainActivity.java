package com.example.tin.openweatherforecast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tin.openweatherforecast.adapters.WeatherAdapter;
import com.example.tin.openweatherforecast.models.Weather;
import com.example.tin.openweatherforecast.sql.WeatherContract;
import com.example.tin.openweatherforecast.sql.WeatherIntentService;
import com.example.tin.openweatherforecast.utilities.DateUtils;
import com.example.tin.openweatherforecast.utilities.IntentServiceUtils;
import com.example.tin.openweatherforecast.utilities.NetworkListener;
import com.example.tin.openweatherforecast.utilities.NetworkConnection;
import com.example.tin.openweatherforecast.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();

    /* Context required to launch IntentService from a non-Activity class */
    Context context;

    /* Constant for logging and referring to a unique loader */
    private static final int WEATHER_LOADER_ID = 99;
    /*
     * 0 = the SQL Loader has never run before, 1 = it has run before, therefore it needs to be reset
     * before running it again
     */
    private int loaderCreated = 0;

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
    String UPDATED;

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
    TextView tvLastDataUpdated;
    ImageView ivTodayIcon;
    Button btnRefreshData;

    ConstraintLayout mWeatherUi;
    ProgressBar mLoadingIndicator;
    TextView tvNoData;

    ImageView ivUpdate;

    private LocationManager locationManager;
    private LocationListener locationListener;
    Location location;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        /* Buttons */
        btnRefreshData = findViewById(R.id.bt_refresh);
        ivUpdate = (ImageView) findViewById(R.id.iV_updateData);


        btnRefreshData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* If the connManager and networkInfo is NOT null, start the login() method */
                if (mConnectionManager != null)
                    mNetworkInfo = mConnectionManager.getActiveNetworkInfo();
                if (mNetworkInfo != null && mNetworkInfo.isConnected()) {

                    showLoading();
                    getLonLat();

                } else {

                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

                }
            }
        });


        ivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* If the connManager and networkInfo is NOT null, start the login() method */
                if (mConnectionManager != null)
                    mNetworkInfo = mConnectionManager.getActiveNetworkInfo();
                if (mNetworkInfo != null && mNetworkInfo.isConnected()) {


                    getLonLat();
                } else {

                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

                }

            }
        });

        mWeatherUi = findViewById(R.id.l_weatherUi);
        mLoadingIndicator = findViewById(R.id.pB_loading_indicator);
        tvNoData = findViewById(R.id.tV_noData);
        tvTodayDate = findViewById(R.id.tV_todayDate);
        tvTodayTemp = findViewById(R.id.tV_todayTemp);
        tvTodayDescription = findViewById(R.id.tV_todayDescription);
        tvTodayWindSpeed = findViewById(R.id.tV_todayWindSpeed);
        tvTodayWindDirection = findViewById(R.id.tV_todayWindDirection);
        ivTodayIcon = findViewById(R.id.iV_todayIcon);
        tvLastDataUpdated = findViewById(R.id.tV_lastUpdate);

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

        /* The celsius degree symbol */
        DEGREE_SYMBOL = getString(R.string.degrees_symbol);

        /* Shows loading screen and hides the UI that contains weather data */
        showLoading();

        // Checking If The Device Is Connected To The Internet
        mConnectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);


        // If the connManager and networkInfo is NOT null, start the login() method
        if (mConnectionManager != null)
            mNetworkInfo = mConnectionManager.getActiveNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isConnected()) {

            getLonLat();

        } else {

            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();

            /* If an instance of the loader already exists, restart it before loading the SQL data */
            if (loaderCreated == 1) {

                getSupportLoaderManager().restartLoader(WEATHER_LOADER_ID, null, this);
            }

            /* Start loading the SQL data */
            getSupportLoaderManager().initLoader(WEATHER_LOADER_ID, null, this);

        }
    }

    private void updateLocation(Location location) {
        Log.d(TAG, "Lat: " + location.getLatitude());
        Log.d(TAG, "Lon: " + location.getLongitude());
        Log.d(TAG, "updateLocation CODE RAN ");

        Double lon = location.getLongitude();
        Double lat = location.getLatitude();

        getData(lon, lat);


    }

    /* Code that runs when Permission to use Location has been granted */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "CODE RAN Allowed Access to Location");

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateLocation(location);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLonLat() {

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            /* Called whenever the location is updated */
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "Location: " + location.toString());
                Log.d(TAG, "Lon: " + location.getLongitude());
                Log.d(TAG, "Lat: " + location.getLatitude());
                Log.d(TAG, "CODE RAN");

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            /* Called when the gps on the device is turned off */
            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        /* If build version is less than Marshmallow skip permission check */
        if (Build.VERSION.SDK_INT < 23) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateLocation(location);

        } else {


        /* if app denied us permission from getting the location */
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            /* Ask user for permission to access location */

                Log.d(TAG, "CODE RAN Refused Access to Location");

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            /* We already have permission, so get the devices location */
            } else {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateLocation(location);
            }
        }
    }

    private void getData(Double lon, Double lat) {

        try {
            /*
             * The getUrl method will return the URL as a String that we need to get the forecast
             * JSON for the weather.
             */
            String weatherRequestUrl = NetworkUtils.getUrl(this, lon, lat);

            /*
             * Use the String URL "weatherRequestUrl" to request the JSON from the server
             * and parse it
             */
            NetworkConnection.getInstance(this).getResponseFromHttpUrl(weatherRequestUrl, new NetworkListener() {
                @Override
                public void getWeatherArrayList(ArrayList<Weather> weather) {
                    /* Logging the weather ArrayList to see if it's functioning */
                    Log.i(TAG, "ArrayList Weather: " + weather);

                    populateTodaysDate(weather);

                    //TODO: Launch this from NetworkConnection, and put as much of it into IntentServiceUtils
                    // TIP: You'll need to pass this activities context within getResponseFromHttpUrl();
                    /* Save Weather ContentValues to Bundle */
                    Bundle weatherDataBundle = IntentServiceUtils.saveWeatherDataToSql(weather);
                    /* Send Bundle to the SqlIntentService to be saved in SQLite */
                    Intent saveSqlIntent = new Intent(MainActivity.this, WeatherIntentService.class);

                    saveSqlIntent.putExtras(weatherDataBundle);
                    startService(saveSqlIntent);

                }
            });

        } catch (Exception e) {
            /* Server probably invalid */
            e.printStackTrace();
        }
    }

    /*
     * The columns of data that we are interested in displaying within our MainActivity's list of
     * weather data.
     */
    public static final String[] WEATHER_FORECAST_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_CALC_DATE,
            WeatherContract.WeatherEntry.COLUMN_TEMP_CURRENT,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_DESC,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_WIND_DEGREE,

    };


    /*
     * Loader which displays weather data saved in SQL database
     *
     * @param loaderId The loader ID for which we need to create a loader
     * @param bundle   Any arguments supplied by the caller
     * @return A new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mSqlWeatherData = null;

            @Override
            protected void onStartLoading() {
                if (mSqlWeatherData != null) {
                    /* Delivers any previously loaded data immediately */
                    deliverResult(mSqlWeatherData);
                } else {
                    /* Force a new load */
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public Cursor loadInBackground() {

                Log.d(TAG, "loadInBackground");

                try {
                    /* This returns every column in every row in ascending order by UnixTimeDate */
                    return getContentResolver().query(
                            WeatherContract.WeatherEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            WeatherContract.WeatherEntry.COLUMN_UNIX_DATE + " ASC"
                    );
                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mSqlWeatherData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        /* Here We Are Accessing The SQLite Query We Received In The Method getSqlCompanies() Which Is Set To Read All Rows
         * We're Going Through Each Row With A For Loop And Putting Them Into Our FavouriteMovie Model
         *
         * @param cursor
         */
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            for (int count = 0; count < data.getCount(); count++) {

                Weather weather = new Weather(

                        data.getInt(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_UNIX_DATE)),
                        data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_CALC_DATE)),
                        data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TEMP_CURRENT)),
                        data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TEMP_MIN)),
                        data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TEMP_MAX)),
                        /*
                         * Below should be the weatherTitle, but it hasn't been saved to SQL so
                         * weatherDescription is being used again
                         */
                        data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_DESC)),
                        data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_DESC)),
                        data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_ICON_ID)),
                        data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED)),
                        data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_DEGREE))

                );

                Log.d(TAG, "Row_Id" + data.getLong(data.getColumnIndex(WeatherContract.WeatherEntry._ID)));

                mWeather.add(weather);

                Log.d(TAG, "DATA LOADED BY onLoadFinished: " + mWeather);

                data.moveToNext();
            }

            /* The last time SQL was updated and the current time in Unix */
            int unixTimeDate = mWeather.get(0).getUnixDateTime();
            int dateNowUnix = DateUtils.getTodaysDateInUnix();

            /* If the last update was more than 24hrs ago */
            if (dateNowUnix - unixTimeDate >= 1440 * 60 * 1000) {

                /* Delete data from SQL as it's older than 24hrs */
                showNoDataScreen();
                /* Data in SQL is over 24hrs and there's no internet, show no data screen */
                deleteWeatherData();

            } else {

                /* if Adapter is not empty, update the adapter */
                if (mAdapter != null) {

                    // Update the adapter with the new list
                    mAdapter.notifyDataSetChanged();
                    showWeatherDataView();

                } else {

                    populateTodaysDate(mWeather);

                }

                loaderCreated = 1;


            }

            /* Cursor is empty and there is no internet connection, show no data screen */
        } else {

            showNoDataScreen();
            Log.v(TAG, "cursor is Empty");

        }

        assert data != null;
        data.close();
    }

    /*
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    /* Helper Class that populates today's feature date and passes weather ArrayList to WeatherAdapter */
    private void populateTodaysDate(ArrayList<Weather> weather) {

        WIND_INTRO = getString(R.string.wind_intro);
        WIND_UNIT = getString(R.string.wind_speed_unit);
        DEGREE_SYMBOL = getString(R.string.degrees_symbol);
        UPDATED = getString(R.string.last_update);

                    /* Populating the current times weather */
        tvTodayDate.setText(weather.get(0).getCalculateDateTime());
        tvTodayTemp.setText((String.valueOf(weather.get(0).getTempCurrent() + DEGREE_SYMBOL)));
        tvTodayDescription.setText(weather.get(0).getWeatherDescription());
        tvTodayWindSpeed.setText((String.valueOf(WIND_INTRO + weather.get(0).getWindSpeed() + WIND_UNIT)));
        tvTodayWindDirection.setText((String.valueOf(weather.get(0).getWindDegree())));
        tvLastDataUpdated.setText(UPDATED + " " + DateUtils.getTodaysDateMonthHourMinute());

        Picasso.with(MainActivity.this).load(weather.get(0).getWeatherIcon())
                .into(ivTodayIcon);

        /*
         * Connecting the weather ArrayList to the Adapter, and the Adapter to the
         * RecyclerView
         */
        mAdapter = new WeatherAdapter(weather, getApplicationContext(), DEGREE_SYMBOL);
        mRecyclerView.setAdapter(mAdapter);

        showWeatherDataView();

    }

    /*
     * Method which deletes the data from the SQLite database
     * - It takes a long as the input which is the ID of the Row
     * - It returns a boolean to say if the deletion was successful or not
     */
    private void deleteWeatherData() {

        /* This is the URI required to delete all of the data */
        Uri uri = WeatherContract.WeatherEntry.CONTENT_URI;

        getContentResolver().delete(uri, null, null);

        Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        Log.d(TAG, "REMOVE: " + getBaseContext() + uri.toString());
    }

    /* Show Loading Indicator / Hide Weather Data */
    private void showLoading() {
        /* Hide the weather data UI */
        mWeatherUi.setVisibility(View.INVISIBLE);
        /* Show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    /* Hide Loading Indicator / Show Weather Data */
    private void showWeatherDataView() {
        /* Hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Hide the No Data Text */
        tvNoData.setVisibility(View.INVISIBLE);
        /* Hide refresh button */
        btnRefreshData.setVisibility(View.INVISIBLE);
        /* Show the weather data UI */
        mWeatherUi.setVisibility(View.VISIBLE);
    }

    /* Show the No Data Screen / Hide Weather Data Screen */
    private void showNoDataScreen() {
        /* Hide the weather data UI */
        mWeatherUi.setVisibility(View.INVISIBLE);
        /* Hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Show refresh button */
        btnRefreshData.setVisibility(View.VISIBLE);
        /* Show the No Data Text */
        tvNoData.setVisibility(View.VISIBLE);

    }

}
