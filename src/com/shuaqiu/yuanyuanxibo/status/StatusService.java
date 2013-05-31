package com.shuaqiu.yuanyuanxibo.status;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;

import com.shuaqiu.yuanyuanxibo.WifiReceiver;

public class StatusService extends Service {

    static boolean isRunning = false;

    private long delay = 10 * 60;

    private ScheduledExecutorService mThreadPoll;

    private ScheduledFuture<?> future;

    private WifiReceiver mWifiReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;

        mThreadPoll = Executors.newScheduledThreadPool(1);

        StatusDownloader command = new StatusDownloader(this);
        future = mThreadPoll.scheduleWithFixedDelay(command, 30, delay,
                TimeUnit.SECONDS);

        mWifiReceiver = new WifiReceiver();
        registerReceiver(mWifiReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));

        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        isRunning = false;

        if (future != null) {
            future.cancel(true);
        }
        mThreadPoll.shutdown();

        unregisterReceiver(mWifiReceiver);
    }
}
