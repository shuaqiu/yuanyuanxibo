/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import android.os.Bundle;
import android.util.Log;

import com.shuaqiu.common.HttpUtil;
import com.shuaqiu.common.task.Function;
import com.shuaqiu.yuanyuanxibo.API.Status;
import com.shuaqiu.yuanyuanxibo.HttpCursor;
import com.shuaqiu.yuanyuanxibo.StateKeeper;

/**
 * @author shuaqiu 2013-5-31
 * 
 */
public class StatusDownloadFunction implements Function<HttpCursor, String> {

    private static final String TAG = "StatusDownloadFunction";

    private static StatusDownloadFunction instance = null;

    public static StatusDownloadFunction getInstance() {
        if (instance == null) {
            instance = new StatusDownloadFunction();
        }
        return instance;
    }

    private StatusDownloadFunction() {
    }

    @Override
    public String apply(HttpCursor httpCursor) {
        Log.d(TAG, "prepare to download");

        Bundle params = new Bundle();
        String accessToken = StateKeeper.accessToken.getAccessToken();
        params.putString("access_token", accessToken);

        long max = getMaxCursor(httpCursor);
        if (max > 0) {
            params.putLong("since_id", max);
        }

        Log.d(TAG, "start to download");
        String respText = HttpUtil.httpGet(Status.FRIEND_TIMELINE, params);
        Log.d(TAG, "downloaded: " + respText);

        return respText;
    }

    private long getMaxCursor(HttpCursor httpCursor) {
        if (httpCursor == null || httpCursor.getPairs().length == 0) {
            return 0;
        }
        return httpCursor.getPairs()[0].getMax();
    }
}
