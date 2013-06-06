/**
 * 
 */
package com.shuaqiu.common.promiss;

/**
 * @author shuaqiu 2013-6-6
 * 
 */
public interface AlwaysCallback<Done, Fail> {

    void apply(State state, Done doneResult, Fail failResult);
}
