/**
 * 
 */
package com.shuaqiu.common.promiss.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import android.os.AsyncTask;
import android.util.Log;

/**
 * @author shuaqiu 2013-6-6
 * 
 */
public class DeferredTask<Done, Fail> extends DeferredObject<Done, Fail> {

    private static final String TAG = null;

    private AsyncTask<Void, Void, Done> mTask = null;

    public DeferredTask(final Runnable runnable, Done type) {
        this(Executors.callable(runnable, type));
    }

    public DeferredTask(final Callable<Done> callable) {
        mTask = new AsyncTask<Void, Void, Done>() {
            @Override
            protected Done doInBackground(Void... params) {
                try {
                    return callable.call();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Done result) {
                DeferredTask.this.resolve(result);
            }

            @Override
            protected void onCancelled() {
                DeferredTask.this.reject(null);
            }
        };
        mTask.execute();
    }
}
