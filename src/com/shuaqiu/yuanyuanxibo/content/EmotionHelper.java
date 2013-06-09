package com.shuaqiu.yuanyuanxibo.content;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

/**
 * @author shuaqiu 2013-5-31
 */
public class EmotionHelper extends AbsObjectHelper {

    static final String TAG = "StatusHelper";

    public static final String TABLE = "t_emotion";

    /** 排序字段 */
    public static final String ORDER_BY = Column.id.name() + " desc";

    public static final Column[] COLUMNS = Column.values();

    /**
     * 微博的列定義
     * 
     * @author shuaqiu 2013-5-31
     */
    public enum Column {
        /** ID */
        id(ColumnType.INTEGER),
        /** 短語 */
        phrase(ColumnType.TEXT),
        /** 類型 */
        type(ColumnType.TEXT),
        /** 表情圖片URL */
        url(ColumnType.TEXT),
        /** 是否熱門表情: true: 是, false: 否 */
        hot(ColumnType.BOOLEAN),
        /** 是否普通表情: true: 是, false: 否 */
        common(ColumnType.BOOLEAN),
        /** 類別 */
        category(ColumnType.TEXT),
        /** 縮略圖片地址 */
        icon(ColumnType.TEXT),
        /** 短語 */
        value(ColumnType.TEXT),
        /** */
        picid(ColumnType.TEXT);

        // -----------------------------------------
        ColumnType columnType;

        private Column(ColumnType type) {
            columnType = type;
        }
    }

    protected static String getDdl() {
        return getDdl(TABLE, COLUMNS, true);
    }

    public static String[] names() {
        return names(COLUMNS);
    }

    public static Bundle toBundle(Cursor cursor, int position) {
        boolean isSuccessMoved = cursor.moveToPosition(position);
        if (!isSuccessMoved) {
            return Bundle.EMPTY;
        }

        return toBundle(cursor);
    }

    public static Bundle toBundle(Cursor cursor) {
        int len = COLUMNS.length;
        Bundle emotion = new Bundle(len);
        for (Column c : COLUMNS) {
            if (cursor.isNull(c.ordinal())) {
                // 如果字段是null 值, 則不作處理
                continue;
            }

            String field = c.name();
            try {
                // 獲取值, 并放到bundle 中
                if (c.columnType == ColumnType.TEXT) {
                    emotion.putString(field, cursor.getString(c.ordinal()));
                } else if (c.columnType == ColumnType.INTEGER) {
                    emotion.putLong(field, cursor.getLong(c.ordinal()));
                } else if (c.columnType == ColumnType.BOOLEAN) {
                    emotion.putBoolean(field, cursor.getInt(c.ordinal()) == 1);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        return emotion;
    }

    /**
     * @param context
     */
    public EmotionHelper(Context context) {
        super(context);
        table = TABLE;
        names = names();
        orderBy = ORDER_BY;
    }

    @Override
    protected ContentValues extract(JSONObject json) {
        ContentValues values = new ContentValues(COLUMNS.length);

        for (Column c : COLUMNS) {
            String field = c.name();

            if (c.columnType == ColumnType.TEXT) {
                values.put(field, json.optString(field));
            } else if (c.columnType == ColumnType.INTEGER) {
                values.put(field, json.optLong(field));
            } else if (c.columnType == ColumnType.BOOLEAN) {
                values.put(field, json.optBoolean(field));
            }
        }

        return values;
    }

}
