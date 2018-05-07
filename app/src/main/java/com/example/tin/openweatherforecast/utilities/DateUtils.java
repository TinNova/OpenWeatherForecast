package com.example.tin.openweatherforecast.utilities;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Tin on 07/05/2018.
 */

public class DateUtils {

    private static final String TAG = DateUtils.class.getSimpleName();

    /* Gets this moments current DateTime in human readable format */
    public static String getTodaysDateHumanReadable() {
        DateFormat df = new SimpleDateFormat("EEE, d MMM, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        Log.d(TAG, "Date: " + date);

        return date;
    }

    /* Converts Unix DateTime to a human readable format */
    public static String convertUnixDateToHumanReadable(int unixDate) {
        int unixInMilliseconds = (unixDate) * 1000;// its need to be in milisecond
        Date dateFormat = new java.util.Date(unixInMilliseconds);
        String humanReadableDateTime = new SimpleDateFormat("EEE, d MMM, HH:mm").format(dateFormat);

        return humanReadableDateTime;
    }

    /* Code that increments or decrements a DateTime */
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
}
