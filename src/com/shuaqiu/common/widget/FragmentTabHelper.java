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

    private static final String TAG = "FragmentTabHelper";

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

    public FragmentTabPagerAdapter getPagerAdapter() {
        return mPagerAdapter;
    }

    private void init() {
        mViewPager = mPagerAdapter.getViewPager();
        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.setOnPageChangeListener(this);

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
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
            setDefaultBackground(R.color.azure);
        }
        return defaultBackground;
    }

    private int getSelectedBackground() {
        if (selectedBackground == 0) {
            setSelectedBackground(R.color.g_blue);
        }
        return selectedBackground;
    }

    /**
     * 設置Tab 的默認背景色
     * 
     * @param colorId
     *            背景色的id
     */
    public void setDefaultBackground(int colorId) {
        defaultBackground = mContext.getResources().getColor(colorId);
    }

    /**
     * 設在選中的Tab 的背景色
     * 
     * @param colorId
     *            背景色的ID
     */
    public void setSelectedBackground(int colorId) {
        selectedBackground = mContext.getResources().getColor(colorId);
    }

    // ----------- Tab 的點擊事件 ------------------------------

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        Log.d(TAG, "tab selected -->" + position);
        mViewPager.setCurrentItem(position);
    }

    // --------------ViewPager 的切換事件 -----------------------
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

    /**
     * 在FragmentPagerAdapter 的基礎上, 增加Tab 和ViewPager 的獲取
     * 
     */
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
