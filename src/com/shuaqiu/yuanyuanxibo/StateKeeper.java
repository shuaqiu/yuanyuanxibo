/**
 * 
 */
package com.shuaqiu.yuanyuanxibo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.shuaqiu.yuanyuanxibo.auth.Oauth2AccessToken;

/**
 * @author shuaqiu May 4, 2013
 */
public final class StateKeeper {

    public static Oauth2AccessToken accessToken;

    public static boolean isWifi;

    public static boolean isWifi(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null || info.isConnected()) {
            return false;
        }

        int type = info.getType();
        return type == ConnectivityManager.TYPE_WIFI;
    }
}
