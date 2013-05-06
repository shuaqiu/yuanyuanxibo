/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.content;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;

/**
 * @author shuaqiu May 6, 2013
 */
public class CursorLoaderCallbacks implements LoaderCallbacks<Cursor> {

    private Context mContext;
    private CursorAdapter mAdapter;

    public CursorLoaderCallbacks(Context context, CursorAdapter adapter) {
        mContext = context;
        mAdapter = adapter;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncDBLoader(mContext);
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
