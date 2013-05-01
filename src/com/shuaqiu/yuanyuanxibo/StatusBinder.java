/**
 * 
 */
package com.shuaqiu.yuanyuanxibo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shuaqiu.common.AsyncImageViewTask;

/**
 * @author shuaqiu 2013-4-30
 */
public class StatusBinder implements ViewBinder {

    private static final long ONE_MINUTE = 60 * 1000L;

    private static final long ONE_HOUR = 60 * ONE_MINUTE;

    private static final long ONE_DAY = 24 * ONE_HOUR;

    private JSONArray mData;

    private DateFormat dateParser = new SimpleDateFormat(
            "EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault());
    private DateFormat timeFormat;
    private DateFormat dateFormat;

    public StatusBinder(Context context) {

        mData = WeiboStatus.getInstance().getStatus();

        timeFormat = android.text.format.DateFormat.getTimeFormat(context);
        dateFormat = android.text.format.DateFormat.getDateFormat(context);
    }

    @Override
    public void bindView(int position, View view) {
        final JSONObject status = mData.optJSONObject(position);
        if (status == null) {
            return;
        }

        setProfileImage(view, status);
        setStatusViews(view, status);
        setRetweetedViews(view, status);
        setThumbnailPic(view, status);
    }

    /**
     * @param view
     * @param status
     */
    protected void setProfileImage(View view, final JSONObject status) {
        View v = view.findViewById(R.id.profile_image);
        String profileImage = optProfileImage(status);
        if (profileImage != null) {
            setViewImage(v, profileImage);
        }
    }

    /**
     * @param status
     */
    protected void setStatusViews(View view, JSONObject status) {
        setViewText(view.findViewById(R.id.user_name), optUsername(status));
        setViewText(view.findViewById(R.id.created_at), optCreateTime(status));
        setViewText(view.findViewById(R.id.text), status.optString("text", ""));
        setViewText(view.findViewById(R.id.source), optSource(status));
        setViewText(view.findViewById(R.id.attitudes_count),
                status.optString("attitudes_count", "0"));
        setViewText(view.findViewById(R.id.reposts_count),
                status.optString("reposts_count", "0"));
        setViewText(view.findViewById(R.id.comments_count),
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
        setViewText(view.findViewById(R.id.retweeted_user_name),
                optUsername(retweetedStatus));
        setViewText(view.findViewById(R.id.retweeted_created_at),
                optCreateTime(retweetedStatus));
        setViewText(view.findViewById(R.id.retweeted_text),
                retweetedStatus.optString("text", ""));
        setViewText(view.findViewById(R.id.retweeted_source),
                optSource(retweetedStatus));
        setViewText(view.findViewById(R.id.retweeted_attitudes_count),
                retweetedStatus.optString("retweeted_attitudes_count", "0"));
        setViewText(view.findViewById(R.id.reposts_count),
                retweetedStatus.optString("retweeted_reposts_count", "0"));
        setViewText(view.findViewById(R.id.retweeted_comments_count),
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
            setViewImage(v, thumbnailPic, progress);
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
        return user.optString("name", "");
    }

    // "Sat Apr 27 00:59:08 +0800 2013"
    protected String optCreateTime(JSONObject status) {
        String createdAt = status.optString("created_at");

        try {
            return format(dateParser.parse(createdAt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return createdAt;
    }

    protected String format(Date date) {
        long diff = System.currentTimeMillis() - date.getTime();

        if (diff < ONE_MINUTE) {
            return "刚刚";
        }
        if (diff < ONE_HOUR) {
            return diff / ONE_MINUTE + "分钟前";
        }
        if (diff < ONE_DAY) {
            return timeFormat.format(date);
        }
        return dateFormat.format(date);
    }

    protected String optSource(JSONObject status) {
        String source = status.optString("source");
        return source.replaceAll("<a.*?>(.*?)</a>", "$1");
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

    protected void setViewImage(View v, String url) {
        if (v == null) {
            return;
        }
        if (v instanceof ImageView) {
            ImageView imageView = (ImageView) v;
            AsyncImageViewTask imageTask = new AsyncImageViewTask(imageView);
            imageTask.execute(url);
        }
    }

    protected void setViewImage(View v, String url, View progressView) {
        if (v == null) {
            return;
        }
        if (v instanceof ImageView) {
            ImageView imageView = (ImageView) v;
            AsyncImageViewTask imageTask = new AsyncImageViewTask(imageView,
                    progressView);
            imageTask.execute(url);
        }
    }

    protected void setViewText(View v, String text) {
        if (v == null) {
            return;
        }
        if (v instanceof TextView) {
            TextView textView = (TextView) v;
            textView.setText(text);
        }
    }

    @Override
    public JSONArray getDataItems() {
        return mData;
    }
}
