package com.shuaqiu.yuanyuanxibo.content;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.shuaqiu.common.util.HttpUtil;
import com.shuaqiu.yuanyuanxibo.API.Friend;
import com.shuaqiu.yuanyuanxibo.StateKeeper;
import com.shuaqiu.yuanyuanxibo.auth.Oauth2AccessToken;

/**
 * <code><pre>
{
    "users": [
          {
                "id": 2861527785,
                "idstr": "2861527785",
                "screen_name": "莫过于-感情",
                "name": "莫过于-感情",
                "province": "23",
                "city": "8",
                "location": "黑龙江 佳木斯",
                "description": "催泪歌者。唱吧最纯洁的男孩儿。",
                "url": "",
                "profile_image_url": "http://tp2.sinaimg.cn/2861527785/50/5661792237/1",
                "profile_url": "u/2861527785",
                "domain": "",
                "weihao": "",
                "gender": "m",
                "followers_count": 3172,
                "friends_count": 33,
                "statuses_count": 157,
                "favourites_count": 0,
                "created_at": "Thu Aug 30 18:37:33 +0800 2012",
                "following": false,
                "allow_all_act_msg": false,
                "geo_enabled": true,
                "verified": false,
                "verified_type": -1,
                "remark": "",
                "status_id": 3588861404096653,
                "allow_all_comment": true,
                "avatar_large": "http://tp2.sinaimg.cn/2861527785/180/5661792237/1",
                "verified_reason": "",
                "follow_me": false,
                "online_status": 0,
                "bi_followers_count": 29,
                "lang": "zh-cn",
                "star": 0,
                "mbtype": 0,
                "mbrank": 0,
                "block_word": 0
          }
    ],
    "next_cursor": 1,
    "previous_cursor": 0,
    "total_number": 35
}
</pre></code>
 * 
 * @author shuaqiu 2013-5-31
 */
public class FriendshipHelper extends AbsObjectHelper {

    static final String TAG = "FriendshipHelper";

    public static final String TABLE = "t_friendship";

    /** 排序字段 */
    public static final String ORDER_BY = Column.id.name() + " desc";

    public static final Column[] COLUMNS = Column.values();

    /**
     * 微博的列定義
     * 
     * @author shuaqiu 2013-5-31
     */
    public enum Column {
        /** 用戶ID */
        id(ColumnType.INTEGER),
        /** 用戶ID 的字符竄表示 */
        idstr(ColumnType.TEXT),
        /** 用戶暱稱 */
        screen_name(ColumnType.TEXT),
        /** 友好顯示名稱 */
        name(ColumnType.TEXT),
        /** 所在省ID */
        province(ColumnType.INTEGER),
        /** 所在地市ID */
        city(ColumnType.INTEGER),
        /** 所在地 */
        location(ColumnType.TEXT),
        /** 個人描述 */
        description(ColumnType.TEXT),
        /** 博客地址 */
        url(ColumnType.TEXT),
        /** 頭像地址, 50x50 像素 */
        profile_image_url(ColumnType.TEXT),
        /** 微博統一URL 地址 */
        profile_url(ColumnType.TEXT),
        /** 個性化域名 */
        domain(ColumnType.TEXT),
        /** 微號 */
        weihao(ColumnType.TEXT),
        /** 性別: m: 男, f: 女, n: 未知 */
        gender(ColumnType.TEXT),
        /** 粉絲數 */
        followers_count(ColumnType.INTEGER),
        /** 關注數 */
        friends_count(ColumnType.INTEGER),
        /** 微博數 */
        statuses_count(ColumnType.INTEGER),
        /** 收藏數 */
        favourites_count(ColumnType.INTEGER),
        /** 創建(註冊) 時間 */
        created_at(ColumnType.TEXT),
        /** 當前登錄用戶是否關注該用戶: true: 是, false: 否 */
        following(ColumnType.BOOLEAN),
        /** 是否允許所有人給我發私信: true: 是, false: 否 */
        allow_all_act_msg(ColumnType.BOOLEAN),
        /** 是否允許標識用戶的地理位置: true: 是, false: 否 */
        geo_enabled(ColumnType.BOOLEAN),
        /** 是否是微博認證用戶, 即加V 用戶: true: 是, false: 否 */
        verified(ColumnType.BOOLEAN),
        /** 認證類型? */
        verified_type(ColumnType.INTEGER),
        /** 備註信息 */
        remark(ColumnType.TEXT),
        /** 最近一條微博的id */
        status_id(ColumnType.INTEGER),
        /** 是否允許所有人對我的微博進行評論: true: 是, false: 否 */
        allow_all_comment(ColumnType.BOOLEAN),
        /** 用戶大頭像地址 */
        avatar_large(ColumnType.TEXT),
        /** 認證原因 */
        verified_reason(ColumnType.TEXT),
        /** 是否關注當前登錄用戶: true: 是, false: 否 */
        follow_me(ColumnType.BOOLEAN),
        /** 在線狀態: 0: 不在線, 1: 在線 */
        online_status(ColumnType.INTEGER),
        /** 互粉數 */
        bi_followers_count(ColumnType.INTEGER),
        /** 當前的語言版本: zh-cn: 簡體中文, zh-tw: 繁體中文, en: 英語 */
        lang(ColumnType.TEXT),
        /***/
        star(ColumnType.TEXT),
        /***/
        mbtype(ColumnType.TEXT),
        /***/
        mbrank(ColumnType.TEXT),
        /***/
        block_word(ColumnType.TEXT);

