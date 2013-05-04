package com.shuaqiu.yuanyuanxibo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.shuaqiu.yuanyuanxibo.auth.AccessTokenKeeper;
import com.shuaqiu.yuanyuanxibo.auth.AuthListener;
import com.shuaqiu.yuanyuanxibo.auth.LoginFragment;
import com.shuaqiu.yuanyuanxibo.auth.Oauth2AccessToken;

public class MainActivity extends FragmentActivity {

    protected static final String TAG = "main";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        GrantedAccessToken.accessToken = AccessTokenKeeper.read(this);
        if (GrantedAccessToken.accessToken.isSessionValid()) {
            initMainView();
        } else {
            initLoginView();
        }
    }

    private void initLoginView() {
        setContentView(R.layout.activity_login);
        FragmentManager fragmentManager = getSupportFragmentManager();
        LoginFragment loginFragment = (LoginFragment) fragmentManager
                .findFragmentById(R.id.login);
        loginFragment.setAuthListener(new MainAuthListener(this));
    }

    private void initMainView() {
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this,
                getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // actionBar.setSelectedNavigationItem(position);
                        onTabSelected(position);
                    }
                });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            findViewById(SectionsPagerAdapter.mPageTitileId[i])
                    .setOnClickListener(new ActionBarClickListener(i));
        }
    }

    /**
     * @author shuaqiu May 4, 2013
     */
    private final class MainAuthListener implements AuthListener {
        private Context mContext;

        public MainAuthListener(Context context) {
            mContext = context;
        }

        @Override
        public void onComplete(String responseText) {
            GrantedAccessToken.accessToken = new Oauth2AccessToken(responseText);
            AccessTokenKeeper.save(mContext, GrantedAccessToken.accessToken);
            initMainView();
        }
    }

    private class ActionBarClickListener implements OnClickListener {
        private int position;

        public ActionBarClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            onTabSelected(position);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void onTabSelected(int position) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        System.err.println("tab selected -->" + position);
        mViewPager.setCurrentItem(position);
    }
}