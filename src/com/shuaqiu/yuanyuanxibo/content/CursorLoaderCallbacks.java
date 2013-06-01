/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.content;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import com.shuaqiu.common.widget.CursorAdapter;

/**
 * @author shuaqiu May 6, 2013
 */
public class CursorLoaderCallbacks implements LoaderCallbacks<Cursor> {

    private Context mContext;
    private CursorAdapter mAdapter;
    private String mTable;
    private String[] mProjection;
    private String mOrderBy;

    public CursorLoaderCallbacks(Context context, CursorAdapter adapter,
            String table, String[] projection, String orderBy) {
        mContext = context;
        mAdapter = adapter;
        mTable = table;
        mProjection = projection;
        mOrderBy = orderBy;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncCursorLoader(mContext, mTable, mProjection, null, null,
                null, null, mOrderBy);
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
