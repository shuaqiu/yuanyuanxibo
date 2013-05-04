package com.shuaqiu.yuanyuanxibo.auth;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

/**
 * @author shuaqiu May 3, 2013
 */
public class Oauth2AccessToken {
    private String mAccessToken = "";
    private long mExpiresTime = 0;
    private String mUid = "";

    /**
     * 根据服务器返回的responsetext生成Oauth2AccessToken 的构造函数，
     * 此方法会将responsetext里的“access_token”，“expires_in”，"refresh_token"解析出来
     * 
     * @param responseText
     *            服务器返回的responsetext
     */
    public Oauth2AccessToken(String responseText) {
        if (responseText != null) {
            try {
                JSONObject json = new JSONObject(responseText);
                mAccessToken = json.optString("access_token");
                setExpiresIn(json.optString("expires_in"));
                mUid = json.optString("uid");
            } catch (JSONException e) {

            }
        }
    }

    public Oauth2AccessToken(String accessToken, long expiresTime, String uid) {
        mAccessToken = accessToken;
        mExpiresTime = expiresTime;
        mUid = uid;
    }

    /**
     * AccessToken是否有效,如果accessToken为空或者expiresTime过期，返回false，否则返回true
     * 
     * @return 如果accessToken为空或者expiresTime过期，返回false，否则返回true
     */
    public boolean isSessionValid() {
        return (!TextUtils.isEmpty(mAccessToken) && (mExpiresTime == 0 || (System
                .currentTimeMillis() < mExpiresTime)));
    }

    /**
     * 获取accessToken
     */
    public String getAccessToken() {
        return mAccessToken;
    }

    /**
     * 获取超时时间，单位: 毫秒，表示从格林威治时间1970年01月01日00时00分00秒起至现在的总 毫秒数
     */
    public long getExpiresTime() {
        return mExpiresTime;
    }

    /**
     * @return the mUid
     */
    public String getUid() {
        return mUid;
    }

    /**
     * 设置过期时间长度值，仅当从服务器获取到数据时使用此方法
     */
    private void setExpiresIn(String expiresIn) {
        if (expiresIn == null || expiresIn.equals("0")) {
            return;
        }
        mExpiresTime = System.currentTimeMillis() + Long.parseLong(expiresIn)
                * 1000;
    }

}