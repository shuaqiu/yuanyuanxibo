package com.shuaqiu.yuanyuanxibo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.shuaqiu.yuanyuanxibo.HttpCursor.CursorPair;
import com.shuaqiu.yuanyuanxibo.HttpCursor.Type;

/**
 * 
 */
public class HttpCursorKeeper {
    private static final String PREF_NAME = "cursor";

    public static void save(Context context, HttpCursor cursor) {
        SharedPreferences pref = getPrefences(context, cursor.getType());
        Editor editor = pref.edit();
        editor.clear();
        // editor.putString("type", cursor.getType());
        editor.putInt("pairCount", cursor.getPairs().length);

        int i = 0;
        for (CursorPair pair : cursor.getPairs()) {
            editor.putLong("timestamp_" + i, pair.getTimestamp());
            editor.putLong("min_" + i, pair.getMin());
            editor.putLong("max_" + i, pair.getMax());
            i++;
        }
        editor.commit();
    }

    /**
     * 清空sharepreference
     * 
     * @param context
     */
    public static void clear(Context context, Type type) {
        SharedPreferences pref = getPrefences(context, type);
        Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 从SharedPreferences读取accessstoken
     * 
     * @param context
     * @return Oauth2AccessToken
     */
    public static HttpCursor read(Context context, Type type) {
        SharedPreferences pref = getPrefences(context, type);

        // String type = pref.getString("type", "");

        int pairCount = pref.getInt("pairCount", 0);
        CursorPair[] pairs = new CursorPair[pairCount];
        for (int i = 0; i < pairCount; i++) {
            long timestamp = pref.getLong("timestamp_" + i, 0);
            long min = pref.getLong("min_" + i, 0);
            long max = pref.getLong("max_" + i, 0);
            pairs[i] = new CursorPair(timestamp, min, max);
            i++;
        }
        return new HttpCursor(type, pairs);
    }

    /**
     * @param context
     * @param type
     * @return
     */
    protected static SharedPreferences getPrefences(Context context, Type type) {
        String name = PREF_NAME + "_" + type.name();
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }
}
