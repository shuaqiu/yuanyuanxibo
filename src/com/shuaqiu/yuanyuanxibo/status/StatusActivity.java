package com.shuaqiu.yuanyuanxibo.status;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;

import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.ViewBinder;
import com.shuaqiu.yuanyuanxibo.content.DatabaseHelper;

public class StatusActivity extends Activity implements OnTouchListener {

    private static final String TAG = "status";

    private DatabaseHelper mDbHelper;
    private Cursor mCursor;
    private int position;

    private ViewPager mViewPager;
    private ViewBinder mBinder;

    private boolean isTouchMove;
    private float touchX;

    private StatusPagerAdapter mAdpater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        mDbHelper = new DatabaseHelper(this);
        mDbHelper.openForRead();
        new AsyncDatabaseTask().execute();

        initViewPager();

        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);

        // bindView(getWindow().getDecorView(), position);

        // getWindow().getDecorView().setOnTouchListener(this);
    }

    private void initViewPager() {
        setContentView(R.layout.activity_status_pager);

        mAdpater = new StatusPagerAdapter(this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAdpater);
    }

    /**
     * @param decorView
     * @param position
     */
    private void bindView(View decorView, int position) {
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
            mBinder.bindView(getWindow().getDecorView(), status);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        mDbHelper.close();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            isTouchMove = true;
            touchX = event.getX();
            break;
        case MotionEvent.ACTION_UP:
            if (isTouchMove) {
                if (event.getX() - touchX < -20) {
                    // move to left
                    position++;
                    bindView(getWindow().getDecorView(), position);
                } else if (event.getX() - touchX > 20) {
                    // move to right
                    position--;
                    bindView(getWindow().getDecorView(), position);
                }
                isTouchMove = false;
            }
            break;
        }
        return false;
    }

    private class AsyncDatabaseTask extends AsyncTask<String, Void, Void> {
        String table = DatabaseHelper.Status.TABLE_NAME;
        String[] columns = new String[] { DatabaseHelper.Status.ID,
                DatabaseHelper.Status.CONTENT, DatabaseHelper.Status.READED };
        String orderBy = DatabaseHelper.Status.ID + " desc";

        @Override
        protected Void doInBackground(String... params) {
            mCursor = mDbHelper
                    .query(table, columns, null, null, orderBy, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // bindView(getWindow().getDecorView(), position);
            mAdpater.changeCursor(mCursor);
            mAdpater.notifyDataSetChanged();
        }
    }
}
