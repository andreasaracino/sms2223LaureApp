package it.uniba.dib.sms22231.utility;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import it.uniba.dib.sms22231.R;

public class TimeUtils {
    public static String getTodayTimeFromDate(Date input) {
        Date startDate = Date.from(Instant.now());
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date today = cal.getTime();
        SimpleDateFormat simpleDateFormat;

        if (input.before(today)) {
            simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        } else {
            simpleDateFormat = new SimpleDateFormat("HH:mm");
        }

        return simpleDateFormat.format(input);
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
        return simpleDateFormat.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static Date stringToDate(String date) {
        try {
            DateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd - HH:mm");
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /*public static getCurrentTime() {
        OffsetDateTime date = OffsetDateTime.now(ZoneOffset.UTC);
        Date date = new Da
        instant;
    }*/
}
