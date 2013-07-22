/**
 * 
 */
package com.shuaqiu.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.util.Log;

/**
 * @author shuaqiu May 5, 2013
 */
public class TimeHelper {
    private static final String TAG = "time";

    private static final long ONE_MINUTE = 60 * 1000L;

    private static final long ONE_HOUR = 60 * ONE_MINUTE;

    private static final long ONE_DAY = 24 * ONE_HOUR;

    private static final String JSON_TIME_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

    private static final String DAY_FORMAT = "yyyy-MM-dd";

    private static DateFormat dateParser;
    private static DateFormat dayFormat;

    private Context mContext;

    private DateFormat dateFormat;
    private DateFormat timeFormat;

    public static String formatToDay(String timeStr) {
        return format(timeStr, getDayFormat());
    }

    public static String format(String timeStr, DateFormat formatter) {
        try {
            return formatter.format(getJsonTimeFormatter().parse(timeStr));
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return timeStr;
    }

    public static DateFormat getJsonTimeFormatter() {
        if (dateParser == null) {
            Locale locale = Locale.getDefault();
            dateParser = new SimpleDateFormat(JSON_TIME_FORMAT, locale);
        }
        return dateParser;
    }

    public static DateFormat getDayFormat() {
        if (dayFormat == null) {
            Locale locale = Locale.getDefault();
            dayFormat = new SimpleDateFormat(DAY_FORMAT, locale);
        }
        return dayFormat;
    }

    public TimeHelper(Context context) {
        mContext = context;
    }

    /**
     * @param timeStr
     *            格式爲"Sat Apr 27 00:59:08 +0800 2013" 的時間字符串
     * @return
     */
    public String beautyTime(String timeStr) {
        try {
            return beautyTime(getJsonTimeFormatter().parse(timeStr));
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return timeStr;
    }

    public String beautyTime(Date date) {
        long diff = System.currentTimeMillis() - date.getTime();

        if (diff < ONE_MINUTE) {
            return "刚刚";
        }
        if (diff < ONE_HOUR) {
            return diff / ONE_MINUTE + "分钟前";
        }
        if (diff < ONE_DAY) {
            return getTimeFormat().format(date);
        }
        return getDateFormat().format(date);
    }

    public String beautyTime(long time) {
        long diff = System.currentTimeMillis() - time;

        if (diff < ONE_MINUTE) {
            return "刚刚";
        }
        if (diff < ONE_HOUR) {
            return diff / ONE_MINUTE + "分钟前";
        }
        if (diff < ONE_DAY) {
            return getTimeFormat().format(time);
        }
        return getDateFormat().format(time);
    }

    private DateFormat getDateFormat() {
        if (dateFormat == null) {
            Locale locale = Locale.getDefault();

            boolean b24 = android.text.format.DateFormat
                    .is24HourFormat(mContext);

            String template = "MMM dd";
            if (b24) {
                template += " HH:mm";
            } else {
                template += " h:mm a";
            }

            dateFormat = new SimpleDateFormat(template, locale);
        }
        return dateFormat;
    }

    private DateFormat getTimeFormat() {
        if (timeFormat == null) {
            timeFormat = android.text.format.DateFormat.getTimeFormat(mContext);
            if (timeFormat instanceof SimpleDateFormat) {
                SimpleDateFormat f = (SimpleDateFormat) timeFormat;
                Log.d(TAG, f.toLocalizedPattern());
                Log.d(TAG, f.toPattern());
            }
        }
        return timeFormat;
    }
}
