package com.example.tin.openweatherforecast.sql;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.example.tin.openweatherforecast.sql.WeatherContract.WeatherEntry.TABLE_NAME;

public class WeatherProvider extends ContentProvider {

    /* Defining final integer constants for the directory of favouritesMovies and a single Item
     * It's convention to use 100, 200, 300 ect for directories,
     * and related ints (101, 102, ..) for items in that directory.
     */
    private static final int CODE_WEATHER = 100;
    private static final int CODE_WEATHER_WITH_ID = 101;

    /*
     * The URI Matcher used by this content provider. The leading "s" in this variable name
     * signifies that this UriMatcher is a static member variable of WeatherProvider
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /* Defining a static buildUriMatcher method that associates URI's with their int match */
    private static UriMatcher buildUriMatcher() {
        /* .NO_MATCH defines it as an empty uriMatcher (because we haven't added an int match yet */
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
         * Add matches with addURI (String authority, String path, int code), this means we are adding the Uri
         * This is for an entire directory
         * content://com.example.tin.robotic/favouriteCompanies/100
         */
        uriMatcher.addURI(WeatherContract.AUTHORITY, WeatherContract.PATH_WEATHER, CODE_WEATHER);
        /*
         * This is for a single item
         * content://com.example.tin.robotic/favouriteCompanies/2/101
         */
        uriMatcher.addURI(WeatherContract.AUTHORITY, WeatherContract.PATH_WEATHER + "/#", CODE_WEATHER_WITH_ID);

        return uriMatcher;
    }

    /* Variable of the FavouriteDbHelper so it can be initialised in onCreate */
    private WeatherDbHelper mWeatherDbHelper;


    @Override
    public boolean onCreate() {

        Context context = getContext();
        mWeatherDbHelper = new WeatherDbHelper(context);

        /* Return true because the method is done */
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        /* Get access to underlying database (read-only for query) */
        final SQLiteDatabase db = mWeatherDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor retCursor;

        switch (match) {
            /* Query for the favouriteMovies directory */
            case CODE_WEATHER:
                retCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

                /* Default Exception */
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        /* Set a notification URI on the Cursor */
        assert retCursor != null;
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mWeatherDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case CODE_WEATHER:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
//                        long weatherDate =
//                                value.getAsLong(WeatherContract.WeatherEntry.COLUMN_UNIX_DATE);

                        //TODO: Ensure that the date is normalised before saving to SQL
                        /* If data is NOT Normalised, then IllegalArgumentExcpetion will be thrown*/
//                        if (!SunshineDateUtils.isDateNormalized(weatherDate)) {
//                            throw new IllegalArgumentException("Date must be normalized to insert");
//                        }

                        long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        /* Users of the delete method will expect the number of rows deleted to be returned. */
        int numRowsDeleted;

        /*
         * A value of null will delete the entire table, but we won't know how many rows were
         * deleted. The documentation for SQLiteDatabase states, passing "1" for the selection
         * will delete all rows and return the number of rows deleted, which is what this method
         * requires
         */
        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {

            case CODE_WEATHER:
                /* Get access to the database signify that all of the rows should be deleted */
                numRowsDeleted = mWeatherDbHelper.getWritableDatabase().delete(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If rows were deleted, notify the resolver */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        /* Return the number of rows deleted */
        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
