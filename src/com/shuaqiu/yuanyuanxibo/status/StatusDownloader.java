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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.shuaqiu.yuanyuanxibo.Defs;
import com.shuaqiu.yuanyuanxibo.HttpCursor;
import com.shuaqiu.yuanyuanxibo.HttpCursor.CursorPair;
import com.shuaqiu.yuanyuanxibo.HttpCursorKeeper;
import com.shuaqiu.yuanyuanxibo.content.DatabaseHelper;

class StatusDownloader implements Runnable {

    private static final String TAG = "statusdownload";

    private static final String STATUS_ID = "id";

    private Context mContext;
    private boolean isBroadcast = true;

    public StatusDownloader(Context context) {
        mContext = context;
    }

    public void setBroadcast(boolean isBroadcast) {
        this.isBroadcast = isBroadcast;
    }

    @Override
    public void run() {
        HttpCursor httpCursor = HttpCursorKeeper.read(mContext,
                HttpCursor.Type.STATUS);

        StatusDownloadFunction download = StatusDownloadFunction.getInstance();
        String respText = download.apply(httpCursor);

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

        JSONArray statuses = data.optJSONArray("statuses");
        if (statuses.length() == 0) {
            Log.d(TAG, "not data");
            return;
        }

        int savedCount = saveStatus(statuses);
        if (savedCount == 0) {
            Log.d(TAG, "fail to save");
            return;
        }

        saveHttpCursor(httpCursor, data);

        if (isBroadcast) {
            sendBoardcast(savedCount);
        }
    }

    private void saveHttpCursor(HttpCursor httpCursor, JSONObject data) {
        long min = getMin(data);
        long max = getMax(data);
        httpCursor.prepend(new CursorPair(min, max));
        HttpCursorKeeper.save(mContext, httpCursor);
    }

    private long getMin(JSONObject data) {
        return data.optLong("next_cursor");
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
        long id = status.optLong("id");
        return id;
    }

    /**
     * @param data
     * @return
     */
    private int saveStatus(JSONArray statuses) {
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

        return statuses.length();
    }

    /**
     * @param savedCount
     */
    private void sendBoardcast(int savedCount) {
        Log.d(TAG, "send boardcast");
        Intent intent = new Intent(Defs.NEW_STATUS);
        intent.putExtra("count", savedCount);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}