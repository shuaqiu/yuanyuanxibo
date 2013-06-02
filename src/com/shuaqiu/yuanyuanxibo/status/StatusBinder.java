/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import android.content.Context;
import android.view.View;

import com.shuaqiu.common.TimeHelper;
import com.shuaqiu.common.util.ViewUtil;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.ViewBinder;
import com.shuaqiu.yuanyuanxibo.content.StatusHelper.Column;

/**
 * @author shuaqiu Jun 2, 2013
 */
public abstract class StatusBinder<Data> implements ViewBinder<Data> {

    public enum Type {
        LIST, DETAIL
    }

    protected TimeHelper mTimeHelper;
    protected Type mType;

    public StatusBinder(Context context, Type type) {
        mTimeHelper = new TimeHelper(context);
        mType = type;
    }

    /**
     * @param view
     * @param data
     */
    @Override
    public void bindView(View view, final Data data) {
        setProfileImage(view, data);
        setStatusViews(view, data);
        setRetweetedViews(view, data);
        setThumbnailPic(view, data);
    }

    /**
     * @param view
     * @param status
     */
    protected void setProfileImage(View view, final Data status) {
        View v = view.findViewById(R.id.profile_image);
        String profileImage = optProfileImage(status);
        if (profileImage != null) {
            ViewUtil.setViewImage(v, profileImage);
        }
    }

    /**
     * @param status
     */
    protected void setStatusViews(View view, Data status) {
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
    protected void setRetweetedViews(View view, final Data status) {
        View retweeted = view.findViewById(R.id.retweeted);
        Data retweetedStatus = optRetweetedStatus(status);
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
    protected void setRetweetedStatusViews(View view, Data retweetedStatus) {
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
    protected void setThumbnailPic(View view, Data status) {
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
            if (mType == Type.DETAIL) {
                // v.setOnClickListener(l);
            }
        }
    }

    /**
     * 獲取轉發的微博數據
     * 
     * @param status
     */
    protected abstract Data optRetweetedStatus(final Data status);

    /**
     * 獲取微博中作者的頭像URL
     * 
     * @param status
     * @return
     */
    protected abstract String optProfileImage(Data status);

    /**
     * 獲取微博作者的名稱, 建議獲取screen_name
     * 
     * @param status
     * @return
     */
    protected abstract String optUsername(Data status);

    /**
     * 獲取微博的內容
     * 
     * @param status
     * @return
     */
    protected abstract String optText(Data status);

    /**
     * 獲取微博的發送時間, 並進行格式化的處理, 其中微博中的時間格式爲: <br/>
     * "Sat Apr 27 00:59:08 +0800 2013"<br/>
     * 即: EEE MMM dd HH:mm:ss zzz yyyy
     * 
     * @param status
     * @return
     * @see TimeHelper#beautyTime(String)
     */
    protected abstract String optCreateTime(Data status);

    /**
     * 獲取微博的發送客戶端名稱
     * 
     * @param status
     * @return
     */
    protected abstract String optSource(Data status);

    /**
     * 獲取微博的圖片內容
     * 
     * @param status
     * @return
     */
    protected abstract String optThumbnailPic(Data status);

    /**
     * 獲取微博的一些數量數據, 比如轉發數, 評論數
     * 
     * @param status
     * @param c
     * @return
     */
    protected abstract String optCount(Data status, Column c);

}