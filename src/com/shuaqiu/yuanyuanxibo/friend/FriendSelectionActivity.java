/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.friend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;

import com.shuaqiu.common.widget.FragmentTabHelper;
import com.shuaqiu.common.widget.FragmentTabHelper.FragmentTabPagerAdapter;
import com.shuaqiu.yuanyuanxibo.R;

/**
 * @author shuaqiu 2013-6-13
 */
public class FriendSelectionActivity extends FragmentActivity implements
        OnClickListener {

    private ViewHolder mHolder;
    private FragmentTabHelper mTabHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friend_selection);

        initViewHolder();
        initTab();

        mHolder.mOk.setOnClickListener(this);
        mHolder.mBack.setOnClickListener(this);
    }

    private void initViewHolder() {
        if (mHolder != null) {
            return;
        }

        View decorView = getWindow().getDecorView();
        Object tag = decorView.getTag();
        if (tag != null && tag instanceof ViewHolder) {
            mHolder = (ViewHolder) tag;
            return;
        }

        mHolder = new ViewHolder();

        mHolder.mSelectedFriends = (EditText) findViewById(R.id.selected_friends);
        mHolder.mOk = findViewById(R.id.ok);
        mHolder.mBack = findViewById(R.id.back);

        mHolder.mPager = (ViewPager) findViewById(R.id.pager);

        mHolder.mRecent = findViewById(R.id.recent);
        mHolder.mAll = findViewById(R.id.all);

        decorView.setTag(mHolder);
    }

    private void initTab() {
        mTabHelper = new FragmentTabHelper(this);
        FragmentTabPagerAdapter pagerAdapter = new TabPagerAdapter(
                getSupportFragmentManager(), mHolder);
        mTabHelper.setPagerAdapter(pagerAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.ok:
            selected();
            break;
        case R.id.back:
            finish();
            break;
        }
    }

    private void selected() {
        Editable text = mHolder.mSelectedFriends.getText();
        Intent data = new Intent();
        data.putExtra("selectedFriends", text.toString());
        setResult(RESULT_OK, data);
        finish();
    }

    private static class ViewHolder {
        private EditText mSelectedFriends;

        private View mOk;
        private View mBack;

        private ViewPager mPager;

        private View mRecent;
        private View mAll;
    }

    private static class TabPagerAdapter extends FragmentTabPagerAdapter {
        private ViewHolder mHolder;
        private View[] mTabs;

        /**
         * @param fm
         * @param mHolder
         */
        public TabPagerAdapter(FragmentManager fm, ViewHolder holder) {
            super(fm);
            mHolder = holder;
            mTabs = new View[] { mHolder.mRecent, mHolder.mAll };
        }

        @Override
        public int getCount() {
            return mTabs.length;
        }

        @Override
        public int getTabId(int position) {
            return getTab(position).getId();
        }

        @Override
        public View getTab(int position) {
            return mTabs[position];
        }

        @Override
        public ViewPager getViewPager() {
            return mHolder.mPager;
        }

        @Override
        public Fragment getItem(int position) {
            return new FriendshipListFragment();
        }

    }
}
