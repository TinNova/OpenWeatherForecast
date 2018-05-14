package com.example.tin.openweatherforecast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.tin.openweatherforecast.data.WeatherCursorLoader;
import com.example.tin.openweatherforecast.data.WeatherSharedPreferencesHelper;
import com.example.tin.openweatherforecast.models.Weather;
import com.example.tin.openweatherforecast.data.WeatherContract;
import com.example.tin.openweatherforecast.data.WeatherIntentService;
import com.example.tin.openweatherforecast.utilities.DateUtils;
import com.example.tin.openweatherforecast.utilities.IntentServiceUtils;
import com.example.tin.openweatherforecast.utilities.LocationUtils;
import com.example.tin.openweatherforecast.utilities.NetworkListener;
import com.example.tin.openweatherforecast.utilities.NetworkConnection;
import com.example.tin.openweatherforecast.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.tin.openweatherforecast.utilities.WeatherDisplayUtils.getLargeArtResourceIdForWeatherCondition;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();

    /* ID that is responsible for identifying the loader that loads the Weather data */
    private static final int WEATHER_LOADER_ID = 99;

    /* Key to retrieve data saved within onSavedInstantState */
    private final String SAVED_INSTANT_STATE_KEY = "saved_instant_state_key";

    /*
     * Int representing whether the loader that loads the Weather data has run previously or not
     * 0 = loader has never run before,
     * 1 = loader has run before, therefore it needs to be reset before running it again
     */
    private int loaderCreated = 0;

    /* Strings for the SQL Intent Service */
    public static final String SQL_WEATHER_DATA = "sql_weather_data";

    /* Used to check if the device is connected to the internet before creating a connection */
    private ConnectivityManager mConnectionManager;
    private NetworkInfo mNetworkInfo;

    /* Request Location Updates values */
    private final int LOCATION_UPDATE_TIME = 100000;
    private final int LOCATION_UPDATE_DISTANCE = 100000;

    /* If codes returns this value as GPS lat/lon there was an error getting the devices location */
    private final double UPDATE_LOCATION_ERROR = 200.000;

    /* When data is pulled from database, a lat and lon is pulled from SharedPreference */
    double LAT_LON_IRRELEVANT = 200.000;


    /* Used to get the devices location */
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location location;

    /*
     * These strings are used to make the UI more attractive and readable for users.
     */
    private String WIND_INTRO;
    private String WIND_UNIT;
    private String DEGREE_SYMBOL;
    private String UPDATED;
    private String LATITUDE;
    private String LONGITUDE;

    /*
     * Needed to populate the Adapter and the RecyclerView
     */
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Weather> mWeather;

    /*
     * Views within the code
     */
    private TextView tvTodayDate;
    private TextView tvTodayTemp;
    private TextView tvTodayDescription;
    private TextView tvTodayWindSpeed;
    private TextView tvTodayWindDirection;
    private TextView tvLastDataUpdated;
    private TextView tvLocation;
    private ImageView ivTodayIcon;
    private Button btnRefreshData;
    private ConstraintLayout mWeatherUi;
    private ProgressBar mLoadingIndicator;
    private TextView tvNoData;
    private ImageView ivUpdate;

    /* Reference to SharedPreferences */
    public static SharedPreferences weatherSharedPref;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Getting an instance of the weatherSharedPref */
        weatherSharedPref = getSharedPreferences(getPackageName() + "_preferences", MODE_PRIVATE);

        /* Getting an instance of the locationManager */
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        /* Used to check if the device is connected to the internet */
        mConnectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        /* Initialising all of the views */
        btnRefreshData = findViewById(R.id.bt_refresh);
        ivUpdate = findViewById(R.id.iV_updateData);
        mWeatherUi = findViewById(R.id.l_weatherUi);
        mLoadingIndicator = findViewById(R.id.pB_loading_indicator);
        tvNoData = findViewById(R.id.tV_noData);
        tvTodayDate = findViewById(R.id.tV_todayDate);
        tvTodayTemp = findViewById(R.id.tV_todayTemp);
        tvTodayDescription = findViewById(R.id.tV_todayDescription);
        tvTodayWindSpeed = findViewById(R.id.tV_todayWindSpeed);
        tvTodayWindDirection = findViewById(R.id.tV_todayWindDirection);
        tvLocation = findViewById(R.id.tV_lastLocation);
        ivTodayIcon = findViewById(R.id.iV_todayIcon);
        tvLastDataUpdated = findViewById(R.id.tV_lastUpdate);

        /* The celsius degree symbol */
        DEGREE_SYMBOL = getString(R.string.degrees_symbol);

        /*
         * Creating The RecyclerView,
         * This will be used to attach the RecyclerView to the MovieAdapter
         */
        mRecyclerView = findViewById(R.id.rV_weatherList);

        /*
         * This will improve performance by stating that changes in the content will not change
         * the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        /*
         * A LayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView as well as determining the policy for when to recycle item views that
         * are no longer visible to the user.
         */
        LinearLayoutManager mLinearLayoutManager =
                new LinearLayoutManager(this);

        /* Set the mRecyclerView to the layoutManager */
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        /* Initialising the mWeather ArrayList<> to avoid a null exception */
        mWeather = new ArrayList<>();

        /* If There isn't a savedInstanceState, Download The Data And Build The RecyclerView */
        if (savedInstanceState == null) {

            /*
             * This method either retrieves data from the OpenWeatherApi or from the SQL database
             * based on whether or not the device has access to the internet and location services.
             */
            downloadResponseOrDisplaySqlData();
        } else {

            /* Retrieve the mWeather ArrayList from onSavedInstanceState */
            mWeather = savedInstanceState.getParcelableArrayList(SAVED_INSTANT_STATE_KEY);

            /* Pass the mWeather ArrayList to the adapter */
            mAdapter = new WeatherAdapter(mWeather, getApplicationContext(), DEGREE_SYMBOL);
            mRecyclerView.setAdapter(mAdapter);

            /* If the connManager and networkInfo is NOT null, start the login() method */
            if (mConnectionManager != null)
                mNetworkInfo = mConnectionManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isConnected()) {

                /* if GPS is enabled */
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    downloadResponseOrDisplaySqlData();
                }

            } else {

                populateTodaysDate(mWeather);
            }
        }

        /* Button used to refresh the weather data */
        btnRefreshData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* If the connManager and networkInfo is NOT null, start the login() method */
                if (mConnectionManager != null)
                    mNetworkInfo = mConnectionManager.getActiveNetworkInfo();
                if (mNetworkInfo != null && mNetworkInfo.isConnected()) {

                    /* if GPS is enabled */
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                        downloadResponseOrDisplaySqlData();
                    } else {

                        Toast.makeText(MainActivity.this, getString(R.string.no_gps), Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });

        /* ImageView with onClickListener used to update the weather data */
        ivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* If the connManager and networkInfo is NOT null, start the login() method */
                if (mConnectionManager != null)
                    mNetworkInfo = mConnectionManager.getActiveNetworkInfo();
                if (mNetworkInfo != null && mNetworkInfo.isConnected()) {

                    /* if GPS is enabled */
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                        downloadResponseOrDisplaySqlData();
                    } else {

                        Toast.makeText(MainActivity.this, getString(R.string.no_gps), Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /* Requesting the latitude and longitude of the device */
    @SuppressLint("MissingPermission")
    private double[] getLonLat() {

        double[] deviceLocation = new double[0];

        /* if GPS is not enabled, tell user, and display SQL data */
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            /* If an instance of the loader already exists, restart it before loading the SQL data */
            if (loaderCreated == 1) {

                getSupportLoaderManager().restartLoader(WEATHER_LOADER_ID, null, this);
            }

            /* Start loading the SQL data */
            getSupportLoaderManager().initLoader(WEATHER_LOADER_ID, null, this);
        }

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

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_TIME, LOCATION_UPDATE_DISTANCE, locationListener);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            deviceLocation = updateLocation(location);
        } else {

            /* if app denied us permission from getting the location */
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                /* Ask user for permission to access location */
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                /* We already have permission, so get the devices location */
            } else {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_TIME, LOCATION_UPDATE_DISTANCE, locationListener);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                deviceLocation = updateLocation(location);
            }
        }

        return (deviceLocation);
    }

    /* Code that runs when Permission to use Location has been granted */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "CODE RAN Allowed Access to Location");

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_TIME, LOCATION_UPDATE_DISTANCE, locationListener);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateLocation(location);
            }
        }
    }

    /* Required in order to retrieve the devices location on OnCreate */
    private double[] updateLocation(Location location) {

        if (location != null) {
            double updateLocationLat = location.getLatitude();
            double updateLocationLon = location.getLongitude();

            return new double[]{updateLocationLat, updateLocationLon};
        } else {

            /* Return 200.000 if the location could not be obtained to avoid a null pointer exception */
            return new double[]{UPDATE_LOCATION_ERROR, UPDATE_LOCATION_ERROR};
        }
    }

    /* Getting data from the OpenWeatherMap Api */
    private void getData(final double lat, final double lon) {

        try {
            /*
             * The getUrl method will return the URL as a String that we need to get the forecast
             * JSON for the weather.
             */
            String weatherRequestUrl = NetworkUtils.getUrl(lat, lon);

            /*
             * Use the String URL "weatherRequestUrl" to request the JSON from the server
             * and parse it
             */
            NetworkConnection.getInstance(this).getResponseFromHttpUrl(weatherRequestUrl, new NetworkListener() {
                @Override
                public void getWeatherArrayList(ArrayList<Weather> weather) {
                    /* Logging the weather ArrayList to see if it's functioning */
                    Log.i(TAG, "ArrayList Weather: " + weather);

                    //TODO: Save data directly to SQL. Then display data directly from SQL, Never display from API
                    //TODO: 1. In Method That Parses API, Save All To SQLite Including WeatherIcon ID
                    //TODO: 2. In Adapter When Populating Image, Insert One From The RES Folder
                    //TODO: Experiment with the Room API, it could be good

                    /* Save Weather ContentValues to Bundle */
                    Bundle weatherDataBundle = IntentServiceUtils.saveWeatherDataToSql(weather);
                    /* Send Bundle to the SqlIntentService to be saved in SQLite */
                    Intent saveSqlIntent = new Intent(MainActivity.this, WeatherIntentService.class);

                    saveSqlIntent.putExtras(weatherDataBundle);
                    startService(saveSqlIntent);

                    /* dataType, SQL = 1, API = 2 */
                    populateTodaysDate(weather);
                }
            });

            /*
             * Save the latitude, longitude to SharedPreferences.
             * The date at which the data was updated is also save within this class.
             */
            WeatherSharedPreferencesHelper.setLatLonAndDate(lat, lon);

        } catch (Exception e) {
            /* Server probably invalid */
            e.printStackTrace();
        }
    }

    /*
     * Loader which displays weather data saved in SQL database
     *
     * @param loaderId The loader ID for which we need to create a loader
     * @param bundle   Any arguments supplied by the caller
     * @return A new Loader instance that is ready to start loading.
     */
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new WeatherCursorLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
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
                        data.getInt(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID)),
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

                    /* Update the adapter with the new list */
                    mAdapter.notifyDataSetChanged();
                    showWeatherDataView();
                } else {

                    /* dataType, SQL = 1, API = 2 */
                    populateTodaysDate(mWeather);
                }

                /* Set int to 1 to indicate an instance of a Loader has been created */
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

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }


    /* Helper Class that populates today's feature date and passes weather ArrayList to WeatherAdapter */
    private void populateTodaysDate(ArrayList<Weather> weather) {

        WIND_INTRO = getString(R.string.wind_intro);
        WIND_UNIT = getString(R.string.wind_speed_unit);
        DEGREE_SYMBOL = getString(R.string.degrees_symbol);
        UPDATED = getString(R.string.last_update);
        LATITUDE = getString(R.string.latitude);
        LONGITUDE = getString(R.string.longitude);

        /*
         * Get the Latitude, Longitude and time of Update from SharedPreferences
         */
        String[] sharedPrefLatLonArray = WeatherSharedPreferencesHelper.getLatLonAndDate();
        String sharedPrefLat = sharedPrefLatLonArray[0];
        String sharedPrefLon = sharedPrefLatLonArray[1];
        String sharedPrefLastUpdate = sharedPrefLatLonArray[2];
        tvLocation.setText((LATITUDE + " " + sharedPrefLat + ", " + LONGITUDE + " " + sharedPrefLon));
        tvLastDataUpdated.setText(sharedPrefLastUpdate);

        /* Populating the current times weather */
        tvTodayDate.setText(weather.get(0).getCalculateDateTime());
        tvTodayTemp.setText((String.valueOf(weather.get(0).getTempCurrent() + DEGREE_SYMBOL)));
        tvTodayDescription.setText(weather.get(0).getWeatherDescription());
        tvTodayWindSpeed.setText((String.valueOf(WIND_INTRO + weather.get(0).getWindSpeed() + WIND_UNIT)));
        tvTodayWindDirection.setText((String.valueOf(weather.get(0).getWindDegree())));

        int largeIconResourceId = getLargeArtResourceIdForWeatherCondition(weather.get(0).getWeatherId());

        Picasso.with(MainActivity.this).load(largeIconResourceId)
                .into(ivTodayIcon);

        mAdapter = new WeatherAdapter(weather, getApplicationContext(), DEGREE_SYMBOL);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();

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

    /*
     * This code checks if there is an internet connection and whether the devices location
     * is attainable.
     *
     * If both are true, then the code will create the OpenWeatherMap url and download the
     * response.
     *
     * If either are false, then the code will display the data available in the SQLite
     * database.
     *
     * If the data within the database is was last updated over 24 hours ago or if there isn't
     * any data saved, it will display a no data screen.
     */
    private void downloadResponseOrDisplaySqlData() {

        /* If the connManager and networkInfo is NOT null */
        if (mConnectionManager != null)
            mNetworkInfo = mConnectionManager.getActiveNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isConnected()) {

            /* if GPS is enabled */
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                /* Shows loading screen and hides the UI that contains weather data */
                showLoading();

                /* Requesting the Latitude and Longitude of the device */
                double[] deviceLocationArray = getLonLat();

                /* If there wasn't an error in getting the lat/lon, continue else, launch the SQL data */
                if (!Objects.equals(deviceLocationArray[0], UPDATE_LOCATION_ERROR)) {
                    double deviceLat = deviceLocationArray[0];
                    double deviceLon = deviceLocationArray[1];

                    getData(deviceLat, deviceLon);
                } else {

                    Toast.makeText(MainActivity.this, getString(R.string.no_gps), Toast.LENGTH_SHORT).show();
                    displaySqlData();

                }
            } else {

                Toast.makeText(MainActivity.this, getString(R.string.no_gps), Toast.LENGTH_SHORT).show();
                displaySqlData();

            }
        } else {

            Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            displaySqlData();
        }
    }

    private void displaySqlData() {

        /* If an instance of the loader already exists, restart it before loading the SQL data */
        if (loaderCreated == 1) {

            getSupportLoaderManager().restartLoader(WEATHER_LOADER_ID, null, MainActivity.this);
        }

            /* Start loading the SQL data */
        getSupportLoaderManager().initLoader(WEATHER_LOADER_ID, null, MainActivity.this);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        /* Saving mWeather to be reused should the device rotate */
        outState.putParcelableArrayList(SAVED_INSTANT_STATE_KEY, mWeather);

        super.onSaveInstanceState(outState);
    }
}
