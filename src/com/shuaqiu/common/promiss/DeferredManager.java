/**
 * 
 */
package com.shuaqiu.common.promiss;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author shuaqiu 2013-6-6
 * 
 */
public interface DeferredManager {

    <Done, Fail> Promiss<Done, Fail> when(Promiss<Done, Fail> promiss);

    Promiss<Void, Throwable> when(Runnable runnable);

    <Done> Promiss<Done, Throwable> when(Callable<Done> callable);

    <Done> Promiss<Done, Throwable> when(Future<Done> future);
}
