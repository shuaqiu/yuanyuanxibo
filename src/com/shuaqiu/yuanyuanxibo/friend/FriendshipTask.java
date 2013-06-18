/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.friend;

import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.shuaqiu.common.util.HttpUtil;
import com.shuaqiu.yuanyuanxibo.API.Friend;
import com.shuaqiu.yuanyuanxibo.StateKeeper;
import com.shuaqiu.yuanyuanxibo.auth.Oauth2AccessToken;
import com.shuaqiu.yuanyuanxibo.content.FriendshipHelper;
import com.shuaqiu.yuanyuanxibo.content.FriendshipHelper.Column;

/**
 * @author shuaqiu 2013-6-17
 */
public class FriendshipTask implements Runnable {

    private static final String TAG = "FriendshipTask";

    private static final int MAX_COUNT_PER_REQ = 200;

    private FriendshipHelper mHelper;

    public FriendshipTask(Context context) {
        mHelper = new FriendshipHelper(context);
    }

    @Override
    public void run() {
        mHelper.openForWrite();

        try {
            for (Type type : Type.values()) {
                int netCount = getCount(type);
                int dbCount = getCountFromDb(type);
                if (netCount > dbCount) {
                    Log.d(TAG, String.format("type: %s, online: %i, local: %i",
                            type, netCount, dbCount));
                    downloadNewDatas(type, netCount - dbCount);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            mHelper.close();
        }

    }

    private void downloadNewDatas(Type type, int count) {
        int cursor = 0;
        while (count > 0) {
            int c = MAX_COUNT_PER_REQ;
            if (count < MAX_COUNT_PER_REQ) {
                c = count;
            }

            Bundle params = buildReqParam();
            params.putInt("count", c);
            params.putInt("cursor", cursor);

            String respText = HttpUtil.httpGet(type.dataApi, params);

            JSONObject json = getJson(respText);

            // save
            mHelper.saveOrUpdate(json.optJSONArray("users"));

            count -= MAX_COUNT_PER_REQ;
            cursor += MAX_COUNT_PER_REQ;
        }

    }

    private JSONObject getJson(String respText) {
        if (respText == null) {
            return new JSONObject();
        }

        try {
            return new JSONObject(respText);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return new JSONObject();
        }
    }

    private int getCount(Type type) {
        Bundle params = buildReqParam();
        params.putInt("count", 1);
        String respText = HttpUtil.httpGet(type.idsApi, params);
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

    private Bundle buildReqParam() {
        Bundle params = new Bundle();
        Oauth2AccessToken accessToken = StateKeeper.accessToken;
        params.putString("access_token", accessToken.getAccessToken());
        params.putString("uid", accessToken.getUid());
        return params;
    }

    private int getCountFromDb(Type type) {
        String sql = "select count(*) from " + FriendshipHelper.TABLE;
        sql += " where " + type.column.name() + " = 1";

        Cursor cursor = null;
        try {
            cursor = mHelper.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return -1;
    }

    private enum Type {
        FRIEND(Friend.FRIENDS, Friend.FRIENDS_IDS, Column.following),

        FOLLOWER(Friend.FOLLOWERS, Friend.FOLLOWERS_IDS, Column.follow_me);

        private String dataApi;

        private String idsApi;

        private Column column;

        private Type(String dataApi, String idsApi, Column column) {
            this.dataApi = dataApi;
            this.idsApi = idsApi;
            this.column = column;
        }

    }
}
