/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.ViewBinder;
import com.shuaqiu.yuanyuanxibo.content.DatabaseHelper;

/**
 * @author shuaqiu 2013-5-28
 * 
 */
public class StatusPagerAdapter extends PagerAdapter {
    private static final String TAG = "statusadapter";
    private Context mContext;
    private Cursor mCursor;
    private View mStatusView;
    private ViewBinder mBinder;

    public StatusPagerAdapter(Context context) {
        mContext = context;
        mBinder = new StatusBinder(context, StatusBinder.Type.DETAIL);
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
        // if (oldCursor != null) {
        // if (mChangeObserver != null)
        // oldCursor.unregisterContentObserver(mChangeObserver);
        // if (mDataSetObserver != null)
        // oldCursor.unregisterDataSetObserver(mDataSetObserver);
        // }
        // mCursor = newCursor;
        // if (newCursor != null) {
        // if (mChangeObserver != null)
        // newCursor.registerContentObserver(mChangeObserver);
        // if (mDataSetObserver != null)
        // newCursor.registerDataSetObserver(mDataSetObserver);
        // mRowIDColumn = newCursor.getColumnIndexOrThrow("_id");
        // mDataValid = true;
        // // notify the observers about the new cursor
        // notifyDataSetChanged();
        // } else {
        // mRowIDColumn = -1;
        // mDataValid = false;
        // // notify the observers about the lack of a data set
        // notifyDataSetInvalidated();
        // }
        return oldCursor;
    }

    @Override
    public int getCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mStatusView == null) {
            mStatusView = View.inflate(mContext, R.layout.activity_status,
                    container);
        }
        bindView(mStatusView, position);
        return mStatusView;
    }

    /**
     * @param view
     * @param position
     */
    private void bindView(View view, int position) {
        if (mCursor == null || mCursor.isClosed()) {
            // ? need to query data?
            return;
        }

        if (!mCursor.moveToPosition(position)) {
            // TODO fetch more data, insert into database, then query again.
            return;
        }
        int columnIndex = mCursor.getColumnIndex(DatabaseHelper.Status.CONTENT);
        if (columnIndex == -1) {
            Log.w(TAG, "Can't find the context column");
            return;
        }
        String content = mCursor.getString(columnIndex);
        try {
            JSONObject status = new JSONObject(content);
            mBinder.bindView(view, status);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            return;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
