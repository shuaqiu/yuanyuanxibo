/**
 * 
 */
package com.shuaqiu.yuanyuanxibo;

/**
 * 定義一些Action 常量
 * 
 * @author shuaqiu Jun 10, 2013
 */
public final class Actions {
    private Actions() {
    }

    /**
     * 微博的Action 常量
     * 
     * @author shuaqiu Jun 10, 2013
     */
    public static final class Status {
        /** 有新的微博 */
        public static final String NEW_RECEIVED = "com.shuaqiu.yuanyuanxibo.status.NEW";

        /** 轉發的微博列表 */
        public static final String REPOST_LIST = "com.shuaqiu.yuanyuanxibo.status.REPOST_LIST";

        /** 提到登錄用戶的微博列表, 也就是@me 的微博列表 */
        public static final String AT_ME_LIST = "com.shuaqiu.yuanyuanxibo.status.AT_ME";

        /** 對微博進行轉發 */
        public static final String REPOST = "com.shuaqiu.yuanyuanxibo.status.REPOST";

        private Status() {
        }

    }

    /**
     * 評論的Action 常量
     * 
     * @author shuaqiu Jun 10, 2013
     */
    public static final class Comment {

        /** 微博的評論列表 */
        public static final String FOR_STATUS = "com.shuaqiu.yuanyuanxibo.comment.STATUS";
        /** 用戶的評論列表 */
        public static final String FOR_USER = "com.shuaqiu.yuanyuanxibo.comment.USER";

        /** 對微博進行評論 */
        public static final String CREATE = "com.shuaqiu.yuanyuanxibo.comment.CREATE";
        /** 對已有的評論進行回復 */
        public static final String REPLY = "com.shuaqiu.yuanyuanxibo.comment.REPLY";

        private Comment() {
        }

    }

}