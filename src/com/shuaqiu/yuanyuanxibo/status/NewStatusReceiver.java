/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author shuaqiu May 27, 2013
 */
public class NewStatusReceiver extends BroadcastReceiver {

    private static final String TAG = "statusreceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, intent.getStringExtra("data"));
    }

}
