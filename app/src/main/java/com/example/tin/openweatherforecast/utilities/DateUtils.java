package com.example.tin.openweatherforecast.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    private static final String TAG = DateUtils.class.getSimpleName();

    /* Gets this moments current DateTime in a human-readable format */
    public static String getTodaysDateFormat01() {
        DateFormat df = new SimpleDateFormat("EEE, d MMM, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());

        return date;
    }

    /* Gets this moments current DateTime in a human-readable format */
    public static String getTodaysDateFormat02() {
        DateFormat df = new SimpleDateFormat("dd/MM HH:mm");
        String date = df.format(Calendar.getInstance().getTime());

        return date;
    }

    public static int getTodaysDateInUnix() {
        Date date = Calendar.getInstance().getTime();
        int unix = (int) date.getTime();

        return unix;
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
        /* Inserting a minus number will decrement the date */
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }
}
