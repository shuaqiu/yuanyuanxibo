/**
 * 
 */
package com.shuaqiu.common.task;

import com.shuaqiu.common.function.Function;

import android.os.AsyncTask;

/**
 * @author shuaqiu 2013-5-31
 * 
 */
@SuppressWarnings("unchecked")
public class Promise<Param, Result> extends AsyncTask<Param, Void, Result> {

    private Function<Param, Result> mFunction;

    private Function<Result, Void> mExecutor;

    private Promise<Result, ?> mPromise;

    public Promise(Function<Param, Result> function) {
        mFunction = function;
    }

    public void then(Function<Result, Void> listener) {
        mExecutor = listener;
    }

    public <T> Promise<Result, T> then(Promise<Result, T> task) {
        mPromise = task;
        return (Promise<Result, T>) mPromise;
    }

    @Override
    protected Result doInBackground(Param... params) {
        Param param = params == null || params.length == 0 ? null : params[0];
        return mFunction.apply(param);
    }

    @Override
    protected void onPostExecute(Result result) {
        if (mPromise != null) {
            mPromise.execute(result);
        }
        if (mExecutor != null) {
            mExecutor.apply(result);
        }
    }
}
