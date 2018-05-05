package com.example.tin.openweatherforecast.utilities;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

                /**PARSE DATA HERE SO IT'S NOT ON THE MAIN THREAD*/
                /**HAVE A METHOD HERE THAT SENDS THE response TO A PARSING CLASS*/

                try {
                    // Define the entire response as a JSON Object
                    JSONObject openWeatherJsonObject = new JSONObject(response);
                    // if cod is not equal to 200, then something went wrong, show a no data screen
                    if (openWeatherJsonObject.getInt("cod") != 200) {
                        //TODO: Handle this case gracefully
                    }

                    // Define the "list" JsonArray as a JSONArray
                    JSONArray listJsonArray = openWeatherJsonObject.getJSONArray("list");
                    // Using a for loop to cycle through each JsonObject within the listJsonArray
                    for (int i = 0; i < listJsonArray.length(); i++) {

                        // Get the ith forecast in the JSON and define it as a JsonObject
                        JSONObject forecastJsonObject = listJsonArray.getJSONObject(i);

                        int unixDateTime = forecastJsonObject.getInt("dt");
                        String calculateDateTime = forecastJsonObject.getString("dt_txt");

                        // Get the "main" JsonObject from the forecastJsonObject
                        // and define it as a JsonObject
                        JSONObject mainJsonObject = forecastJsonObject.getJSONObject("main");

                        double tempCurrent = mainJsonObject.getDouble("temp");
                        double tempMin = mainJsonObject.getDouble("temp_min");
                        double tempMax = mainJsonObject.getDouble("temp_max");

                        // Get the "weather" JsonArray from the forecastJsonObject
                        // and define it as a JsonArray
                        JSONArray weatherJsonArray = forecastJsonObject.getJSONArray("weather");
                        // Get the 0th JsonObject from the weatherJsonArray
                        // and define it as a JsonObject
                        JSONObject weatherJsonObject = weatherJsonArray.getJSONObject(0);

                        String weatherTitle = weatherJsonObject.getString("main");
                        String weatherDescription = weatherJsonObject.getString("description");
                        String weatherIcon = weatherJsonObject.getString("icon");

                        // Get the "wind" JsonObject from the forecastJsonObject
                        // and define it as a JsonObject
                        JSONObject windJsonObject = forecastJsonObject.getJSONObject("wind");

                        double windSpeed = windJsonObject.getDouble("speed");
                        double windDegree = windJsonObject.getDouble("deg");

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

