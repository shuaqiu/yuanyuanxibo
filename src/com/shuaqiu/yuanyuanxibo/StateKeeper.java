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
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiInfo = manager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        Log.d(TAG, "" + wifiInfo);
        Log.d(TAG, "" + wifiInfo.isConnected());

        // Log.d(TAG, "" +
        // manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE));

        return wifiInfo.isConnected();
    }
}
