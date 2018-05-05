package com.example.tin.openweatherforecast.utilities;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.MalformedURLException;


public class NetworkConnection {

    private static final String TAG = NetworkConnection.class.getSimpleName();


    private static NetworkConnection instance = null;

    // Required for Volley API
    private RequestQueue mRequestQueue;

    private NetworkConnection(Context context) {
        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized NetworkConnection getInstance(Context context) {
        if (null == instance) {
            instance = new NetworkConnection(context);
        }
        return instance;
    }

    // This prevents the code from needed to pass the context each time
    public static synchronized NetworkConnection getInstance() {
        if (null == instance) {
            throw new IllegalStateException(NetworkConnection.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return instance;
    }


    public void getResponseFromHttpUrl(String url, final NetworkListener listener) throws MalformedURLException {

        // Handler for the JSON response when server returns ok
        final com.android.volley.Response.Listener<String>
                responseListener = new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(final String response) {

                Log.d(TAG + ": ", "Response : " + response);
                // Passing the response to the NetworkListener
                listener.getResult(response);

            }

        };

        // Handler for when the server returns an error response
        com.android.volley.Response.ErrorListener errorListener = new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };

        // This is the body of the Request
        StringRequest request = new StringRequest(Request.Method.GET, url, responseListener, errorListener) {

        };

        mRequestQueue.add(request);

    }
}
