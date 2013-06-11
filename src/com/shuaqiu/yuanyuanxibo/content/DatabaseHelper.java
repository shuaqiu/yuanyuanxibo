/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.content;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    public void close() {
        mDbOpenHelper.close();
    }

    public enum ColumnType {
        TEXT, INTEGER, BOOLEAN
    }

}
