/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.shuaqiu.yuanyuanxibo.content.StatusHelper;

/**
 * @author shuaqiu 2013-5-28
 */
public class StatusPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "StatusPagerAdapter";

    private Cursor mCursor;

    public StatusPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if (newCursor != null) {
            Log.d(TAG, "cursor changed");
            // notify the observers about the new cursor
            notifyDataSetChanged();
        }
        return oldCursor;
    }

    @Override
    public int getCount() {
        if (mCursor == null) {
            return 0;
        }
        Log.d(TAG, "count ->" + mCursor.getCount());
        return mCursor.getCount();
    }

    private Bundle getStatusBundle(int position) {
        if (mCursor == null || mCursor.isClosed()) {
            // ? need to query data?
            return null;
        }

        if (!mCursor.moveToPosition(position)) {
            // TODO fetch more data, insert into database, then query again.
            return null;
        }
        return StatusHelper.toBundle(mCursor, position);
    }

    @Override
    public Fragment getItem(int position) {
        StatusFragment fragment = new StatusFragment();
        Bundle args = new Bundle();
        args.putBundle(StatusFragment.STATUS, getStatusBundle(position));
        fragment.setArguments(args);
        return fragment;
    }

}
