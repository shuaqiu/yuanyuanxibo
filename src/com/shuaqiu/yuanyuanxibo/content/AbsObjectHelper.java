/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.content;

import java.lang.reflect.Field;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * @author shuaqiu 2013-6-9
 */
public abstract class AbsObjectHelper extends DatabaseHelper {

    private static final String TAG = "AbsObjectHelper";

    protected static <E extends Enum<E>> String getDdl(String table,
            E[] columns, E primary) {
        return getDdl(table, columns, primary, false);
    }

    /**
     * @param table
     * @param columns
     * @param primary
     * @param isAutoIncrement
     * @return
     */
    private static <E extends Enum<E>> String getDdl(String table, E[] columns,
            E primary, boolean isAutoIncrement) {
        StringBuilder ddl = new StringBuilder("create table if not exists ");
        ddl.append(table);
        ddl.append("(");

        int i = 0;
        for (E column : columns) {
            if (i > 0) {
                ddl.append(", ");
            }
            ddl.append(column.name() + " ");

            ColumnType columnType = getColumnType(column);
            ddl.append(columnType.name());
            if (column == primary) {
                ddl.append(" primary key");
                if (isAutoIncrement) {
                    ddl.append(" autoincrement");
                }
            }
            i++;
        }

        ddl.append(")");

        return ddl.toString();
    }

    private static <E extends Enum<E>> ColumnType getColumnType(E column) {
        try {
            Field field = column.getClass().getDeclaredField("columnType");
            return (ColumnType) field.get(column);
        } catch (SecurityException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (NoSuchFieldException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return ColumnType.TEXT;
    }

    public static <E extends Enum<E>> String[] names(E[] columns) {
        String[] names = new String[columns.length];
        int i = 0;
        for (E column : columns) {
            names[i++] = column.name();
        }

        return names;
    }

    protected String table;
    protected String[] names;
    protected String orderBy;

    public AbsObjectHelper(Context context) {
        super(context);
    }

    public Cursor query(String selection, String[] selectionArgs, String limit) {
        return query(table, names, selection, selectionArgs, orderBy, limit);
    }

    public int saveOrUpdate(JSONArray arr) {
        Log.d(TAG, "write data to " + table);

        for (int i = 0; i < arr.length(); i++) {
            JSONObject json = arr.optJSONObject(i);
            saveOrUpdate(json);
        }

        return arr.length();
    }

    public long saveOrUpdate(JSONObject json) {
        ContentValues values = extract(json);
        return saveOrUpdate(table, values);
    }

    protected abstract ContentValues extract(JSONObject json);

    public int delete(String whereClause, String[] whereArgs) {
        return delete(table, whereClause, whereArgs);
    }
}