/**
 * 
 */
package com.shuaqiu.common;

import android.util.Log;

/**
 * @author shuaqiu 2013-6-1
 */
public final class Tuple<M, N> {
    private static final String TAG = "Tuple";

    private final M m;
    private final N n;

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

    @Override
    public String toString() {
        return "(" + m + "," + n + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tuple)) {
            return false;
        }
        try {
            @SuppressWarnings("unchecked")
            Tuple<M, N> t = (Tuple<M, N>) o;
            return m.equals(t.m) && n.equals(t.n);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = 37 * hash + m.hashCode();
        hash = 37 * hash + n.hashCode();
        return hash;
    }
}
