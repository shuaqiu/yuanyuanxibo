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

import com.shuaqiu.common.widget.FragmentTabHelper;
import com.shuaqiu.yuanyuanxibo.auth.AccessTokenKeeper;
import com.shuaqiu.yuanyuanxibo.auth.AuthListener;
import com.shuaqiu.yuanyuanxibo.auth.LoginFragment;
import com.shuaqiu.yuanyuanxibo.auth.Oauth2AccessToken;

public class MainActivity extends FragmentActivity implements OnClickListener {

    private static final String TAG = "main";

    private ViewHolder mHolder;

    private FragmentTabHelper mTabHelper;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 初始化屬性設置
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // 首先初始化一下狀態
        StateKeeper.init(this);

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

        initViewHolder();

        initTab();
        initAction();
        startService();
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
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager(), mHolder);
        mTabHelper.setPagerAdapter(mSectionsPagerAdapter);
    }

    private void initAction() {
        mHolder.mNewStatus.setOnClickListener(this);
        mHolder.mRefresh.setOnClickListener(this);
    }

    private void startService() {
        Intent service = new Intent(this, MainService.class);
        startService(service);
    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.new_status:
            newStatus();
            break;
        case R.id.refresh:
            refresh();
            break;
        default:
            break;
        }
    }

    /**
     * 新建微博
     */
    private void newStatus() {
    }

    /**
     * 進行刷新操作
     */
    private void refresh() {
        int currentItem = mHolder.mViewPager.getCurrentItem();
        Fragment item = mSectionsPagerAdapter.getItem(currentItem);
        if (item instanceof Refreshable) {
            ((Refreshable) item).refresh();
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

    static class ViewHolder {
        ViewPager mViewPager;

        // R.string.home, R.string.at_me, R.string.comments, R.string.messages
        View mHome;
        View mAtMe;
        View mComments;
        View mMessages;

        View mNewStatus;
        View mRefresh;

        static ViewHolder from(View v) {
            Object tag = v.getTag();
            if (tag != null && tag instanceof ViewHolder) {
                return (ViewHolder) tag;
            }

            ViewHolder holder = new ViewHolder();
            v.setTag(holder);

            holder.mViewPager = (ViewPager) v.findViewById(R.id.pager);

            holder.mHome = v.findViewById(R.id.home);
            holder.mAtMe = v.findViewById(R.id.at_me);
            holder.mComments = v.findViewById(R.id.comments);
            holder.mMessages = v.findViewById(R.id.messages);

            holder.mNewStatus = v.findViewById(R.id.new_status);
            holder.mRefresh = v.findViewById(R.id.refresh);

            return holder;
        }
    }
}