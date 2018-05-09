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


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();

    /* ID that is responsible for identifying the loader that loads the Weather data */
    private static final int WEATHER_LOADER_ID = 99;

    /*
     * Int representing whether the loader that loads the Weather data has run previously or not
     * 0 = loader has never run before,
     * 1 = loader has run before, therefore it needs to be reset before running it again
     */
    private int loaderCreated = 0;

    /* Strings for the SQL Intent Service */
    public static final String SQL_WEATHER_DATA = "sql_weather_data";

    // Used to check if the device has internet connection
    private ConnectivityManager mConnectionManager;
    private NetworkInfo mNetworkInfo;

    private int LOCATION_UPDATE_TIME = 100000;
    private int LOCATION_UPDATE_DISTANCE = 100000;

    /* if codes returns this value as GPS lat/lon there was an error */
    private Double UPDATE_LOCATION_ERROR = 200.000;


    /*
     * Needed to make the wind speed more readable for users in UI
     */
    String WIND_INTRO;
    String WIND_UNIT;
    String DEGREE_SYMBOL;
    String UPDATED;
    String LATITUDE;
    String LONGITUDE;

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
    TextView tvLocation;
    ImageView ivTodayIcon;
    Button btnRefreshData;

    ConstraintLayout mWeatherUi;
    ProgressBar mLoadingIndicator;
    TextView tvNoData;

    ImageView ivUpdate;

    private LocationManager locationManager;
    private LocationListener locationListener;
    Location location;

    /* lon and lat */
    private Double lat;
    private Double lon;

    public static SharedPreferences weatherSharedPref;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherSharedPref = getSharedPreferences(getPackageName() + "_preferences", MODE_PRIVATE);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

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

                    /* if GPS is not enabled, tell user, and display SQL data */
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                        Toast.makeText(MainActivity.this, getString(R.string.no_gps), Toast.LENGTH_SHORT).show();

                        /* If an instance of the loader already exists, restart it before loading the SQL data */
                        if (loaderCreated == 1) {

                            getSupportLoaderManager().restartLoader(WEATHER_LOADER_ID, null, MainActivity.this);
                        }

                        /* Start loading the SQL data */
                        getSupportLoaderManager().initLoader(WEATHER_LOADER_ID, null, MainActivity.this);

                    } else {

                    /* Getting an updated Latitude and Longitude from the device */
                        requestLocationFromDevice();
                    /* Getting the data and passing in the updated lat and lon of the device */
                        getData(lon, lat);

                    }

                } else {

                    Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

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

                    /* if GPS is not enabled, tell user */
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                        Toast.makeText(MainActivity.this, getString(R.string.no_gps), Toast.LENGTH_SHORT).show();

                    } else {
                    /* Getting an updated Latitude and Longitude from the device */
                        requestLocationFromDevice();
                    /* Getting the data and passing in the updated lat and lon of the deivce */
                        getData(lon, lat);

                    }

                } else {

                    Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

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
        tvLocation = findViewById(R.id.tV_lastLocation);
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


        // Checking If The Device Is Connected To The Internet
        mConnectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);


        // If the connManager and networkInfo is NOT null, start the login() method
        if (mConnectionManager != null)
            mNetworkInfo = mConnectionManager.getActiveNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isConnected()) {

            /* if GPS is enabled */
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                /* Shows loading screen and hides the UI that contains weather data */
                showLoading();
                /* Requesting the Latitude and Longitude of the device */
                requestLocationFromDevice();
                getData(lon, lat);

            } else {

                Toast.makeText(MainActivity.this, getString(R.string.no_gps), Toast.LENGTH_SHORT).show();

                /* If an instance of the loader already exists, restart it before loading the SQL data */
                if (loaderCreated == 1) {
                    getSupportLoaderManager().restartLoader(WEATHER_LOADER_ID, null, this);
                }
                /* Start loading the SQL data */
                getSupportLoaderManager().initLoader(WEATHER_LOADER_ID, null, this);

            }


        } else {

            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

            /* If an instance of the loader already exists, restart it before loading the SQL data */
            if (loaderCreated == 1) {

                getSupportLoaderManager().restartLoader(WEATHER_LOADER_ID, null, this);
            }

            /* Start loading the SQL data */
            getSupportLoaderManager().initLoader(WEATHER_LOADER_ID, null, this);

        }
    }

    /* Requesting the latitude and longitude of the device */
    private void requestLocationFromDevice() {

        Double[] deviceLocationArray = getLonLat();

        /* If there wasn't an error in getting the lat/lon, continue else, launch the SQL data */
        if (!Objects.equals(deviceLocationArray[0], UPDATE_LOCATION_ERROR)) {
            lat = deviceLocationArray[0];
            lon = deviceLocationArray[1];
        } else {

            Toast.makeText(MainActivity.this, getString(R.string.no_gps), Toast.LENGTH_SHORT).show();

            /* If an instance of the loader already exists, restart it before loading the SQL data */
            if (loaderCreated == 1) {

                getSupportLoaderManager().restartLoader(WEATHER_LOADER_ID, null, this);
            }

            /* Start loading the SQL data */
            getSupportLoaderManager().initLoader(WEATHER_LOADER_ID, null, this);

        }
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

    @SuppressLint("MissingPermission")
    private Double[] getLonLat() {

        Double[] deviceLocation = new Double[0];

//        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        /* if GPS is not enabled, tell user, and display SQL data */
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

//            Toast.makeText(this, "GPS Switched off, showing weather for last known location", Toast.LENGTH_SHORT).show();
//            Double testlat = 100.000;
//            Double testlon = 100.00;
//            deviceLocation = new Double[]{testlat, testlon};
//            return (deviceLocation);

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

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_TIME, LOCATION_UPDATE_DISTANCE, locationListener);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                deviceLocation = updateLocation(location);
            }
        }

        return (deviceLocation);
    }

    private Double[] updateLocation(Location location) {

        if (location != null) {
            Double updateLocationLat = location.getLatitude();
            Double updateLocationLon = location.getLongitude();

            return new Double[]{updateLocationLat, updateLocationLon};
        } else {

            return new Double[]{UPDATE_LOCATION_ERROR, UPDATE_LOCATION_ERROR};
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

                    /* dataType, SQL = 1, API = 2 */
                    populateTodaysDate(weather, 2);

                    //TODO: Launch this from NetworkConnection, and put as much of it into IntentServiceUtils
                    /* Save Weather ContentValues to Bundle */
                    Bundle weatherDataBundle = IntentServiceUtils.saveWeatherDataToSql(weather);
                    /* Send Bundle to the SqlIntentService to be saved in SQLite */
                    Intent saveSqlIntent = new Intent(MainActivity.this, WeatherIntentService.class);

                    saveSqlIntent.putExtras(weatherDataBundle);
                    startService(saveSqlIntent);

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
         * We're Going Through Each Row With A For Loop And Putting Them Into Our Weather Model
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
                    /* dataType, SQL = 1, API = 2 */
                    populateTodaysDate(mWeather, 1);

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
    private void populateTodaysDate(ArrayList<Weather> weather, int dataType) {

        WIND_INTRO = getString(R.string.wind_intro);
        WIND_UNIT = getString(R.string.wind_speed_unit);
        DEGREE_SYMBOL = getString(R.string.degrees_symbol);
        UPDATED = getString(R.string.last_update);
        LATITUDE = getString(R.string.latitude);
        LONGITUDE = getString(R.string.longitude);

        Log.d(TAG, "dataType: " + dataType);
        /*
         *if data came from SQL, display the lat/lon and update time that was saved in SharePref
         * at the same time
         */
        if (dataType == 1) {
            String[] sharedPrefLatLonArray = WeatherSharedPreferencesHelper.getLatLonAndDate();
            String sharedPreflat = sharedPrefLatLonArray[0];
            String sharedPrefLon = sharedPrefLatLonArray[1];
            String sharedPrefLastUpdate = sharedPrefLatLonArray[2];

            tvLocation.setText((LATITUDE + " " + sharedPreflat + ", " + LONGITUDE + " " + sharedPrefLon));
            tvLastDataUpdated.setText(sharedPrefLastUpdate);

        } else {

            /* Rounding the lat/lon Doubles to two decimal places */
            Double roundedLat = LocationUtils.round(lat, 2);
            Double roundedLon = LocationUtils.round(lon, 2);

            tvLocation.setText((String.valueOf(LATITUDE + " " + roundedLat + ", " + LONGITUDE + " " + roundedLon)));

            tvLastDataUpdated.setText(UPDATED + " " + DateUtils.getTodaysDateFormat02());

        }

        /* Populating the current times weather */
        tvTodayDate.setText(weather.get(0).getCalculateDateTime());
        tvTodayTemp.setText((String.valueOf(weather.get(0).getTempCurrent() + DEGREE_SYMBOL)));
        tvTodayDescription.setText(weather.get(0).getWeatherDescription());
        tvTodayWindSpeed.setText((String.valueOf(WIND_INTRO + weather.get(0).getWindSpeed() + WIND_UNIT)));
        tvTodayWindDirection.setText((String.valueOf(weather.get(0).getWindDegree())));


        Picasso.with(MainActivity.this).load(weather.get(0).getWeatherIcon())
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
