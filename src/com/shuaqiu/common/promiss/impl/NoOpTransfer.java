/**
 * 
 */
package com.shuaqiu.common.promiss.impl;

import com.shuaqiu.common.promiss.Transfer;

/**
 * @author shuaqiu 2013-6-6
 * 
 * @param <D>
 */
public final class NoOpTransfer<D> implements Transfer<D, D> {
    @Override
    public D transfer(D d) {
        return d;
    };
}