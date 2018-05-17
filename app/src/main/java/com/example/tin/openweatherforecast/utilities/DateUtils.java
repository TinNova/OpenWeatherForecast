package com.example.tin.openweatherforecast.utilities;

import android.content.Context;

import com.example.tin.openweatherforecast.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    private static final String TAG = DateUtils.class.getSimpleName();

    /**
     * Get the current date and time
     *
     * @return String in the form of "Wed, 12, Jan, 13:00"
     */
    public static String getTodaysDateFormat01() {
        DateFormat df = new SimpleDateFormat("EEE, d MMM, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());

        return date;
    }

    /**
     * Gets the current date and time
     *
     * @return String in the form of "12/01 13:00"
     */
    public static String getTodaysDateFormat02() {
        DateFormat df = new SimpleDateFormat("dd/MM HH:mm");
        String date = df.format(Calendar.getInstance().getTime());

        return date;
    }

    /**
     * Gets the current date and time
     *
     * @return String in the form of "2014-07-23"
     */
    public static String getTodaysDateFormat03() {
        DateFormat df = new SimpleDateFormat("YYY-MM-dd");
        String date = df.format(Calendar.getInstance().getTime());

        return date;
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        /* Inserting a minus number will decrement the date */
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }

    /**
     * Gets the current time in Unix format
     *
     * @return int of the current time in Unix format
     */
    public static long getTodaysDateInUnix() {
        Date date = Calendar.getInstance().getTime();
        long unix = (long) date.getTime();

        return unix;
    }

    /**
     * Converts a unix time format to this format "Wed, 12, Jan, 13:00"
     *
     * @param unixDate The date and time in Unix format
     * @return String in the form of "Wed, 12, Jan, 13:00"
     */
    public static String convertUnixDateToHumanReadable(long unixDate) {
        long unixInMilliseconds = (unixDate) * 1000;// its need to be in milisecond
        Date dateFormat = new java.util.Date(unixInMilliseconds);
        String humanReadableDateTime = new SimpleDateFormat("EEE, d MMM").format(dateFormat);

        return humanReadableDateTime;
    }

    /**
     * Formating the time for the refresh button
     *
     * @param context        Android Context to access preferences and resources
     * @param lastUpdateTime String of the last time the data was updated
     * @return String in the format of "Updated 16/05 21:00"
     */
    public static String formatLastUpdateTime(Context context, String lastUpdateTime) {

        return context.getString(R.string.last_update) + " " + lastUpdateTime;
    }
}
