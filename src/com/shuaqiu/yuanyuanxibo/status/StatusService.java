package com.shuaqiu.yuanyuanxibo.status;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.shuaqiu.common.HttpUtil;
import com.shuaqiu.yuanyuanxibo.API.Status;
import com.shuaqiu.yuanyuanxibo.StateKeeper;
import com.shuaqiu.yuanyuanxibo.content.DatabaseHelper;

public class StatusService extends Service {

    private long delay = 5 * 60;

    private ScheduledExecutorService mThreadPoll;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mThreadPoll = Executors.newScheduledThreadPool(1);

        StatusDownloader command = new StatusDownloader(this);
        mThreadPoll.scheduleWithFixedDelay(command, 0, delay, TimeUnit.SECONDS);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mThreadPoll.shutdown();
    }

    private class StatusDownloader implements Runnable {

        private static final String TAG = "statusdownload";

        private static final String NEW_STATUS = "";

        private static final String STATUS_ID = "id";

        private Context mContext;

        public StatusDownloader(Context context) {
            mContext = context;
        }

        @Override
        public void run() {
            Log.d(TAG, ".............to download status");

            Bundle params = new Bundle();
            String accessToken = StateKeeper.accessToken.getAccessToken();
            params.putString("access_token", accessToken);

            String respText = HttpUtil.httpGet(Status.FRIEND_TIMELINE, params);

            if (respText == null) {
                return;
            }

            JSONObject data = null;
            try {
                data = new JSONObject(respText);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                return;
            }

            saveStatus(data);
            sendBoardcast(respText);
        }

        /**
         * @param data
         */
        private void saveStatus(JSONObject data) {
            JSONArray statuses = data.optJSONArray("statuses");

            DatabaseHelper dbHelper = new DatabaseHelper(mContext);
            dbHelper.openForWrite();

            for (int i = 0; i < statuses.length(); i++) {
                JSONObject status = statuses.optJSONObject(i);
                long id = status.optLong(STATUS_ID);

                ContentValues values = new ContentValues(3);
                values.put(DatabaseHelper.Status.ID, id);
                values.put(DatabaseHelper.Status.CONTENT, status.toString());
                values.put(DatabaseHelper.Status.READED, 0);
                dbHelper.saveOrUpdate(DatabaseHelper.Status.TABLE_NAME, values);
            }
            dbHelper.close();
        }

        /**
         * @param respText
         */
        private void sendBoardcast(String respText) {
            Intent intent = new Intent(NEW_STATUS);
            intent.putExtra("data", respText);
            mContext.sendBroadcast(intent);
        }
    }
}
