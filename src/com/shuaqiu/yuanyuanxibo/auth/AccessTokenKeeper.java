package com.shuaqiu.yuanyuanxibo.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 该类用于保存Oauth2AccessToken到sharepreference，并提供读取功能
 */
public class AccessTokenKeeper {
    private static final String PREF_NAME = "token";

    /**
     * 保存accesstoken到SharedPreferences
     * 
     * @param context
     *            Activity 上下文环境
     * @param token
     *            Oauth2AccessToken
     */
    public static void save(Context context, Oauth2AccessToken token) {
        SharedPreferences pref = getTokenPrefences(context);
        Editor editor = pref.edit();
        editor.putString("accessToken", token.getAccessToken());
        editor.putLong("expiresTime", token.getExpiresTime());
        editor.putString("uid", token.getUid());
        editor.commit();
    }

    /**
     * 清空sharepreference
     * 
     * @param context
     */
    public static void clear(Context context) {
        SharedPreferences pref = getTokenPrefences(context);
        Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 从SharedPreferences读取accessstoken
     * 
     * @param context
     * @return Oauth2AccessToken
     */
    public static Oauth2AccessToken read(Context context) {
        SharedPreferences pref = getTokenPrefences(context);
        String accessToken = pref.getString("accessToken", "");
        long expiresTime = pref.getLong("expiresTime", 0);
        String uid = pref.getString("uid", "");
        return new Oauth2AccessToken(accessToken, expiresTime, uid);
    }

    /**
     * @param context
     * @return
     */
    private static SharedPreferences getTokenPrefences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}
