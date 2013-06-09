/**
 * 
 */
package com.shuaqiu.common.promiss.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;

import android.os.AsyncTask;
import android.util.Log;

import com.shuaqiu.common.promiss.Callback;
import com.shuaqiu.common.promiss.Deferred;

/**
 * @author shuaqiu 2013-6-6
 */
public class DeferredTask<V> extends DeferredObject<V, Throwable> {

    private static final String TAG = null;

    private AsyncTask<Void, Void, V> mTask = null;

    public DeferredTask(final Callable<V> callable) {
        mTask = new AsyncDeferredTask<V>(callable, this);
        mTask.execute();
    }

    /**
     * @author shuaqiu Jun 9, 2013
     */
    private static final class AsyncDeferredTask<V> extends
            AsyncTask<Void, Void, V> {

        private final Callable<V> callable;
        private final Deferred<V, Throwable> deferred;

        private AsyncDeferredTask(Callable<V> callable,
                Deferred<V, Throwable> deferred) {
            this.callable = callable;
            this.deferred = deferred;
        }

        @Override
        protected V doInBackground(Void... params) {
            try {
                return callable.call();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(V result) {
            deferred.resolve(result);
        }

        @Override
        protected void onCancelled() {
            deferred.reject(new CancellationException("task is cancelled"));
        }
    }

    public interface TaskJob<V> extends Callable<V>, Callback<V> {
    }
}
