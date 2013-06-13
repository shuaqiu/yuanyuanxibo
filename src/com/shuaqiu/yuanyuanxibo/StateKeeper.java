/**
 * 
 */
package com.shuaqiu.yuanyuanxibo;

import java.io.File;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.shuaqiu.yuanyuanxibo.auth.AccessTokenKeeper;
import com.shuaqiu.yuanyuanxibo.auth.Oauth2AccessToken;

/**
 * @author shuaqiu May 4, 2013
 */
public final class StateKeeper {

    private static final String TAG = "StateKeeper";

    public static Oauth2AccessToken accessToken;

    public static boolean isWifi;
    public static File pictureDir;

    /**
     * 初始化系統的狀態
     * <ul>
     * <li>讀取accessToken 設置</li>
     * <li>讀取WIFI 狀態</li>
     * <li>獲取圖片緩存路徑</li>
     * </ul>
     * 
     * @param context
     */
    public static void init(Context context) {
        accessToken = AccessTokenKeeper.read(context);
        isWifi = isWifiConnected(context);
        pictureDir = getPictureDir(context);
    }

    public static boolean isWifiConnected(Context context) {
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

    public static File getPictureDir(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }
}
