/**
 * 
 */
package com.shuaqiu.common.promiss;

/**
 * @author shuaqiu 2013-6-6
 * 
 */
public interface Deferred<Done, Fail> extends Promiss<Done, Fail> {

    Deferred<Done, Fail> resolve(final Done result);

    Deferred<Done, Fail> reject(final Fail result);

    Promiss<Done, Fail> promiss();
}
