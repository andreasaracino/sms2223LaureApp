package it.uniba.dib.sms22231.utility;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

import it.uniba.dib.sms22231.R;

public class TimeUtils {
    public static String getTimeFromDate(Date input, boolean time) {
        SimpleDateFormat simpleDateFormat;

        if (time) {
            simpleDateFormat = new SimpleDateFormat("HH:mm");
        } else {
            simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        }

        return simpleDateFormat.format(input);
    }

    public static boolean areDatesSameDay(Date a, Date b) {
        Calendar aC = Calendar.getInstance();
        aC.setTime(a);
        Calendar bC = Calendar.getInstance();
        bC.setTime(b);

        return aC.get(Calendar.DAY_OF_YEAR) == bC.get(Calendar.DAY_OF_YEAR) &&
                aC.get(Calendar.YEAR) == bC.get(Calendar.YEAR);
    }

    public static String getTimeAgoFromDate(Date input, Context context) {
        String timeAgo;
        Date currentDate = Date.from(Instant.now());

        long timeElapsed = (currentDate.getTime() - input.getTime()) / 1000;

        if (timeElapsed < 60) {
            return context.getResources().getString(R.string.now);
        }

        timeElapsed /= 60;
        if (timeElapsed < 60) {
            return context.getResources().getQuantityString(R.plurals.minutesAgo, (int) timeElapsed, (int) timeElapsed);
        }

        timeElapsed /= 60;
        if (timeElapsed < 24) {
            return context.getResources().getQuantityString(R.plurals.hoursAgo, (int) timeElapsed, (int) timeElapsed);
        }

        timeElapsed /= 24;
        if (timeElapsed < 7) {
            return context.getResources().getQuantityString(R.plurals.daysAgo, (int) timeElapsed, (int) timeElapsed);
        }

        timeElapsed /= 7;
        if (timeElapsed < 4) {
            return context.getResources().getQuantityString(R.plurals.weeksAgo, (int) timeElapsed, (int) timeElapsed);
        }

        timeElapsed /= 4;
        if (timeElapsed < 12) {
            return context.getResources().getQuantityString(R.plurals.monthsAgo, (int) timeElapsed, (int) timeElapsed);
        }

        timeElapsed /= 12;
        return context.getResources().getQuantityString(R.plurals.yearsAgo, (int) timeElapsed, (int) timeElapsed);
    }

    @SuppressLint("SimpleDateFormat")
    public static String dateToString(Date date) {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd - HH:mm");
        return simpleDateFormat.format(dateToUTC(date));
    }

    @SuppressLint("SimpleDateFormat")
    public static Date stringToDate(String date) {
        try {
            DateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd - HH:mm");
            return dateFromUTC(simpleDateFormat.parse(date));
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date dateFromUTC(Date date){
        return new Date(date.getTime() + Calendar.getInstance().getTimeZone().getOffset(new Date().getTime()));
    }

    public static Date dateToUTC(Date date){
        return new Date(date.getTime() - Calendar.getInstance().getTimeZone().getOffset(date.getTime()));
    }

    /*public static getCurrentTime() {
        OffsetDateTime date = OffsetDateTime.now(ZoneOffset.UTC);
        Date date = new Da
        instant;
    }*/
}
