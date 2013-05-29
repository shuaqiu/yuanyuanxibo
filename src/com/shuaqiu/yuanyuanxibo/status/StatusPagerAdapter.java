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

import com.shuaqiu.yuanyuanxibo.content.DatabaseHelper;

/**
 * @author shuaqiu 2013-5-28
 * 
 */
public class StatusPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "statusadapter";

    private Cursor mCursor;
    private int mContentIndex = -1;

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

    private String getContent(int position) {
        if (mCursor == null || mCursor.isClosed()) {
            // ? need to query data?
            return null;
        }

        if (!mCursor.moveToPosition(position)) {
            // TODO fetch more data, insert into database, then query again.
            return null;
        }
        int contentIndex = getContentIndex();
        if (contentIndex == -1) {
            Log.w(TAG, "Can't find the context column");
            return null;
        }
        return mCursor.getString(contentIndex);
    }

    private int getContentIndex() {
        if (mContentIndex == -1) {
            mContentIndex = mCursor
                    .getColumnIndex(DatabaseHelper.Status.CONTENT);
        }
        return mContentIndex;
    }

    @Override
    public Fragment getItem(int position) {
        StatusFragment fragment = new StatusFragment();
        Bundle args = new Bundle();
        args.putString(StatusFragment.STATUS_CONTENT, getContent(position));
        fragment.setArguments(args);
        return fragment;
    }

}
