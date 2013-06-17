/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.content;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

/**
 * @author shuaqiu May 7, 2013
 */
public class DatabaseHelper {

    private static final String TAG = "db";

    private DatabaseOpenHelper mDbOpenHelper;
    private SQLiteDatabase mDb;

    public DatabaseHelper(Context context) {
        mDbOpenHelper = new DatabaseOpenHelper(context);
    }

    public void openForWrite() {
        try {
            mDb = mDbOpenHelper.getWritableDatabase();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            mDb = mDbOpenHelper.getReadableDatabase();
        }
    }

    public void openForRead() {
        mDb = mDbOpenHelper.getReadableDatabase();
    }

    public long saveOrUpdate(String table, ContentValues values) {
        return mDb.insertWithOnConflict(table, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public Cursor query(String table, String[] columns, String selection,
            String[] selectionArgs, String groupBy, String having,
            String orderBy, String limit) {
        return mDb.query(table, columns, selection, selectionArgs, groupBy,
                having, orderBy, limit);
    }

    public Cursor query(String table, String[] columns, String selection,
            String[] selectionArgs, String orderBy, String limit) {
        return mDb.query(table, columns, selection, selectionArgs, null, null,
                orderBy, limit);
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return mDb.rawQuery(sql, selectionArgs);
    }

    public void close() {
        mDbOpenHelper.close();
    }

    public enum ColumnType {
        /** 字符竄 */
        TEXT {
            @Override
            public <E extends Enum<E>> void addToBundle(Bundle bundle,
                    Cursor cursor, E column) {
                String val = cursor.getString(column.ordinal());
                bundle.putString(column.name(), val);
            }

            @Override
            public void extractTo(ContentValues values, JSONObject json,
                    String field) {
                values.put(field, json.optString(field));
            }
        },
        /** 整型 */
        INTEGER {
            @Override
            public <E extends Enum<E>> void addToBundle(Bundle bundle,
                    Cursor cursor, E column) {
                bundle.putLong(column.name(), cursor.getLong(column.ordinal()));
            }

            @Override
            public void extractTo(ContentValues values, JSONObject json,
                    String field) {
                values.put(field, json.optLong(field));
            }
        },
        /** boolean */
        BOOLEAN {
            @Override
            public <E extends Enum<E>> void addToBundle(Bundle bundle,
                    Cursor cursor, E column) {
                boolean val = cursor.getInt(column.ordinal()) == 1;
                bundle.putBoolean(column.name(), val);
            }

            @Override
            public void extractTo(ContentValues values, JSONObject json,
                    String field) {
                values.put(field, json.optBoolean(field));
            }
        };

        /**
         * 從cursor 取出對應column 的值, 並放入到bundle, 其中key 直接使用column 的name() 方法返回值
         * 
         * @param bundle
         * @param cursor
         * @param column
         */
        public abstract <E extends Enum<E>> void addToBundle(Bundle bundle,
                Cursor cursor, E column);

        /**
         * 從JSON 中取出對應字段的值, 並放入到ContentValues, key 直接使用該字段
         * 
         * @param values
         * @param json
         * @param field
         */
        public abstract void extractTo(ContentValues values, JSONObject json,
                String field);
    }

}
