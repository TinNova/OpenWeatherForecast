package com.example.tin.openweatherforecast.utilities;

import android.content.Context;

import com.example.tin.openweatherforecast.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
     * Gets the current time in Unix format
     *
     * @return int of the current time in Unix format
     */
    public static int getTodaysDateInUnix() {
        Date date = Calendar.getInstance().getTime();
        int unix = (int) date.getTime();

        return unix;
    }

    /**
     * Converts a unix time format to this format "Wed, 12, Jan, 13:00"
     *
     * @param unixDate The date and time in Unix format
     * @return String in the form of "Wed, 12, Jan, 13:00"
     */
    public static String convertUnixDateToHumanReadable(int unixDate) {
        int unixInMilliseconds = (unixDate) * 1000;// its need to be in milisecond
        Date dateFormat = new java.util.Date(unixInMilliseconds);
        String humanReadableDateTime = new SimpleDateFormat("EEE, d MMM, HH:mm").format(dateFormat);

        return humanReadableDateTime;
    }

    /**
     * ??
     *
     * @param date
     * @param days
     * @return
     */
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        /* Inserting a minus number will decrement the date */
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }

    public static String formatLastUpdateTime(Context context, String lastUpdateTime) {

        return context.getString(R.string.last_update) + " " + lastUpdateTime;


    }
}
