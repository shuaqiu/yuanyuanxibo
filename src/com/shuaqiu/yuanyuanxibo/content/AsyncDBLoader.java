/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.content;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.AsyncTaskLoader;

/**
 * @author shuaqiu May 6, 2013
 */
public class AsyncDBLoader extends AsyncTaskLoader<Cursor> {
    private DBOpenHelper mDb;

    /**
     * @param context
     */
    public AsyncDBLoader(Context context) {
        super(context);
        mDb = new DBOpenHelper(context);
    }

    @Override
    public Cursor loadInBackground() {
        SQLiteDatabase db = mDb.getReadableDatabase();
        Cursor cursor = db.query("t_status", new String[] { "id", "content",
                "readed" }, null, null, null, null, "id desc", "20");
        return cursor;
    }

}
