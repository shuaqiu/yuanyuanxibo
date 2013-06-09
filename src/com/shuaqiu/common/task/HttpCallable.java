/**
 * 
 */
package com.shuaqiu.common.task;

import java.util.concurrent.Callable;

import android.os.Bundle;
import android.util.Log;

/**
 * @author shuaqiu Jun 9, 2013
 */
public abstract class HttpCallable implements Callable<String> {

    private static final String TAG = "HttpCallable";

    protected String mUrl;
    protected Bundle mParam;

    protected HttpCallable(String url, Bundle param) {
        mUrl = url;
        mParam = param;
    }

    @Override
    public String call() throws Exception {
        if (mUrl == null) {
            Log.w(TAG, "try to post to a null url, returned");
            return null;
        }
        return httpCall();
    }

    /**
     * @return
     */
    protected abstract String httpCall();

}