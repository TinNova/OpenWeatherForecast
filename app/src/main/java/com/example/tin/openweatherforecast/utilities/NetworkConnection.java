package com.example.tin.openweatherforecast.utilities;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tin.openweatherforecast.models.Weather;

import java.net.MalformedURLException;
import java.util.ArrayList;


public class NetworkConnection {

    private static final String TAG = NetworkConnection.class.getSimpleName();

    private ArrayList<Weather> mWeather = new ArrayList<>();


    private static NetworkConnection instance = null;

    // Required for Volley API
    private final RequestQueue mRequestQueue;

    private NetworkConnection(Context context) {
        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized NetworkConnection getInstance(Context context) {
        if (null == instance) {
            instance = new NetworkConnection(context);
        }
        return instance;
    }

    /* This prevents the code from needed to pass the context each time */
    public static synchronized NetworkConnection getInstance() {
        if (null == instance) {
            throw new IllegalStateException(NetworkConnection.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return instance;
    }


    public void getResponseFromHttpUrl(String url, final NetworkListener listener) throws MalformedURLException {

        /* If the mWeather ArrayList contains old data, remove it */
        if (mWeather != null) {
            mWeather.clear();
        }

        /* Handler for the JSON response when server returns ok */
        final com.android.volley.Response.Listener<String>
                responseListener = new com.android.volley.Response.Listener<String>() {

            /* If response is successful */
            @Override
            public void onResponse(final String response) {

                mWeather = WeatherJsonUtils.parseWeatherJson(response);

                Log.d(TAG + ": ", "Response : " + response);

                /* Send mWeather ArrayList to MainActivity */
                listener.getWeatherArrayList(mWeather);
            }
        };

        /* Handler for when the server returns an error response */
        com.android.volley.Response.ErrorListener errorListener = new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };

        /* This is the body of the Request */
        StringRequest request = new StringRequest(Request.Method.GET, url, responseListener, errorListener) {
        };


        mRequestQueue.add(request);
    }
}
