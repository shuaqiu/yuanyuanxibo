/**
 * 
 */
package com.shuaqiu.yuanyuanxibo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.shuaqiu.common.widget.FragmentTabHelper.FragmentTabPagerAdapter;
import com.shuaqiu.yuanyuanxibo.MainActivity.ViewHolder;
import com.shuaqiu.yuanyuanxibo.comment.CommentListFragment;
import com.shuaqiu.yuanyuanxibo.status.RetweetedStatusListFragment;
import com.shuaqiu.yuanyuanxibo.status.StatusListFragment;

/**
 */
public class SectionsPagerAdapter extends FragmentTabPagerAdapter {

    private ViewHolder mHolder;
    private View[] mTabs;
    private Fragment[] items;

    public SectionsPagerAdapter(FragmentManager fm, ViewHolder holder) {
        super(fm);
        mHolder = holder;
        mTabs = new View[] { mHolder.mHome, mHolder.mAtMe, mHolder.mComments,
                mHolder.mMessages };
        items = new Fragment[getCount()];
    }

    @Override
    public Fragment getItem(int position) {
        if (items[position] == null) {
            items[position] = createItem(position);
        }
        return items[position];
    }

    /**
     * @param position
     * @return
     */
    private Fragment createItem(int position) {
        switch (position) {
        case 0:
            return new StatusListFragment();
        case 1:
            return new RetweetedStatusListFragment();
        case 2:
            return new CommentListFragment();
        case 3:
            return new Fragment();
        }
        return new Fragment();
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
        return mHolder.mViewPager;
    }
}