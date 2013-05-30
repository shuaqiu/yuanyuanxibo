/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.shuaqiu.common.HttpUtil;
import com.shuaqiu.yuanyuanxibo.API.Status;
import com.shuaqiu.yuanyuanxibo.Defs;
import com.shuaqiu.yuanyuanxibo.HttpCursor;
import com.shuaqiu.yuanyuanxibo.HttpCursor.CursorPair;
import com.shuaqiu.yuanyuanxibo.HttpCursorKeeper;
import com.shuaqiu.yuanyuanxibo.StateKeeper;
import com.shuaqiu.yuanyuanxibo.content.DatabaseHelper;

class StatusDownloader implements Runnable {

    private static final String TAG = "statusdownload";

    private static final String STATUS_ID = "id";

    private Context mContext;

    public StatusDownloader(Context context) {
        mContext = context;
    }

    @Override
    public void run() {
        Log.d(TAG, "prepare to download");

        Bundle params = new Bundle();
        String accessToken = StateKeeper.accessToken.getAccessToken();
        params.putString("access_token", accessToken);

        HttpCursor httpCursor = HttpCursorKeeper.read(mContext,
                HttpCursor.Type.STATUS);
        if (httpCursor != null && httpCursor.getPairs().length > 0) {
            long max = httpCursor.getPairs()[0].getMax();
            if (max > 0) {
                params.putLong("since_id", max);
            }
        }

        Log.d(TAG, "start to download");
        String respText = HttpUtil.httpGet(Status.FRIEND_TIMELINE, params);
        Log.d(TAG, "downloaded: " + respText);

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

        saveHttpCursor(httpCursor, data);

        saveStatus(data);
        sendBoardcast(respText);
    }

    private void saveHttpCursor(HttpCursor httpCursor, JSONObject data) {
        long max = getMax(data);
        long min = data.optLong("next_cursor");
        httpCursor
                .prepend(new CursorPair(System.currentTimeMillis(), min, max));
        HttpCursorKeeper.save(mContext, httpCursor);
    }

    private long getMax(JSONObject data) {
        long max = data.optLong("previous_cursor");
        if (max > 0) {
            return max;
        }
        JSONArray statuses = data.optJSONArray("statuses");
        if (statuses.length() == 0) {
            return max;
        }
        JSONObject status = statuses.optJSONObject(0);
        long id = status.optLong(STATUS_ID);
        return id;
    }

    /**
     * @param data
     */
    private void saveStatus(JSONObject data) {
        JSONArray statuses = data.optJSONArray("statuses");
        if (statuses.length() == 0) {
            return;
        }

        Log.d(TAG, "write status data to database");
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
        Log.d(TAG, "send boardcast");
        Intent intent = new Intent(Defs.NEW_STATUS);
        intent.putExtra("data", respText);
        // mContext.sendBroadcast(intent);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}