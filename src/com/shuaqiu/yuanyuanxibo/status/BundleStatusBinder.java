/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import org.json.JSONArray;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.shuaqiu.yuanyuanxibo.content.StatusHelper;
import com.shuaqiu.yuanyuanxibo.content.StatusHelper.Column;

/**
 * @author shuaqiu 2013-4-30
 */
public class BundleStatusBinder extends StatusBinder<Bundle> {

    private static final String TAG = "BundleStatusBinder";

    public BundleStatusBinder(Context context, Type type) {
        super(context, type);
    }

    @Override
    protected long optStatusId(Bundle status) {
        return status.getLong(Column.id.name());
    }

    @Override
    protected Bundle optRetweetedStatus(Bundle status) {
        return status.getBundle(StatusHelper.RETWEETED_STATUS);
    }

    @Override
    protected String optProfileImage(Bundle status) {
        return status.getString(Column.user_profile_image_url.name());
    }

    @Override
    protected String optUsername(Bundle status) {
        String screenName = status.getString(Column.user_screen_name.name());
        if (screenName == null) {
            return "";
        }
        return screenName;
    }

    @Override
    protected String optText(Bundle status) {
        String text = status.getString(Column.text.name());
        if (text == null) {
            return "";
        }
        return text;
    }

    // "Sat Apr 27 00:59:08 +0800 2013"
    @Override
    protected String optCreateTime(Bundle status) {
        String createdAt = status.getString(Column.created_at.name());
        return mTimeHelper.beautyTime(createdAt);
    }

    @Override
    protected String optSource(Bundle status) {
        String source = status.getString(Column.source.name());
        // return Html.fromHtml(source);
        return source.replaceAll("<a.*?>(.*?)</a>", "$1");
    }

    @Override
    protected String optThumbnailPic(Bundle status, ImageQuality imgQuality) {
        if (imgQuality == ImageQuality.NONE) {
            return null;
        }

        String name = imgQuality.column.name();

        String thumbnailPic = status.getString(name);
        if (thumbnailPic == null || thumbnailPic.equals("")) {
            Bundle retweetedStatus = optRetweetedStatus(status);
            if (retweetedStatus == null) {
                return null;
            }

            thumbnailPic = retweetedStatus.getString(name);
            if (thumbnailPic == null || thumbnailPic.equals("")) {
                return null;
            }
        }
        return thumbnailPic;
    }

    @Override
    protected String[] optPics(Bundle status, ImageQuality quality) {
        String name = Column.pic_urls.name();

        String picUrls = status.getString(name);
        if (picUrls == null || picUrls.equals("[]")) {
            Bundle retweetedStatus = optRetweetedStatus(status);
            if (retweetedStatus == null) {
                return null;
            }
            picUrls = retweetedStatus.getString(name);
            if (picUrls == null || picUrls.equals("[]")) {
                return null;
            }
        }

        return optPics(picUrls, quality);
    }

    private String[] optPics(String jsonStr, ImageQuality quality) {
        try {
            JSONArray arr = new JSONArray(jsonStr);
            return optPics(arr, quality);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected String optCount(Bundle status, Column c) {
        return status.getLong(c.name(), 0) + "";
    }

}
