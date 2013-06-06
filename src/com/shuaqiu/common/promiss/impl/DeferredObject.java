/**
 * 
 */
package com.shuaqiu.common.promiss.impl;

import com.shuaqiu.common.promiss.Deferred;
import com.shuaqiu.common.promiss.Promiss;
import com.shuaqiu.common.promiss.State;

/**
 * @author shuaqiu 2013-6-6
 * 
 */
public class DeferredObject<Done, Fail> extends AbsPromiss<Done, Fail>
        implements Deferred<Done, Fail> {

    @Override
    public Deferred<Done, Fail> resolve(Done result) {
        final State state;
        lock.lock();
        try {
            if (!isPending()) {
                throw new IllegalStateException(
                        "Deferred already finished, can't resolv again");
            }
            this.state = state = State.RESOLVED;
            resolveResult = result;
        } finally {
            lock.unlock();
        }

        try {
            triggerDone(result);
        } finally {
            triggerAlways(state, result, null);
        }
        return this;
    }

    @Override
    public Deferred<Done, Fail> reject(Fail result) {
        lock.lock();
        try {
            if (!isPending()) {
                throw new IllegalStateException(
                        "Deferred already finished, can't reject again");
            }
            state = state = State.REJECTED;
            rejectResult = result;
        } finally {
            lock.unlock();
        }

        try {
            triggerFail(result);
        } finally {
            triggerAlways(state, null, result);
        }
        return this;
    }

    @Override
    public Promiss<Done, Fail> promiss() {
        return this;
    }

}
