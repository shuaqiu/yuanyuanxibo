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

    private Context mContext;
    private DateFormat dateParser;
    private DateFormat dateFormat;
    private DateFormat timeFormat;

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
            return format(getDateParser().parse(timeStr));
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return timeStr;
    }

    public String format(Date date) {
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

    /**
     * @return
     */
    private DateFormat getDateParser() {
        if (dateParser == null) {
            Locale locale = Locale.getDefault();
            dateParser = new SimpleDateFormat(JSON_TIME_FORMAT, locale);
        }
        return dateParser;
    }

    /**
     * @return
     */
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
