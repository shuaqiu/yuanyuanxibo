/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.auth;


/**
 * @author shuaqiu May 3, 2013
 */
public interface AuthListener {
    /**
     * 认证结束后将调用此方法
     * 
     * @param values
     *            Key-value string pairs extracted from the response.
     *            从responsetext中获取的键值对
     *            ，键值包括"access_token"，"expires_in"，“refresh_token”
     */
    public void onComplete(String responseText);

}
