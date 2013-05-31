/**
 * 
 */
package com.shuaqiu.common.task;

/**
 * @author shuaqiu 2013-5-31
 * 
 */
public interface Function<Param, Result> {

    Result apply(Param params);
}
