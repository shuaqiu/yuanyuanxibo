/**
 * 
 */
package com.shuaqiu.common.function;

/**
 * @author shuaqiu 2013-5-31
 * 
 */
public interface Function<Param, Result> {

    Result apply(Param params);
}
