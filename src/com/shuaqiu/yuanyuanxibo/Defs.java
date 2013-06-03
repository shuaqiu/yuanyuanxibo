/**
 * 
 */
package com.shuaqiu.yuanyuanxibo;

/**
 * @author shuaqiu 2013-5-3
 */
public interface Defs {

    String SCHEME = "yyxibo";

    String USER_SCHEME = "yyxibo://user_info";
    String USER_NAME = "name";

    String TREND_SCHEME = "yyxibo://trend_info";
    String TREND_NAME = "name";

    public interface Action {
        String NEW_STATUS = "com.shuaqiu.yuanyuanxibo.status.NEW";

        String STATUS_COMMENT = "com.shuaqiu.yuanyuanxibo.comment.STATUS";

        String USER_COMMENT = "com.shuaqiu.yuanyuanxibo.comment.USER";
    }
}
