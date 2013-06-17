/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.friend;

import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

import com.shuaqiu.common.util.HttpUtil;
import com.shuaqiu.yuanyuanxibo.API.Friend;
import com.shuaqiu.yuanyuanxibo.StateKeeper;
import com.shuaqiu.yuanyuanxibo.auth.Oauth2AccessToken;

/**
 * @author shuaqiu 2013-6-17
 * 
 */
public class FriendshipTask implements Runnable {

    private static final String TAG = "FriendshipTask";

    @Override
    public void run() {
    }

    private int getFriendsCount() {
        String urlStr = Friend.FRIENDS_IDS;

        return getCount(urlStr);
    }

    private int getCount(String urlStr) {
        Bundle params = new Bundle();
        Oauth2AccessToken accessToken = StateKeeper.accessToken;
        params.putString("access_token", accessToken.getAccessToken());
        params.putString("uid", accessToken.getUid());
        params.putInt("count", 1);
        String respText = HttpUtil.httpGet(urlStr, params);
        if (respText == null) {
            return -1;
        }

        JSONObject json = null;
        try {
            json = new JSONObject(respText);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return -1;
        }

        return json.optInt("total_number", -1);
    }

    private int getCountFromDb() {
        return -1;
    }
}
