package com.shuaqiu.yuanyuanxibo.status;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class StatusService extends Service {

    private long delay = 10 * 60;

    private ScheduledExecutorService mThreadPoll;

    private ScheduledFuture<?> future;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mThreadPoll = Executors.newScheduledThreadPool(1);

        StatusDownloader command = new StatusDownloader(this);
        future = mThreadPoll.scheduleWithFixedDelay(command, 30, delay,
                TimeUnit.SECONDS);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (future != null) {
            future.cancel(true);
        }
        mThreadPoll.shutdown();
    }
}
