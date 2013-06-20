package com.shuaqiu.yuanyuanxibo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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

    private static final int WHAT_STATUS_TASK = 1;

    private static final long DELAY = 30 * 1000;

    private static final long DEFAULT_PERIOD = 10 * 60 * 1000;

    private boolean isRunning = false;

    private BroadcastReceiver mWifiReceiver;
    private BroadcastReceiver mImageTaskReceiver;

    private SharedPreferences mPref;

    private Handler mHandler = new Handler();

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        mHandler = new Handler();
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isRunning) {
            Log.d(TAG, "service is running");
            return START_NOT_STICKY;
        }

        isRunning = true;

        handleStatusTask();

        //
        FriendshipTask friendshipTask = new FriendshipTask(this);
        mHandler.postDelayed(friendshipTask, DELAY * 1000);

        registerWifiReceiver();

        return START_NOT_STICKY;
    }

    /**
     * 根據配置, 啟動微博的下載任務
     */
    private void handleStatusTask() {
        if (isPrefetchData()) {
            final StatusDownloader command = new StatusDownloader(this);

            Message message = Message.obtain(mHandler, new Runnable() {

                @Override
                public void run() {
                    command.run();

                    if (isPrefetchData()) {
                        mHandler.postDelayed(this, getPeriod());

                        if (isPrefetchImage()) {
                            registerImageTaskReceiver();
                        } else {
                            unregisterImageTaskReceiver();
                        }
                    }
                }
            });
            message.what = WHAT_STATUS_TASK;
            mHandler.sendMessageDelayed(message, DELAY);

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
        if (mImageTaskReceiver != null) {
            return;
        }
        mImageTaskReceiver = new ImageTaskReceiver(this, mHandler);
        IntentFilter filter = new IntentFilter(Status.NEW_RECEIVED);
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mImageTaskReceiver, filter);
    }

    /**
     * 
     */
    private void unregisterImageTaskReceiver() {
        if (mImageTaskReceiver == null) {
            return;
        }
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.unregisterReceiver(mImageTaskReceiver);
        mImageTaskReceiver = null;
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
        return Long.parseLong(str) * 1000;
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

        mHandler.removeMessages(WHAT_STATUS_TASK);
        mHandler = null;

        unregisterReceiver(mWifiReceiver);
        unregisterImageTaskReceiver();
    }
}