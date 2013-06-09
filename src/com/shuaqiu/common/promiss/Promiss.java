/**
 * 
 */
package com.shuaqiu.common.promiss;

/**
 * @author shuaqiu 2013-6-6
 */
public interface Promiss<Done, Fail> {

    State getState();

    boolean isPending();

    boolean isResolved();

    boolean isRejected();

    Promiss<Done, Fail> then(Callback<Done> callback);

    Promiss<Done, Fail> then(Callback<Done> doneCallback,
            Callback<Fail> failCallback);

    <DoneOut, FailOut> Promiss<DoneOut, FailOut> then(
            Transfer<Done, DoneOut> transfer);

    <DoneOut, FailOut> Promiss<DoneOut, FailOut> then(
            Transfer<Done, DoneOut> doneTransfer,
            Transfer<Fail, FailOut> failTransfer);

    Promiss<Done, Fail> done(Callback<Done> callback);

    Promiss<Done, Fail> fail(Callback<Fail> callback);

    Promiss<Done, Fail> always(AlwaysCallback<Done, Fail> callback);
}
