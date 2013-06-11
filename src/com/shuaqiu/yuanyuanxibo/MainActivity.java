package com.shuaqiu.yuanyuanxibo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.shuaqiu.yuanyuanxibo.auth.AccessTokenKeeper;
import com.shuaqiu.yuanyuanxibo.auth.AuthListener;
import com.shuaqiu.yuanyuanxibo.auth.LoginFragment;
import com.shuaqiu.yuanyuanxibo.auth.Oauth2AccessToken;

public class MainActivity extends FragmentActivity implements OnClickListener {

    private static final String TAG = "main";

    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private int defaultBackground = 0;
    private int selectedBackground = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 初始化屬性設置
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // 首先初始化一下Wifi 的狀態
        StateKeeper.isWifi = StateKeeper.isWifi(this);

        StateKeeper.accessToken = AccessTokenKeeper.read(this);
        if (StateKeeper.accessToken.isSessionValid()) {
            initMainView();
        } else {
            Log.d(TAG, "access token is invalid, need login!");
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
        mViewPager.setOnPageChangeListener(new MainPageChangeListener());

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            View tab = findViewById(SectionsPagerAdapter.TITLE_IDS[i]);
            tab.setTag(i);
            tab.setOnClickListener(this);
        }

        setTabBackground(0);

        findViewById(R.id.refresh).setOnClickListener(this);
    }

    // private boolean hasNewStatus() {
    // Intent intent = getIntent();
    // return intent != null && Defs.NEW_STATUS.equals(intent.getAction());
    // }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.settings:
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * @param position
     */
    private void setTabBackground(int position) {
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            View tab = findViewById(SectionsPagerAdapter.TITLE_IDS[i]);
            if (i == position) {
                tab.setBackgroundColor(getSelectedBackground());
            } else {
                tab.setBackgroundColor(getDefaultBackground());
            }
        }
    }

    private int getDefaultBackground() {
        if (defaultBackground == 0) {
            defaultBackground = getResources().getColor(R.color.azure);
        }
        return defaultBackground;
    }

    private int getSelectedBackground() {
        if (selectedBackground == 0) {
            selectedBackground = getResources().getColor(R.color.g_blue);
        }
        return selectedBackground;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.refresh:
            refresh();
            break;
        default:
            selectTab(v);
            break;
        }
    }

    private void selectTab(View v) {
        // @see SectionsPagerAdapter#TITLE_IDS
        int position = (Integer) v.getTag();
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        Log.d(TAG, "tab selected -->" + position);
        mViewPager.setCurrentItem(position);
    }

    /**
     * 進行刷新操作
     */
    private void refresh() {
        int currentItem = mViewPager.getCurrentItem();
        Fragment item = mSectionsPagerAdapter.getItem(currentItem);
        if (item instanceof Refreshable) {
            ((Refreshable) item).refresh();
        }
    }

    /**
     * @author shuaqiu 2013-5-6
     */
    private final class MainPageChangeListener extends
            ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            setTabBackground(position);
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
            StateKeeper.accessToken = new Oauth2AccessToken(responseText);
            AccessTokenKeeper.save(mContext, StateKeeper.accessToken);
            initMainView();
        }
    }
}