        // -----------------------------------------
        ColumnType columnType;

        private Column(ColumnType type) {
            columnType = type;
        }
    }

    protected static String getDdl() {
        return getDdl(TABLE, COLUMNS, Column.id);
    }

    public static String[] names() {
        return names(COLUMNS);
    }

    public static List<Bundle> toBundles(Cursor cursor) {
        List<Bundle> bundles = new ArrayList<Bundle>(cursor.getCount());
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            bundles.add(toBundle(cursor));
        }

        return bundles;
    }

    public static Bundle toBundle(Cursor cursor, int position) {
        boolean isSuccessMoved = cursor.moveToPosition(position);
        if (!isSuccessMoved) {
            return Bundle.EMPTY;
        }

        return toBundle(cursor);
    }

    public static Bundle toBundle(Cursor cursor) {
        int len = COLUMNS.length;
        Bundle emotion = new Bundle(len);
        for (Column c : COLUMNS) {
            if (cursor.isNull(c.ordinal())) {
                // 如果字段是null 值, 則不作處理
                continue;
            }

            try {
                // 獲取值, 并放到bundle 中
                c.columnType.addToBundle(emotion, cursor, c);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        return emotion;
    }

    /**
     * @param context
     */
    public FriendshipHelper(Context context) {
        super(context);
        table = TABLE;
        names = names();
        orderBy = ORDER_BY;
    }

    @Override
    protected ContentValues extract(JSONObject json) {
        ContentValues values = new ContentValues(COLUMNS.length);

        for (Column c : COLUMNS) {
            c.columnType.extractTo(values, json, c.name());
        }

        return values;
    }

    public void downloadAllFriends() {
        Log.d(TAG, "prepare to download");

        Bundle params = new Bundle();
        Oauth2AccessToken accessToken = StateKeeper.accessToken;
        params.putString("access_token", accessToken.getAccessToken());
        params.putString("uid", accessToken.getUid());
        // params.putInt("count", 200);

        int cursor = 0;
        // do {
        JSONObject json = download(params, cursor);
        if (json == null) {
            return;
        }

        // 取得下一頁的cursor, 進行循環遍歷, 獲取所有的關注.
        // 如果已經到最後一頁, 則next_cursor 的值為0
        cursor = json.optInt("next_cursor");
        // } while (cursor > 0);
    }

    public JSONObject download(Bundle params, int cursor) {
        params.putInt("cursor", cursor);

        Log.d(TAG, "start to download");
        String respText = HttpUtil.httpGet(Friend.FRIENDS, params);
        Log.d(TAG, "downloaded: " + respText);

        if (respText == null) {
            return null;
        }

        JSONObject json = null;
        try {
            json = new JSONObject(respText);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }

        JSONArray arr = json.optJSONArray("users");
        if (arr == null || arr.length() == 0) {
            Log.d(TAG, "not data");
            return null;
        }

        Log.d(TAG, "write emotions data to database");
        saveOrUpdate(arr);

        return json;
    }

    public void tryDownloadImage() {

    }
}
