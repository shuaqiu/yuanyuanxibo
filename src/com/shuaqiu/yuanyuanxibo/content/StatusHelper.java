package com.shuaqiu.yuanyuanxibo.content;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.shuaqiu.common.Tuple;

/**
 * @author shuaqiu 2013-5-31
 */
public class StatusHelper extends DatabaseHelper {
    public static final String TABLE = "t_status";

    private static final String TAG = "StatusHelper";

    private static final String ORDER_BY = Column.id.name() + " desc";

    //
    public enum Column {
        /** 微博ID */
        id(ColumnType.INTEGER),
        /** 微博創建時間 */
        created_at(ColumnType.TEXT),
        /** 微博內容 */
        text(ColumnType.TEXT),
        /** 微博來源 */
        source(ColumnType.TEXT),
        /** 是否已收藏: true: 是, false: 否 */
        favorited(ColumnType.BOOLEAN),
        /** 圖片列表 */
        pic_urls(ColumnType.TEXT),
        /** 縮略圖片地址 */
        thumbnail_pic(ColumnType.TEXT),
        /** 中等尺寸圖片地址 */
        bmiddle_pic(ColumnType.TEXT),
        /** 原始圖片地址 */
        original_pic(ColumnType.TEXT),
        /** 地理信息 */
        geo(ColumnType.TEXT),
        /** 微博作者ID */
        uid(ColumnType.INTEGER),
        /** 微博作者 */
        user_screen_name(ColumnType.TEXT),
        /** 微博作者頭像, 50x50 */
        user_profile_image_url(ColumnType.TEXT),
        /** 微博作者是否是微博認證用戶, 即加V 用戶, true: 是, false: 否 */
        user_verified(ColumnType.BOOLEAN),
        /** 轉發數 */
        reposts_count(ColumnType.INTEGER),
        /** 評論數 */
        comments_count(ColumnType.INTEGER),
        /** 表態數 */
        attitudes_count(ColumnType.INTEGER),
        /**
         * 微博的可見性及指定可見分組信息<br/>
         * visible.type:<br/>
         * 微博的可見性: 0: 普通微博, 1: 私密微博, 3: 指定分組微博, 4: 密友微博<br/>
         * visible.list_id:<br/>
         * 分組組號
         */
        visible(ColumnType.TEXT),

        /** 微博ID */
        retweeted_id(ColumnType.INTEGER),
        /** 微博創建時間 */
        retweeted_created_at(ColumnType.TEXT),
        /** 微博內容 */
        retweeted_text(ColumnType.TEXT),
        /** 微博來源 */
        retweeted_source(ColumnType.TEXT),
        /** 是否已收藏: true: 是, false: 否 */
        retweeted_favorited(ColumnType.BOOLEAN),
        /** 圖片列表 */
        retweeted_pic_urls(ColumnType.TEXT),
        /** 縮略圖片地址 */
        retweeted_thumbnail_pic(ColumnType.TEXT),
        /** 中等尺寸圖片地址 */
        retweeted_bmiddle_pic(ColumnType.TEXT),
        /** 原始圖片地址 */
        retweeted_original_pic(ColumnType.TEXT),
        /** 地理信息 */
        retweeted_geo(ColumnType.TEXT),
        /** 微博作者ID */
        retweeted_uid(ColumnType.INTEGER),
        /** 微博作者 */
        retweeted_user_screen_name(ColumnType.TEXT),
        /** 微博作者頭像, 50x50 */
        retweeted_user_profile_image_url(ColumnType.TEXT),
        /** 微博作者是否是微博認證用戶, 即加V 用戶, true: 是, false: 否 */
        retweeted_user_verified(ColumnType.BOOLEAN),
        /** 轉發數 */
        retweeted_reposts_count(ColumnType.INTEGER),
        /** 評論數 */
        retweeted_comments_count(ColumnType.INTEGER),
        /** 表態數 */
        retweeted_attitudes_count(ColumnType.INTEGER),
        /**
         * 微博的可見性及指定可見分組信息<br/>
         * visible.type:<br/>
         * 微博的可見性: 0: 普通微博, 1: 私密微博, 3: 指定分組微博, 4: 密友微博<br/>
         * visible.list_id:<br/>
         * 分組組號
         */
        retweeted_visible(ColumnType.TEXT);

        // -----------------------------------------
        private ColumnType type;

        private Column(ColumnType type) {
            this.type = type;
        }
    }

