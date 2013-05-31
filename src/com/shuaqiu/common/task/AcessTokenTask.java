/**
 * 
 */
package com.shuaqiu.common.task;

import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;

import com.shuaqiu.common.util.HttpUtil;
import com.shuaqiu.yuanyuanxibo.auth.AuthListener;

/**
 * @author shuaqiu May 2, 2013
 */
public class AcessTokenTask extends AsyncTask<String, Void, String> {
    private Bundle mArgs = null;
    private AuthListener mAuthListener;

    public AcessTokenTask(Bundle args, AuthListener authListener) {
        mArgs = args;
        mAuthListener = authListener;
    }

    @Override
    protected String doInBackground(String... params) {
        if (params == null || params.length == 0) {
            return null;
        }
        URL url = HttpUtil.parseUrl(params[0]);
        return HttpUtil.httpPost(url, mArgs);
    }

    @Override
    protected void onPostExecute(String result) {
        mAuthListener.onComplete(result);
    }
}
