/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.content;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;

import com.shuaqiu.common.widget.CursorAdapter;

/**
 * @author shuaqiu May 6, 2013
 */
public class CursorLoaderCallbacks implements LoaderCallbacks<Cursor> {

    private static final String TAG = "CursorLoaderCallbacks";

    private Context mContext;
    private CursorAdapter mAdapter;
    private String mTable;
    private String[] mProjection;
    private String mOrderBy;
    private String mLimit;

    public CursorLoaderCallbacks(Context context, CursorAdapter adapter,
            String table, String[] projection, String orderBy) {
        this(context, adapter, table, projection, orderBy, null);
    }

    public CursorLoaderCallbacks(Context context, CursorAdapter adapter,
            String table, String[] projection, String orderBy, String limit) {
        mContext = context;
        mAdapter = adapter;
        mTable = table;
        mProjection = projection;
        mOrderBy = orderBy;
        mLimit = limit;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (args != null) {
            String limit = args.getString("limit");
            Log.d(TAG, limit);
            if (limit != null) {
                mLimit = limit;
            }
        }
        return new AsyncCursorLoader(mContext, mTable, mProjection, null, null,
                null, null, mOrderBy, mLimit);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

}
