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
    public static final int[] TITLE_IDS = new int[] { R.id.home, R.id.at_me,
            R.id.comments, R.id.messages };

    public static final int[] TITIES = new int[] { R.string.home,
            R.string.at_me, R.string.comments, R.string.messages };

    private Fragment[] items;

    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        items = new Fragment[] { new StatusListFragment(), new Fragment(),
                new Fragment(), new Fragment() };
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
            return new Fragment();
        case 2:
            return new CommentListFragment();
        case 3:
            return new Fragment();
        }
        return new Fragment();
    }

    @Override
    public int getCount() {
        return TITIES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getString(TITIES[position]);
    }
}