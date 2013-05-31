/**
 * 
 */
package com.shuaqiu.common.task;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.shuaqiu.common.util.HttpUtil;

public class AsyncHttpGetTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "httpget";

    private AsyncTaskListener<JSONObject> mListener;
    private Bundle mParams;

    public AsyncHttpGetTask(Bundle params,
            AsyncTaskListener<JSONObject> listener) {
        mParams = params;
        mListener = listener;
    }

    @Override
    protected String doInBackground(String... urls) {
        if (urls == null || urls.length == 0) {
            return null;
        }

        return HttpUtil.httpGet(urls[0], mParams);
    }

    @Override
    protected void onPostExecute(String result) {
        if (result == null) {
            return;
        }
        Log.d(TAG, result);
        try {
            JSONObject data = new JSONObject(result);
            mListener.onPostExecute(data);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}