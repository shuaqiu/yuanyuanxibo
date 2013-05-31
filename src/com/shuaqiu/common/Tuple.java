/**
 * 
 */
package com.shuaqiu.common;

/**
 * @author shuaqiu 2013-6-1
 */
public class Tuple<M, N> {
    private M m;
    private N n;

    /**
     * @param m
     * @param n
     */
    public Tuple(M m, N n) {
        this.m = m;
        this.n = n;
    }

    public M getValue1() {
        return m;
    }

    public N getValue2() {
        return n;
    }
}
