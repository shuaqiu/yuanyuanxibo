/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import org.json.JSONObject;

import android.content.Context;
import android.view.View;

import com.shuaqiu.common.TimeHelper;
import com.shuaqiu.common.ViewUtil;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.StateKeeper;
import com.shuaqiu.yuanyuanxibo.ViewBinder;

/**
 * @author shuaqiu 2013-4-30
 */
public class StatusBinder implements ViewBinder {
    public enum Type {
        LIST, DETAIL
    }

    private TimeHelper mTimeHelper;
    private Type mType;

    public StatusBinder(Context context, Type type) {
        mTimeHelper = new TimeHelper(context);
        mType = type;
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
        ViewUtil.addLinks(usernameView, ViewUtil.USER);

        View textView = view.findViewById(R.id.text);
        ViewUtil.setViewText(textView, status.optString("text", ""));
        ViewUtil.addLinks(textView, ViewUtil.ALL);

        ViewUtil.setViewText(view.findViewById(R.id.created_at),
                optCreateTime(status));

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
        ViewUtil.addLinks(usernameView, ViewUtil.USER);

        View textView = view.findViewById(R.id.retweeted_text);
        ViewUtil.setViewText(textView, retweetedStatus.optString("text", ""));
        ViewUtil.addLinks(textView, ViewUtil.ALL);

        ViewUtil.setViewText(view.findViewById(R.id.retweeted_created_at),
                optCreateTime(retweetedStatus));
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
        View progress = view.findViewById(R.id.progress);

        String thumbnailPic = optThumbnailPic(status);
        if (thumbnailPic == null) {
            v.setVisibility(View.GONE);
            if (progress != null) {
                progress.setVisibility(View.GONE);
            }
        } else {
            ViewUtil.setViewImage(v, thumbnailPic, progress);
            v.setVisibility(View.VISIBLE);
            if (mType == Type.DETAIL) {
                // v.setOnClickListener(l);
            }
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

    protected String optSource(JSONObject status) {
        String source = status.optString("source");
        // return Html.fromHtml(source);
        return source.replaceAll("<a.*?>(.*?)</a>", "$1");
    }

    protected String optThumbnailPic(JSONObject status) {
        String name = "thumbnail_pic";
        if (mType == Type.DETAIL && StateKeeper.isWifi) {
            name = "bmiddle_pic";
        }

        String thumbnailPic = status.optString(name, null);
        if (thumbnailPic != null) {
            return thumbnailPic;
        }
        JSONObject retweetedStatus = status.optJSONObject("retweeted_status");
        if (retweetedStatus == null) {
            return null;
        }
        return retweetedStatus.optString(name, null);
    }
}
