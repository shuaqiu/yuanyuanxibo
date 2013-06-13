package com.shuaqiu.yuanyuanxibo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author shuaqiu 2013-5-29
 * 
 */
public class WifiReceiver extends BroadcastReceiver {

    private static final String TAG = "wifireceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isWifi = StateKeeper.isWifi;
        StateKeeper.isWifi = StateKeeper.isWifiConnected(context);
        Log.i(TAG, "isWifi: " + isWifi + "-->" + StateKeeper.isWifi);
    }
}
