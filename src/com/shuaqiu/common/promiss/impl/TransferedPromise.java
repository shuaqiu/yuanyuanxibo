/**
 * 
 */
package com.shuaqiu.common.promiss.impl;

import com.shuaqiu.common.promiss.Callback;
import com.shuaqiu.common.promiss.Promiss;
import com.shuaqiu.common.promiss.Transfer;

/**
 * @author shuaqiu 2013-6-6
 * 
 */
public class TransferedPromise<DoneIn, FailIn, DoneOut, FailOut> extends
        DeferredObject<DoneOut, FailOut> implements Promiss<DoneOut, FailOut> {

    /**
     * @param promiss
     * @param doneTransfer
     * @param failTransfer
     */
    public TransferedPromise(final Promiss<DoneIn, FailIn> promiss,
            final Transfer<DoneIn, DoneOut> doneTransfer,
            final Transfer<FailIn, FailOut> failTransfer) {
        promiss.done(new Callback<DoneIn>() {
            @Override
            public void apply(DoneIn result) {
                // assert(doneTransfer != null);
                DoneOut out = null;
                if (doneTransfer != null) {
                    out = doneTransfer.transfer(result);
                }
                TransferedPromise.this.resolve(out);
            }
        });
        promiss.fail(new Callback<FailIn>() {
            @Override
            public void apply(FailIn result) {
                // assert(failTransfer != null);
                FailOut out = null;
                if (doneTransfer != null) {
                    out = failTransfer.transfer(result);
                }
                TransferedPromise.this.reject(out);
            }
        });
    }
}
