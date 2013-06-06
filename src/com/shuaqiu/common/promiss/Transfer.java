/**
 * 
 */
package com.shuaqiu.common.promiss;

/**
 * @author shuaqiu 2013-6-6
 * 
 */
public interface Transfer<In, Out> {

    Out transfer(final In in);
}
