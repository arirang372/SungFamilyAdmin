package com.sungfamilyadmin;

import android.content.Context;
import android.widget.Toast;

import com.sungfamilyadmin.constants.Constant;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Random;

public class Utils {

    public static String getFormattedDateTime(long time)
    {
        DateTime dt = new DateTime(time, DateTimeZone.UTC);
        dt = dt.withZone(DateTimeZone.getDefault());
        DateTimeFormatter f = DateTimeFormat.forPattern("MMM dd, hh:mm a");
        return dt.toString(f);
    }

    public static void toast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static int getRandomNumber(int min, int max)
    {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    private static String getRandomFirstName()
    {
        return Constant.FIRST_NAMES[getRandomNumber(0,Constant.FIRST_NAMES.length - 1)];
    }

    private static String getRandomLastName()
    {
        return Constant.LAST_NAMES[getRandomNumber(0,Constant.LAST_NAMES.length - 1)];
    }

    public static String getRandomUserName()
    {
        return getRandomFirstName() + " " + getRandomLastName();
    }
}
