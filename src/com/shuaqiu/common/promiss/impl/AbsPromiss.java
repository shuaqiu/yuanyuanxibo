/**
 * 
 */
package com.shuaqiu.common.promiss.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import android.util.Log;

import com.shuaqiu.common.promiss.AlwaysCallback;
import com.shuaqiu.common.promiss.Callback;
import com.shuaqiu.common.promiss.Promiss;
import com.shuaqiu.common.promiss.State;
import com.shuaqiu.common.promiss.Transfer;

/**
 * @author shuaqiu 2013-6-6
 */
public abstract class AbsPromiss<Done, Fail> implements Promiss<Done, Fail> {

    private static final String TAG = null;

    protected final transient ReentrantLock lock = new ReentrantLock();

    protected final List<Callback<Done>> doneCallbacks = new CopyOnWriteArrayList<Callback<Done>>();
    protected final List<Callback<Fail>> failCallbacks = new CopyOnWriteArrayList<Callback<Fail>>();
    protected final List<AlwaysCallback<Done, Fail>> alwaysCallbacks = new CopyOnWriteArrayList<AlwaysCallback<Done, Fail>>();

    protected volatile State state = State.PENDING;

    protected Done resolveResult;
    protected Fail rejectResult;

    @Override
    public State getState() {
        return state;
    }

    @Override
    public boolean isPending() {
        return state == State.PENDING;
    }

    @Override
    public boolean isResolved() {
        return state == State.RESOLVED;
    }

    @Override
    public boolean isRejected() {
        return state == State.REJECTED;
    }

    @Override
    public Promiss<Done, Fail> then(Callback<Done> doneCallback) {
        return done(doneCallback);
    }

    @Override
    public Promiss<Done, Fail> then(Callback<Done> doneCallback,
            Callback<Fail> failCallback) {
        done(doneCallback);
        fail(failCallback);
        return this;
    }

    @Override
    public <DoneOut, FailOut> Promiss<DoneOut, FailOut> then(
            Transfer<Done, DoneOut> transfer) {
        return then(transfer, null);
    }

    @Override
    public <DoneOut, FailOut> Promiss<DoneOut, FailOut> then(
            Transfer<Done, DoneOut> doneTransfer,
            Transfer<Fail, FailOut> failTransfer) {
        return new TransferedPromise<Done, Fail, DoneOut, FailOut>(this,
                doneTransfer, failTransfer);
    }

    @Override
    public Promiss<Done, Fail> done(Callback<Done> callback) {
        doneCallbacks.add(callback);

        boolean isResolved;
        Done result;

        lock.lock();
        try {
            isResolved = isResolved();

            result = resolveResult;
        } finally {
            lock.unlock();
        }
        if (isResolved) {
            callback.apply(result);
        }

        return this;
    }

    @Override
    public Promiss<Done, Fail> fail(Callback<Fail> callback) {
        failCallbacks.add(callback);

        boolean isRejected;
        Fail result;

        lock.lock();
        try {
            isRejected = isRejected();
            result = rejectResult;
        } finally {
            lock.unlock();
        }

        if (isRejected) {
            callback.apply(result);
        }

        return this;
    }

    @Override
    public Promiss<Done, Fail> always(AlwaysCallback<Done, Fail> callback) {
        alwaysCallbacks.add(callback);

        final State state;
        final Done resolveResult;
        final Fail rejectResult;

        lock.lock();
        try {
            state = getState();
            resolveResult = this.resolveResult;
            rejectResult = this.rejectResult;
        } finally {
            lock.unlock();
        }

        if (state != State.PENDING) {
            callback.apply(state, resolveResult, rejectResult);
        }

        return this;
    }

    protected void triggerDone(Done resolved) {
        for (Callback<Done> callback : doneCallbacks) {
            try {
                callback.apply(resolved);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    protected void triggerFail(Fail rejected) {
        for (Callback<Fail> callback : failCallbacks) {
            try {
                callback.apply(rejected);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    protected void triggerAlways(State state, Done resolve, Fail reject) {
        for (AlwaysCallback<Done, Fail> callback : alwaysCallbacks) {
            try {
                callback.apply(state, resolve, reject);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }
}
