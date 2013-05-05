/**
 * 
 */
package com.shuaqiu.yuanyuanxibo;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shuaqiu.yuanyuanxibo.comment.CommentListFragment;
import com.shuaqiu.yuanyuanxibo.status.StatusListFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one
 * of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    public static final int[] mPageTitileId = new int[] { R.id.home,
            R.id.at_me, R.id.comments, R.id.messages };

    public static final int[] sPageTitile = new int[] { R.string.home,
            R.string.at_me, R.string.comments, R.string.messages };

    private Fragment[] items;

    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        items = new Fragment[] { new StatusListFragment(),
                new CommentListFragment(), new Fragment(), new Fragment() };
    }

    @Override
    public Fragment getItem(int position) {
        return items[position];
    }

    @Override
    public int getCount() {
        return sPageTitile.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getString(sPageTitile[position]);
    }
}