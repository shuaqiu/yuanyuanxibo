/**
 * 
 */
package com.shuaqiu.common.task;

import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;

import com.shuaqiu.common.HttpUtil;
import com.shuaqiu.common.InputStreamHandler;

/**
 * @author shuaqiu May 2, 2013
 */
public class AsyncHttpPostTask<Result> extends AsyncTask<String, Void, Result> {
    private Bundle mArgs = null;
    private InputStreamHandler<Result> mHandler = null;

    public AsyncHttpPostTask(Bundle args, InputStreamHandler<Result> handler) {
        mArgs = args;
        mHandler = handler;
    }

    @Override
    protected Result doInBackground(String... params) {
        if (params == null || params.length == 0) {
            return null;
        }
        URL url = HttpUtil.parseUrl(params[0]);
        return HttpUtil.httpPost(url, mArgs, mHandler);
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
    }
}
