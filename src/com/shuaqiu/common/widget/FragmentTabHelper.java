/**
 * 
 */
package com.shuaqiu.common.widget;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.shuaqiu.yuanyuanxibo.R;

/**
 * @author shuaqiu Jun 13, 2013
 */
public class FragmentTabHelper implements OnClickListener, OnPageChangeListener {

    private static final String TAG = "FragmentTabHost";

    private int defaultBackground = 0;

    private int selectedBackground = 0;

    private Context mContext;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private FragmentTabPagerAdapter mPagerAdapter;

    /**
     * @param context
     */
    public FragmentTabHelper(Context context) {
        mContext = context;
    }

    /**
     * @param pagerAdapter
     *            the pagerAdapter to set
     */
    public void setPagerAdapter(FragmentTabPagerAdapter pagerAdapter) {
        mPagerAdapter = pagerAdapter;
        init();
    }

    private void init() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.

        // Set up the ViewPager with the sections adapter.
        mViewPager = mPagerAdapter.getViewPager();
        mViewPager.setAdapter(mPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(this);

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            View tab = mPagerAdapter.getTab(i);
            tab.setTag(i);
            tab.setOnClickListener(this);
        }

        setTabBackground(0);
    }

    /**
     * @param position
     */
    private void setTabBackground(int position) {
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            View tab = mPagerAdapter.getTab(i);
            if (i == position) {
                tab.setBackgroundColor(getSelectedBackground());
            } else {
                tab.setBackgroundColor(getDefaultBackground());
            }
        }
    }

    private int getDefaultBackground() {
        if (defaultBackground == 0) {
            defaultBackground = mContext.getResources().getColor(R.color.azure);
        }
        return defaultBackground;
    }

    private int getSelectedBackground() {
        if (selectedBackground == 0) {
            selectedBackground = mContext.getResources().getColor(
                    R.color.g_blue);
        }
        return selectedBackground;
    }

    @Override
    public void onClick(View v) {
        selectTab(v);
    }

    private void selectTab(View v) {
        int position = (Integer) v.getTag();
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        Log.d(TAG, "tab selected -->" + position);
        mViewPager.setCurrentItem(position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
            int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        setTabBackground(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public static abstract class FragmentTabPagerAdapter extends
            FragmentPagerAdapter {

        /**
         * @param fm
         */
        public FragmentTabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public abstract int getTabId(int position);

        public abstract View getTab(int position);

        public abstract ViewPager getViewPager();

    }
}
