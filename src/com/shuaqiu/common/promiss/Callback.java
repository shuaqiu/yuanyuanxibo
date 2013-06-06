/**
 * 
 */
package com.shuaqiu.common.promiss;

/**
 * @author shuaqiu 2013-6-6
 * 
 */
public interface Callback<Result> {

    void apply(final Result result);
}
