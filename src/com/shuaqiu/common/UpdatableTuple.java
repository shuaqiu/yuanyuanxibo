/**
 * 
 */
package com.shuaqiu.common;


/**
 * @author shuaqiu 2013-6-1
 */
public final class UpdatableTuple<M, N> {
    private M m;
    private N n;

    /**
     * @param m
     * @param n
     */
    public UpdatableTuple(M m, N n) {
        update(m, n);
    }

    public UpdatableTuple<M, N> update(M m, N n) {
        this.m = m;
        this.n = n;
        return this;
    }

    public M getValue1() {
        return m;
    }

    public N getValue2() {
        return n;
    }

    @Override
    public String toString() {
        return "(" + m + "," + n + ")";
    }
}
