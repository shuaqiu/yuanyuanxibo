package com.shuaqiu.yuanyuanxibo.status;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;

import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.content.DatabaseHelper;

public class StatusActivity extends FragmentActivity {

    private static final String TAG = "statusactivity";

    private DatabaseHelper mDbHelper;
    private Cursor mCursor;
    private int mPosition;
    private long mMaxId;

    private ViewPager mViewPager;

    private StatusPagerAdapter mAdpater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_status_pager);

        mDbHelper = new DatabaseHelper(this);
        mDbHelper.openForRead();

        new AsyncDatabaseTask().execute();

        Intent intent = getIntent();
        mPosition = intent.getIntExtra("position", 0);
        mMaxId = intent.getLongExtra("maxId", -1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        mDbHelper.close();
    }

    private void initViewPager() {
        mAdpater = new StatusPagerAdapter(getSupportFragmentManager());
        mAdpater.changeCursor(mCursor);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAdpater);
        mViewPager.setCurrentItem(mPosition);
    }

    private class AsyncDatabaseTask extends AsyncTask<String, Void, Void> {
        String table = DatabaseHelper.Status.TABLE_NAME;
        String[] columns = new String[] { DatabaseHelper.Status.ID,
                DatabaseHelper.Status.CONTENT, DatabaseHelper.Status.READED };
        String orderBy = DatabaseHelper.Status.ID + " desc";

        @Override
        protected Void doInBackground(String... params) {
            Log.d(TAG, "try to load status");
            String selection = null;
            String[] selectionArgs = null;
            if (mMaxId > 0) {
                Log.d(TAG, "filter id <= " + mMaxId);
                selection = "id <= ?";
                selectionArgs = new String[] { Long.toString(mMaxId) };
            }
            mCursor = mDbHelper.query(table, columns, selection, selectionArgs,
                    orderBy, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "loaded status, begin to show");
            initViewPager();
        }
    }
}
