package it.uniba.dib.sms22231.utility;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import it.uniba.dib.sms22231.R;

public class ResUtils {
    private static ResUtils instance;
    private final Context context;

    private ResUtils(Context context) {
        this.context = context;
    }

    public int getColorByNumber(int n) {
        return Color.parseColor(context.getResources().getStringArray(R.array.chat_icon_colors)[n % 10]);
    }

    public ColorStateList getColorStateList(int resId) {
        return ColorStateList.valueOf(getColor(resId));
    }

    public int getColor(int resId) {
        return ContextCompat.getColor(context, resId);
    }

    public String getStringWithParams(int resId, Object... args) {
        return context.getString(resId, args);
    }

    public static void init(Context context) {
        instance = new ResUtils(context);
    }

    public static ResUtils getInstance() {
        return instance;
    }
}
