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
    // Ottengo ora e minuti, oppure giorno, mese e anno da una data
    public static String getTimeFromDate(Date input, boolean time) {
        SimpleDateFormat simpleDateFormat;
        simpleDateFormat = new SimpleDateFormat(time ? "HH:mm" : "dd/MM/yyyy");

        return simpleDateFormat.format(input);
    }

    // Controllo se due date sono dello stesso giorno
    public static boolean areDatesSameDay(Date a, Date b) {
        Calendar aC = Calendar.getInstance();
        aC.setTime(a);
        Calendar bC = Calendar.getInstance();
        bC.setTime(b);

        return aC.get(Calendar.DAY_OF_YEAR) == bC.get(Calendar.DAY_OF_YEAR) &&
                aC.get(Calendar.YEAR) == bC.get(Calendar.YEAR);
    }

    // converto una data in UTC e poi in stringa
    @SuppressLint("SimpleDateFormat")
    public static String dateToString(Date date, boolean includeSeconds) {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd - HH:mm" + (includeSeconds ? ":ss" : ""));
        return simpleDateFormat.format(dateToUTC(date));
    }

    // converto una data in stringa
    @SuppressLint("SimpleDateFormat")
    public static String dateToString(Date date) {
        DateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        return simpleDateFormat.format(date);
    }

    // converto una stringa in data e poi la converte nel fuso orario di sistema
    @SuppressLint("SimpleDateFormat")
    public static Date stringToDate(String date, boolean includeSeconds) {
        try {
            DateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd - HH:mm" + (includeSeconds ? ":ss" : ""));
            return dateFromUTC(simpleDateFormat.parse(date));
        } catch (ParseException e) {
            return null;
        }
    }

    // converte una data da UTC al fuso orario corrente
    public static Date dateFromUTC(Date date){
        return new Date(date.getTime() + Calendar.getInstance().getTimeZone().getOffset(new Date().getTime()));
    }

    // converte una data dal fuso orario corrente a UTC
    public static Date dateToUTC(Date date){
        return new Date(date.getTime() - Calendar.getInstance().getTimeZone().getOffset(date.getTime()));
    }
}
