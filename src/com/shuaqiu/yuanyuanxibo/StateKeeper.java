/**
 * 
 */
package com.shuaqiu.yuanyuanxibo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.shuaqiu.yuanyuanxibo.auth.Oauth2AccessToken;

/**
 * @author shuaqiu May 4, 2013
 */
public final class StateKeeper {

    private static final String TAG = "StateKeeper";

    public static Oauth2AccessToken accessToken;

    public static boolean isWifi;

    public static boolean isWifi(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        if (info == null || info.isConnected()) {
            return false;
        }

        int type = info.getType();
        Log.d(TAG, "type : " + type);
        return type == ConnectivityManager.TYPE_WIFI;
    }

    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo();
    }
}
