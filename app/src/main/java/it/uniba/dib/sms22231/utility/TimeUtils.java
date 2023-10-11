package it.uniba.dib.sms22231.utility;

import android.content.Context;

import java.time.Instant;
import java.util.Date;

import it.uniba.dib.sms22231.R;

public class TimeUtils {
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
}