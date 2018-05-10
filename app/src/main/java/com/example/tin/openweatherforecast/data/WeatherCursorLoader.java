package com.example.tin.openweatherforecast.data;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.util.Log;

public class WeatherCursorLoader extends AsyncTaskLoader<Cursor> {

    private static final String LOG_TAG = WeatherCursorLoader.class.getSimpleName();

    public WeatherCursorLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        Log.d(LOG_TAG, "ON START HIT");
        forceLoad();
    }

    @Nullable
    @Override
    public Cursor loadInBackground() {
        Log.d(LOG_TAG, "LOAD IN BG HIT");
        try {
            return getContext().getContentResolver().query(
                    WeatherContract.WeatherEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    WeatherContract.WeatherEntry.COLUMN_UNIX_DATE + " ASC"
            );
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getLocalizedMessage());
        }
        return null;
    }
}