    public static String getDdl() {
        StringBuilder ddl = new StringBuilder("create table if not exists ");
        ddl.append(TABLE);
        ddl.append("(");

        Column[] columns = Column.values();
        int i = 0;
        for (Column column : columns) {
            if (i > 0) {
                ddl.append(", ");
            }
            ddl.append(column.name() + " " + column.type.name());
            if (column == Column.id) {
                ddl.append(" primary key");
            }
            i++;
        }

        ddl.append(")");

        return ddl.toString();
    }

    public static String[] names() {
        Column[] columns = Column.values();

        String[] names = new String[columns.length];
        int i = 0;
        for (Column column : columns) {
            names[i++] = column.name();
        }

        return names;
    }

    /**
     * @param context
     */
    public StatusHelper(Context context) {
        super(context);
    }

    public Cursor query(String selection, String[] selectionArgs, String limit) {
        return query(TABLE, names(), selection, selectionArgs, ORDER_BY, limit);
    }

    public Map<Column, Object> toMap(Cursor cursor, int position) {
        Column[] columns = Column.values();
        Map<Column, Object> m = new HashMap<Column, Object>(columns.length);

        boolean isSuccessMoved = cursor.moveToPosition(position);
        if (!isSuccessMoved) {
            return m;
        }

        for (Column c : columns) {
            try {
                if (c.type == ColumnType.TEXT) {
                    m.put(c, cursor.getString(c.ordinal()));
                } else if (c.type == ColumnType.INTEGER) {
                    m.put(c, cursor.getLong(c.ordinal()));
                } else if (c.type == ColumnType.BOOLEAN) {
                    m.put(c, cursor.getInt(c.ordinal()) == 1);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        return m;
    }

    public int saveOrUpdate(JSONArray statuses) {
        Log.d(TAG, "write status data to database");

        for (int i = 0; i < statuses.length(); i++) {
            JSONObject status = statuses.optJSONObject(i);
            saveOrUpdate(status);
        }

        return statuses.length();
    }

    public long saveOrUpdate(JSONObject status) {
        ContentValues values = extract(status);
        return saveOrUpdate(TABLE, values);
    }

    private ContentValues extract(JSONObject status) {
        Column[] columns = Column.values();
        JSONObject retweetedStatus = status.optJSONObject("retweeted_status");

        int size = columns.length;
        if (retweetedStatus == null) {
            size /= 2;
        }
        ContentValues values = new ContentValues(size);

        for (Column c : columns) {
            Tuple<JSONObject, String> tuple = resolveTargetAndField(status,
                    retweetedStatus, c);
            if (tuple == null) {
                continue;
            }

            JSONObject target = tuple.getValue1();
            String field = tuple.getValue2();

            if (field.equals("pic_urls") || field.equals("geo")
                    || field.equals("visible")) {
                // 這幾個字段的內容都是對象, 因此不做進一步深入解析了, 直接將對象作為JSON 文本保存
                Object val = target.opt(field);
                if (val != null) {
                    values.put(c.name(), val.toString());
                }
            } else if (c.type == ColumnType.TEXT) {
                values.put(c.name(), target.optString(field));
            } else if (c.type == ColumnType.INTEGER) {
                values.put(c.name(), target.optLong(field));
            } else if (c.type == ColumnType.BOOLEAN) {
                values.put(c.name(), target.optBoolean(field));
            }
        }

        return values;
    }

    private Tuple<JSONObject, String> resolveTargetAndField(JSONObject status,
            JSONObject retweetedStatus, Column c) {
        JSONObject target = status;
        String field = c.name();

        if (field.startsWith("retweeted_")) {
            // 處理轉發微博字段
            if (retweetedStatus == null) {
                // 如果是原創微博, 則沒有轉發的微博, 則不需要處理這些字段
                return null;
            }
            // 目標設為轉發的微博, 字段名則是去掉前綴
            target = retweetedStatus;
            field = field.replace("retweeted_", "");
        }

        if (field.startsWith("user_")) {
            // 處理微博作者相關的字段
            JSONObject user = target.optJSONObject("user");
            if (user == null) {
                // 如果不存在微博作者, 也就是查詢的時候只返回uid, 則沒有內容, 無法獲取相關信息
                return null;
            }
            // 目標為作者, 字段名則是去掉前綴
            target = user;
            field = field.replace("user_", "");
        }
        if (field.equals("uid")) {
            // 處理uid 字段, 同樣分兩種情況
            JSONObject user = target.optJSONObject("user");
            if (user != null) {
                // 如果存在微博作者信息, 則目標重設為作者, 字段名為id
                target = user;
                field = "id";
            }
            // 如果不存在, 則直接獲取微博的uid 字段內容
        }
        return new Tuple<JSONObject, String>(target, field);
    }
}
