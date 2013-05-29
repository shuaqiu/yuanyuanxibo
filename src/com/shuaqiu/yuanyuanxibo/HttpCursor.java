/**
 * 
 */
package com.shuaqiu.yuanyuanxibo;

/**
 * @author shuaqiu 2013-5-29
 * 
 */
public class HttpCursor {

    private Type type;

    private CursorPair[] pairs;

    public HttpCursor(Type type, CursorPair[] pairs) {
        this.type = type;
        this.pairs = pairs;
    }

    public Type getType() {
        return type;
    }

    public CursorPair[] getPairs() {
        if (pairs == null) {
            return new CursorPair[0];
        }
        return pairs;
    }

    /**
     * 將p 放在列表的第一個位置
     * 
     * @param p
     */
    public void prepend(CursorPair p) {
        if (pairs == null || pairs.length == 0) {
            pairs = new CursorPair[] { p };
            return;
        }
        boolean merged = pairs[0].mergeWith(p);
        if (merged) {
            // 合併成功, 則不需要再做處理
            return;
        }
        // 將新的值放在第一個位置
        CursorPair[] newPairs = new CursorPair[pairs.length + 1];
        System.arraycopy(pairs, 0, newPairs, 1, pairs.length);
        newPairs[0] = p;
        pairs = newPairs;
    }

    public static class CursorPair {
        private long timestamp = 0;
        private long min = 0;
        private long max = 0;

        public CursorPair(long timestamp, long min, long max) {
            init(timestamp, min, max);
        }

        private void init(long timestamp, long min, long max) {
            this.timestamp = timestamp;
            this.min = min;
            this.max = max;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public long getMin() {
            return min;
        }

        public long getMax() {
            return max;
        }

        public boolean mergeWith(CursorPair p) {
            long timestamp = Math.max(this.timestamp, p.timestamp);

            if (min < p.min && p.min <= max && max <= p.max) {
                init(timestamp, min, p.max);
                return true;
            }
            if (p.min < min && min <= p.max && p.max <= max) {
                init(timestamp, p.min, max);
                return true;
            }
            return false;
        }
    }

    public enum Type {
        STATUS, COMMENT;
    }
}
