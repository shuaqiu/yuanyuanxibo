/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.shuaqiu.yuanyuanxibo.content.StatusHelper;
import com.shuaqiu.yuanyuanxibo.content.StatusHelper.Column;

/**
 * @author shuaqiu 2013-4-30
 */
public class JsonStatusBinder extends StatusBinder<JSONObject> {

    public JsonStatusBinder(Context context, Type type) {
        super(context, type);
    }

    @Override
    protected long optStatusId(JSONObject status) {
        return status.optLong(Column.id.name());
    }

    @Override
    protected JSONObject optRetweetedStatus(JSONObject status) {
        return status.optJSONObject(StatusHelper.RETWEETED_STATUS);
    }

    @Override
    protected String optProfileImage(JSONObject status) {
        JSONObject user = status.optJSONObject("user");
        if (user == null) {
            return null;
        }
        return user.optString("profile_image_url", null);
    }

    @Override
    protected String optUsername(JSONObject status) {
        JSONObject user = status.optJSONObject("user");
        if (user == null) {
            return "";
        }
        return user.optString("screen_name", "");
    }

    @Override
    protected String optText(JSONObject status) {
        return status.optString(Column.text.name(), "");
    }

    // "Sat Apr 27 00:59:08 +0800 2013"
    @Override
    protected String optCreateTime(JSONObject status) {
        String createdAt = status.optString(Column.created_at.name());
        return mTimeHelper.beautyTime(createdAt);
    }

    @Override
    protected String optSource(JSONObject status) {
        String source = status.optString(Column.source.name());
        // return Html.fromHtml(source);
        return source.replaceAll("<a.*?>(.*?)</a>", "$1");
    }

    @Override
    protected String optThumbnailPic(JSONObject status, ImageQuality imgQuality) {
        if (imgQuality == ImageQuality.NONE) {
            return null;
        }

        String name = imgQuality.column.name();

        String thumbnailPic = status.optString(name, null);
        if (thumbnailPic != null) {
            return thumbnailPic;
        }
        JSONObject retweetedStatus = optRetweetedStatus(status);
        if (retweetedStatus == null) {
            return null;
        }
        return retweetedStatus.optString(name, null);
    }

    @Override
    protected String[] optPics(JSONObject status, ImageQuality quality) {
        String name = Column.pic_urls.name();

        JSONArray picUrls = status.optJSONArray(name);
        if (picUrls == null || picUrls.equals("[]")) {
            JSONObject retweetedStatus = optRetweetedStatus(status);
            if (retweetedStatus == null) {
                return null;
            }
            picUrls = retweetedStatus.optJSONArray(name);
            if (picUrls == null || picUrls.equals("[]")) {
                return null;
            }
        }

        return optPics(picUrls, quality);
    }

    @Override
    protected String optCount(JSONObject status, Column c) {
        return status.optString(c.name(), "0");
    }
}
