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
        mHolder.mSelectedFriends.setText("@");
        mHolder.mSelectedFriends.setSelection("@".length());

        mHolder.mOk.setOnClickListener(this);
        mHolder.mBack.setOnClickListener(this);
    }

    private void initViewHolder() {
        if (mHolder != null) {
            return;
        }

        View decorView = getWindow().getDecorView();
        mHolder = ViewHolder.from(decorView);
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
        String friends = text.toString().replaceFirst("@$", "");

        Intent data = new Intent();
        data.putExtra("selected", friends);
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

        static ViewHolder from(View v) {
            Object tag = v.getTag();
            if (tag != null && tag instanceof ViewHolder) {
                return (ViewHolder) tag;
            }

            ViewHolder holder = new ViewHolder();
            v.setTag(holder);

            holder.mSelectedFriends = (EditText) v
                    .findViewById(R.id.selected_friends);
            holder.mOk = v.findViewById(R.id.ok);
            holder.mBack = v.findViewById(R.id.back);

            holder.mPager = (ViewPager) v.findViewById(R.id.pager);

            holder.mRecent = v.findViewById(R.id.recent);
            holder.mAll = v.findViewById(R.id.all);

            return holder;
        }
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
            if (position == 1) {
                // All
                FriendsListFragment fragment = new FriendsListFragment();
                Bundle args = new Bundle(1);
                args.putString("type", FriendsListFragment.Type.ALL.name());
                fragment.setArguments(args);
                return fragment;
            }

            // Recent
            return new FriendsListFragment();
        }

    }
}
