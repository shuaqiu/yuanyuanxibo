package com.shuaqiu.yuanyuanxibo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.shuaqiu.yuanyuanxibo.Actions.Status;
import com.shuaqiu.yuanyuanxibo.friend.FriendshipTask;
import com.shuaqiu.yuanyuanxibo.status.ImageTaskReceiver;
import com.shuaqiu.yuanyuanxibo.status.StatusDownloader;

/**
 * @author shuaqiu Jun 19, 2013
 */
public class MainService extends Service {
    static final String TAG = "PrefetchService";

    private static final long DELAY = 30;

    private long period = 10 * 60;

    private boolean isRunning = false;

    private ScheduledExecutorService mThreadPoll;

    private List<ScheduledFuture<?>> futures;

    private WifiReceiver mWifiReceiver;

    private SharedPreferences mPref;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        mThreadPoll = Executors.newScheduledThreadPool(1);
        futures = new ArrayList<ScheduledFuture<?>>();

        mPref = PreferenceManager.getDefaultSharedPreferences(this);

        period = getPeriod();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isRunning) {
            Log.d(TAG, "service is running");
            return START_NOT_STICKY;
        }

        isRunning = true;

        if (isPrefetchData()) {
            StatusDownloader command = new StatusDownloader(this);
            futures.add(mThreadPoll.scheduleWithFixedDelay(command, DELAY,
                    period, TimeUnit.SECONDS));

            if (isPrefetchImage()) {
                addStatusDownloadedReceiver();
            }
        }

        //
        FriendshipTask friendshipTask = new FriendshipTask(this);
        mThreadPoll.schedule(friendshipTask, DELAY, TimeUnit.SECONDS);

        mWifiReceiver = new WifiReceiver();
        registerReceiver(mWifiReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));

        return START_NOT_STICKY;
    }

    /**
     * 
     */
    private void addStatusDownloadedReceiver() {
        BroadcastReceiver receiver = new ImageTaskReceiver(this,
                mThreadPoll);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Status.NEW_RECEIVED);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                filter);
    }

    private long getPeriod() {
        String key = "prefetch_interval_mobile";
        if (StateKeeper.isWifi) {
            key = "prefetch_interval_wifi";
        }
        return mPref.getLong(key, 10 * 60);
    }

    private boolean isPrefetchData() {
        String key = "prefetch_mobile";
        if (StateKeeper.isWifi) {
            key = "prefetch_wifi";
        }
        return mPref.getBoolean(key, true);
    }

    private boolean isPrefetchImage() {
        String key = "prefetch_image_mobile";
        if (StateKeeper.isWifi) {
            key = "prefetch_image_wifi";
        }
        return mPref.getBoolean(key, true);
    }

    @Override
    public void onDestroy() {
        isRunning = false;

        for (ScheduledFuture<?> future : futures) {
            if (future != null && !future.isDone()) {
                future.cancel(true);
            }
        }
        mThreadPoll.shutdown();

        unregisterReceiver(mWifiReceiver);
    }
}
