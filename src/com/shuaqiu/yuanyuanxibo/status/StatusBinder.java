/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import org.json.JSONObject;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.shuaqiu.common.TimeHelper;
import com.shuaqiu.common.ViewUtil;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.ViewBinder;

/**
 * @author shuaqiu 2013-4-30
 */
public class StatusBinder implements ViewBinder {
    private TimeHelper mTimeHelper;

    public StatusBinder(Context context) {
        mTimeHelper = new TimeHelper(context);
    }

    /**
     * @param view
     * @param data
     */
    @Override
    public void bindView(View view, final JSONObject data) {
        setProfileImage(view, data);
        setStatusViews(view, data);
        setRetweetedViews(view, data);
        setThumbnailPic(view, data);
    }

    /**
     * @param view
     * @param status
     */
    protected void setProfileImage(View view, final JSONObject status) {
        View v = view.findViewById(R.id.profile_image);
        String profileImage = optProfileImage(status);
        if (profileImage != null) {
            ViewUtil.setViewImage(v, profileImage);
        }
    }

    /**
     * @param status
     */
    protected void setStatusViews(View view, JSONObject status) {
        View usernameView = view.findViewById(R.id.user_name);
        ViewUtil.setViewText(usernameView, optUsername(status));
        ViewUtil.addUserLinks((TextView) usernameView, ViewUtil.USER_PATTERN);
        ViewUtil.setViewText(view.findViewById(R.id.created_at),
                optCreateTime(status));
        ViewUtil.setViewStatusText(view.findViewById(R.id.text),
                status.optString("text", ""));
        ViewUtil.setViewText(view.findViewById(R.id.source), optSource(status));
        ViewUtil.setViewText(view.findViewById(R.id.attitudes_count),
                status.optString("attitudes_count", "0"));
        ViewUtil.setViewText(view.findViewById(R.id.reposts_count),
                status.optString("reposts_count", "0"));
        ViewUtil.setViewText(view.findViewById(R.id.comments_count),
                status.optString("comments_count", "0"));
    }

    /**
     * @param view
     * @param status
     */
    protected void setRetweetedViews(View view, final JSONObject status) {
        View retweeted = view.findViewById(R.id.retweeted);
        JSONObject retweetedStatus = status.optJSONObject("retweeted_status");
        if (retweetedStatus == null) {
            retweeted.setVisibility(View.GONE);
        } else {
            retweeted.setVisibility(View.VISIBLE);
            setRetweetedStatusViews(view, retweetedStatus);
        }
    }

    /**
     * @param retweetedStatus
     */
    protected void setRetweetedStatusViews(View view, JSONObject retweetedStatus) {
        View usernameView = view.findViewById(R.id.retweeted_user_name);
        ViewUtil.setViewText(usernameView, optUsername(retweetedStatus));
        ViewUtil.addUserLinks((TextView) usernameView, ViewUtil.USER_PATTERN);

        ViewUtil.setViewText(view.findViewById(R.id.retweeted_created_at),
                optCreateTime(retweetedStatus));
        ViewUtil.setViewStatusText(view.findViewById(R.id.retweeted_text),
                retweetedStatus.optString("text", ""));
        ViewUtil.setViewText(view.findViewById(R.id.retweeted_source),
                optSource(retweetedStatus));
        ViewUtil.setViewText(view.findViewById(R.id.retweeted_attitudes_count),
                retweetedStatus.optString("attitudes_count", "0"));
        ViewUtil.setViewText(view.findViewById(R.id.retweeted_reposts_count),
                retweetedStatus.optString("reposts_count", "0"));
        ViewUtil.setViewText(view.findViewById(R.id.retweeted_comments_count),
                retweetedStatus.optString("comments_count", "0"));

    }

    /**
     * @param status
     */
    protected void setThumbnailPic(View view, JSONObject status) {
        View v = view.findViewById(R.id.thumbnail_pic);

        String thumbnailPic = optThumbnailPic(status);
        if (thumbnailPic == null) {
            v.setVisibility(View.GONE);
        } else {
            v.setVisibility(View.VISIBLE);
            View progress = view.findViewById(R.id.progress);
            ViewUtil.setViewImage(v, thumbnailPic, progress);
        }

    }

    /**
     * @param status
     * @return
     */
    protected String optProfileImage(JSONObject status) {
        JSONObject user = status.optJSONObject("user");
        if (user == null) {
            return null;
        }
        return user.optString("profile_image_url", null);
    }

    protected String optUsername(JSONObject status) {
        JSONObject user = status.optJSONObject("user");
        if (user == null) {
            return "";
        }
        return user.optString("screen_name", "");
    }

    // "Sat Apr 27 00:59:08 +0800 2013"
    protected String optCreateTime(JSONObject status) {
        String createdAt = status.optString("created_at");
        return mTimeHelper.beautyTime(createdAt);
    }

    protected Spanned optSource(JSONObject status) {
        String source = status.optString("source");
        return Html.fromHtml(source);
        // return source.replaceAll("<a.*?>(.*?)</a>", "$1");
    }

    protected String optThumbnailPic(JSONObject status) {
        String thumbnailPic = status.optString("thumbnail_pic", null);
        if (thumbnailPic != null) {
            return thumbnailPic;
        }
        JSONObject retweetedStatus = status.optJSONObject("retweeted_status");
        if (retweetedStatus == null) {
            return null;
        }
        return retweetedStatus.optString("thumbnail_pic", null);
    }
}