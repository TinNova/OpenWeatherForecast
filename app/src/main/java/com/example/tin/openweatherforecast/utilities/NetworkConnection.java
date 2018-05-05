package com.example.tin.openweatherforecast.utilities;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tin.openweatherforecast.models.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;


public class NetworkConnection {

    /* Date & Time Information */
    private static final String OWM_UNIX_DT = "dt";
    private static final String OWM_CALC_DT = "dt_txt";

    /* Temperature Information */
    private static final String OWN_TEMP_CURRENT = "temp";
    private static final String OWN_TEMP_MIN = "temp_min";
    private static final String OWN_TEMP_MAX = "temp_max";

    /* Weather Information */
    private static final String OWN_TITLE = "main";
    private static final String OWN_DESCRIPTION = "description";
    private static final String OWN_ICON = "icon";

    /* Wind Information */
    private static final String OWN_WIND_SPEED = "speed";
    private static final String OWN_WIND_DEGREE = "deg";

    /* JSON Objects And Arrays Within The Json Containing Weather Data */
    private static final String OWN_JSON_LIST = "list";
    private static final String OWN_JSON_MAIN = "main";
    private static final String OWN_JSON_WEATHER = "weather";
    private static final String OWN_JSON_WIND = "wind";

    private static final String OWM_MESSAGE_CODE = "cod";

    /* The Time Value To Save Into The Weather ArrayList */
    private static final String MIDDAY = "12:00:00";



    private static final String TAG = NetworkConnection.class.getSimpleName();

    public ArrayList<Weather> mWeather = new ArrayList<>();


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
                //listener.getResult(response);

                /**PARSE DATA HERE SO IT'S NOT ON THE MAIN THREAD*/
                /**HAVE A METHOD HERE THAT SENDS THE response TO A PARSING CLASS*/

                try {
                    // Define the entire response as a JSON Object
                    JSONObject openWeatherJsonObject = new JSONObject(response);
                    // if cod is not equal to 200, then something went wrong, show a no data screen
                    if (openWeatherJsonObject.getInt(OWM_MESSAGE_CODE) != 200) {
                        //TODO: Handle this case gracefully
                    }

                    // Define the "list" JsonArray as a JSONArray
                    JSONArray listJsonArray = openWeatherJsonObject.getJSONArray(OWN_JSON_LIST);
                    // Using a for loop to cycle through each JsonObject within the listJsonArray
                    for (int i = 0; i < listJsonArray.length(); i++) {

                        // Get the ith forecast in the JSON and define it as a JsonObject
                        JSONObject forecastJsonObject = listJsonArray.getJSONObject(i);

                        int unixDateTime = forecastJsonObject.getInt(OWM_UNIX_DT);
                        String calculateDateTime = forecastJsonObject.getString(OWM_CALC_DT);

                        // Get the "main" JsonObject from the forecastJsonObject
                        // and define it as a JsonObject
                        JSONObject mainJsonObject = forecastJsonObject.getJSONObject(OWN_JSON_MAIN);

                        double tempCurrent = mainJsonObject.getDouble(OWN_TEMP_CURRENT);
                        double tempMin = mainJsonObject.getDouble(OWN_TEMP_MIN);
                        double tempMax = mainJsonObject.getDouble(OWN_TEMP_MAX);

                        // Get the "weather" JsonArray from the forecastJsonObject
                        // and define it as a JsonArray
                        JSONArray weatherJsonArray = forecastJsonObject.getJSONArray(OWN_JSON_WEATHER);
                        // Get the 0th JsonObject from the weatherJsonArray
                        // and define it as a JsonObject
                        JSONObject weatherJsonObject = weatherJsonArray.getJSONObject(0);

                        String weatherTitle = weatherJsonObject.getString(OWN_TITLE);
                        String weatherDescription = weatherJsonObject.getString(OWN_DESCRIPTION);
                        String weatherIcon = weatherJsonObject.getString(OWN_ICON);

                        // Get the "wind" JsonObject from the forecastJsonObject
                        // and define it as a JsonObject
                        JSONObject windJsonObject = forecastJsonObject.getJSONObject(OWN_JSON_WIND);

                        double windSpeed = windJsonObject.getDouble(OWN_WIND_SPEED);
                        double windDegree = windJsonObject.getDouble(OWN_WIND_DEGREE);

                        // if statement ensures we only take the midday data for each day, except
                        // if it is the current day, in which case we will take the current data
                        // and the midday data if it is before midday.
                        if (i == 0 || calculateDateTime.contains(MIDDAY)) {

                            Weather weather = new Weather(
                                    unixDateTime,
                                    calculateDateTime,
                                    tempCurrent,
                                    tempMin,
                                    tempMax,
                                    weatherTitle,
                                    weatherDescription,
                                    weatherIcon,
                                    windSpeed,
                                    windDegree
                            );

                            mWeather.add(weather);
                            Log.d(TAG, "Weather List: " + weather);

                        }

                    }

                    // Send mWeather ArrayList to MainActivity
                    listener.getWeatherArrayList(mWeather);


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

