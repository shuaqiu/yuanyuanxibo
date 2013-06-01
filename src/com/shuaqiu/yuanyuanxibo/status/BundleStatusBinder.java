/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.shuaqiu.common.TimeHelper;
import com.shuaqiu.common.util.ViewUtil;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.StateKeeper;
import com.shuaqiu.yuanyuanxibo.ViewBinder;
import com.shuaqiu.yuanyuanxibo.content.StatusHelper;
import com.shuaqiu.yuanyuanxibo.content.StatusHelper.Column;

/**
 * @author shuaqiu 2013-4-30
 */
public class BundleStatusBinder implements ViewBinder<Bundle> {
    public enum Type {
        LIST, DETAIL
    }

    private TimeHelper mTimeHelper;
    private Type mType;

    public BundleStatusBinder(Context context, Type type) {
        mTimeHelper = new TimeHelper(context);
        mType = type;
    }

    /**
     * @param view
     * @param data
     */
    @Override
    public void bindView(View view, final Bundle data) {
        setProfileImage(view, data);
        setStatusViews(view, data);
        setRetweetedViews(view, data);
        setThumbnailPic(view, data);
    }

    /**
     * @param view
     * @param status
     */
    protected void setProfileImage(View view, final Bundle status) {
        View v = view.findViewById(R.id.profile_image);
        String profileImage = optProfileImage(status);
        if (profileImage != null) {
            ViewUtil.setViewImage(v, profileImage);
        }
    }

    /**
     * @param status
     */
    protected void setStatusViews(View view, Bundle status) {
        View usernameView = view.findViewById(R.id.user_name);
        ViewUtil.setViewText(usernameView, optUsername(status));
        ViewUtil.addLinks(usernameView, ViewUtil.USER);

        View textView = view.findViewById(R.id.text);
        ViewUtil.setViewText(textView, optText(status));
        ViewUtil.addLinks(textView, ViewUtil.ALL);

        ViewUtil.setViewText(view.findViewById(R.id.created_at),
                optCreateTime(status));

        ViewUtil.setViewText(view.findViewById(R.id.source), optSource(status));
        ViewUtil.setViewText(view.findViewById(R.id.attitudes_count),
                optCount(status, Column.attitudes_count));
        ViewUtil.setViewText(view.findViewById(R.id.reposts_count),
                optCount(status, Column.reposts_count));
        ViewUtil.setViewText(view.findViewById(R.id.comments_count),
                optCount(status, Column.comments_count));
    }

    /**
     * @param view
     * @param status
     */
    protected void setRetweetedViews(View view, final Bundle status) {
        View retweeted = view.findViewById(R.id.retweeted);
        Bundle retweetedStatus = status
                .getBundle(StatusHelper.RETWEETED_STATUS);
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
    protected void setRetweetedStatusViews(View view, Bundle retweetedStatus) {
        View usernameView = view.findViewById(R.id.retweeted_user_name);
        ViewUtil.setViewText(usernameView, optUsername(retweetedStatus));
        ViewUtil.addLinks(usernameView, ViewUtil.USER);

        View textView = view.findViewById(R.id.retweeted_text);
        ViewUtil.setViewText(textView, optText(retweetedStatus));
        ViewUtil.addLinks(textView, ViewUtil.ALL);

        ViewUtil.setViewText(view.findViewById(R.id.retweeted_created_at),
                optCreateTime(retweetedStatus));
        ViewUtil.setViewText(view.findViewById(R.id.retweeted_source),
                optSource(retweetedStatus));
        ViewUtil.setViewText(view.findViewById(R.id.retweeted_attitudes_count),
                optCount(retweetedStatus, Column.attitudes_count));
        ViewUtil.setViewText(view.findViewById(R.id.retweeted_reposts_count),
                optCount(retweetedStatus, Column.reposts_count));
        ViewUtil.setViewText(view.findViewById(R.id.retweeted_comments_count),
                optCount(retweetedStatus, Column.comments_count));

    }

    /**
     * @param status
     */
    protected void setThumbnailPic(View view, Bundle status) {
        View v = view.findViewById(R.id.thumbnail_pic);
        View progress = view.findViewById(R.id.progress);

        v.setVisibility(View.GONE);
        String thumbnailPic = optThumbnailPic(status);
        if (thumbnailPic == null) {
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
    protected String optProfileImage(Bundle status) {
        return status.getString(Column.user_profile_image_url.name());
    }

    protected String optUsername(Bundle status) {
        String screenName = status.getString(Column.user_screen_name.name());
        if (screenName == null) {
            return "";
        }
        return screenName;
    }

    protected String optText(Bundle status) {
        String text = status.getString(Column.text.name());
        if (text == null) {
            return "";
        }
        return text;
    }

    // "Sat Apr 27 00:59:08 +0800 2013"
    protected String optCreateTime(Bundle status) {
        String createdAt = status.getString(Column.created_at.name());
        return mTimeHelper.beautyTime(createdAt);
    }

    protected String optSource(Bundle status) {
        String source = status.getString(Column.source.name());
        // return Html.fromHtml(source);
        return source.replaceAll("<a.*?>(.*?)</a>", "$1");
    }

    protected String optThumbnailPic(Bundle status) {
        String name = Column.thumbnail_pic.name();
        if (mType == Type.DETAIL && StateKeeper.isWifi) {
            name = Column.bmiddle_pic.name();
        }

        String thumbnailPic = status.getString(name);
        if (thumbnailPic != null && !thumbnailPic.equals("")) {
            return thumbnailPic;
        }
        Bundle retweetedStatus = status
                .getBundle(StatusHelper.RETWEETED_STATUS);
        if (retweetedStatus == null) {
            return null;
        }
        return retweetedStatus.getString(name);
    }

    protected String optCount(Bundle status, Column c) {
        return status.getLong(c.name(), 0) + "";
    }

}
