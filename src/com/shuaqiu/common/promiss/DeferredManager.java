/**
 * 
 */
package com.shuaqiu.common.promiss;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import com.shuaqiu.common.promiss.impl.DeferredTask;

/**
 * @author shuaqiu 2013-6-6
 */
public class DeferredManager {

    public static <Done, Fail> Promiss<Done, Fail> when(
            Promiss<Done, Fail> promiss) {
        return promiss;
    }

    public static Promiss<Void, Throwable> when(Runnable runnable) {
        return new DeferredTask<Void>(Executors.callable(runnable, (Void) null));
    }

    public static <Done> Promiss<Done, Throwable> when(Callable<Done> callable) {
        return new DeferredTask<Done>(callable);
    }
}
