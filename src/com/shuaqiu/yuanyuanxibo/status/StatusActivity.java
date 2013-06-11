package com.shuaqiu.yuanyuanxibo.status;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.shuaqiu.common.promiss.Callback;
import com.shuaqiu.common.promiss.DeferredManager;
import com.shuaqiu.yuanyuanxibo.Actions.Comment;
import com.shuaqiu.yuanyuanxibo.Actions.Status;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.StateKeeper;
import com.shuaqiu.yuanyuanxibo.comment.SendActivity;
import com.shuaqiu.yuanyuanxibo.content.QueryCallable;
import com.shuaqiu.yuanyuanxibo.content.StatusHelper;

public class StatusActivity extends FragmentActivity implements
        OnClickListener, Callback<Cursor> {

    static final String TAG = "statusactivity";

    private StatusHelper mStatusHelper;
    private Cursor mCursor;
    private int mPosition;
    private long mMaxId;

    private ViewPager mViewPager;

    private StatusPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_status);

        Intent intent = getIntent();
        mPosition = intent.getIntExtra("position", 0);
        mMaxId = intent.getLongExtra("maxId", -1);

        mStatusHelper = new StatusHelper(this);
        mStatusHelper.openForRead();

        String selection = null;
        String[] args = null;
        if (mMaxId > 0) {
            Log.d(TAG, "filter id <= " + mMaxId);
            selection = "id <= ?";
            args = new String[] { Long.toString(mMaxId) };
        }
        QueryCallable query = new QueryCallable(mStatusHelper, selection, args,
                "100");
        DeferredManager.when(query).then(this);

        findViewById(R.id.repost).setOnClickListener(this);
        findViewById(R.id.comment).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        mStatusHelper.close();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();

        int currentItem = mViewPager.getCurrentItem();
        Bundle status = mAdapter.getStatusBundle(currentItem);

        String token = StateKeeper.accessToken.getAccessToken();
        intent.putExtra("access_token", token);
        intent.putExtras(buildClickArgs(status));

        switch (v.getId()) {
        case R.id.repost:
            intent.setAction(Status.REPOST);
            intent.setClass(this, SendActivity.class);
            break;
        case R.id.comment:
            intent.setAction(Comment.CREATE);
            intent.setClass(this, SendActivity.class);
            break;
        }

        startActivity(intent);
    }

    protected Bundle buildClickArgs(Bundle status) {
        BundleStatusBinder binder = new BundleStatusBinder(this, null);
        return binder.buildClickArgs(status);
    }

    private void initViewPager() {
        mAdapter = new StatusPagerAdapter(getSupportFragmentManager());
        mAdapter.changeCursor(mCursor);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mPosition);
    }

    @Override
    public void apply(Cursor result) {
        Log.d(TAG, "loaded status, begin to show");
        mCursor = result;
        initViewPager();
    }
}
