package com.shuaqiu.yuanyuanxibo;

import java.util.HashMap;
import java.util.Map;
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

    private static final long DEFAULT_PERIOD = 60 * 10;

    private long period = DEFAULT_PERIOD;

    private boolean isRunning = false;

    private ScheduledExecutorService mThreadPoll;

    private Map<String, ScheduledFuture<?>> futures;

    private BroadcastReceiver mWifiReceiver;
    private BroadcastReceiver mImageTaskReceiver;

    private SharedPreferences mPref;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        mThreadPoll = Executors.newScheduledThreadPool(1);
        futures = new HashMap<String, ScheduledFuture<?>>();

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

        startStatusTask();

        //
        FriendshipTask friendshipTask = new FriendshipTask(this);
        mThreadPoll.schedule(friendshipTask, DELAY, TimeUnit.SECONDS);

        registerWifiReceiver();

        return START_NOT_STICKY;
    }

    /**
     * 根據配置, 啟動微博的下載任務
     */
    private void startStatusTask() {
        String key = "statusFuture";

        ScheduledFuture<?> future = futures.remove(key);
        cancel(future, false);

        if (isPrefetchData()) {
            StatusDownloader command = new StatusDownloader(this);
            future = mThreadPoll.scheduleWithFixedDelay(command, DELAY, period,
                    TimeUnit.SECONDS);
            futures.put("statusFuture", future);

            if (isPrefetchImage()) {
                registerImageTaskReceiver();
            }
        }
    }

    /**
     * 是否下載微博數據
     * 
     * @return
     */
    private boolean isPrefetchData() {
        String key = "prefetch_mobile";
        if (StateKeeper.isWifi) {
            key = "prefetch_wifi";
        }
        return mPref.getBoolean(key, true);
    }

    /**
     * 是否下載微博圖片, 如果不下載微博數據, 則該選項不生效
     * 
     * @return
     */
    private boolean isPrefetchImage() {
        String key = "prefetch_image_mobile";
        if (StateKeeper.isWifi) {
            key = "prefetch_image_wifi";
        }
        return mPref.getBoolean(key, true);
    }

    /**
     * 註冊微博下載完成的receiver, 獲取其中的圖片URL, 并進行下載
     */
    private void registerImageTaskReceiver() {
        mImageTaskReceiver = new ImageTaskReceiver(this, mThreadPoll);
        IntentFilter filter = new IntentFilter(Status.NEW_RECEIVED);
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mImageTaskReceiver, filter);
    }

    /**
     * 獲取下載週期的設置
     * 
     * @return
     */
    private long getPeriod() {
        String key = "prefetch_interval_mobile";
        if (StateKeeper.isWifi) {
            key = "prefetch_interval_wifi";
        }
        String str = mPref.getString(key, "" + DEFAULT_PERIOD);
        return Long.parseLong(str);
    }

    /**
     * 註冊網絡狀態變化的receiver, 監聽網絡的變化
     */
    private void registerWifiReceiver() {
        mWifiReceiver = new WifiReceiver();
        IntentFilter filter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mWifiReceiver, filter);
    }

    @Override
    public void onDestroy() {
        isRunning = false;

        for (ScheduledFuture<?> future : futures.values()) {
            cancel(future, true);
        }
        mThreadPoll.shutdown();

        unregisterReceiver(mWifiReceiver);

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.unregisterReceiver(mImageTaskReceiver);
    }

    private void cancel(ScheduledFuture<?> future, boolean mayInterruptIfRunning) {
        if (future != null && !future.isDone()) {
            future.cancel(mayInterruptIfRunning);
        }
    }
}
