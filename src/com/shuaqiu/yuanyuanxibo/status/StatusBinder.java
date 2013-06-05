/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.shuaqiu.common.TimeHelper;
import com.shuaqiu.common.util.ViewUtil;
import com.shuaqiu.common.widget.ViewBinder;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.StartActivityClickListener;
import com.shuaqiu.yuanyuanxibo.StateKeeper;
import com.shuaqiu.yuanyuanxibo.content.StatusHelper.Column;

/**
 * @author shuaqiu Jun 2, 2013
 */
public abstract class StatusBinder<Data> implements ViewBinder<Data> {

    /**
     * 微博圖片: 縮略圖的關鍵字, 比如:<br/>
     * http://ww2.sinaimg.cn/thumbnail/5259a295jw1e5a6fael8mj20m80gpaeb.jpg
     */
    static final String PIC_THUMBNAIL = "thumbnail";

    /**
     * 微博圖片: 中等大小圖片的關鍵字, 比如:<br/>
     * http://ww2.sinaimg.cn/bmiddle/5259a295jw1e5a6fael8mj20m80gpaeb.jpg
     */
    static final String PIC_BMIDDLE = "bmiddle";

    /**
     * 微博圖片: 原圖的關鍵字, 比如:<br/>
     * http://ww2.sinaimg.cn/large/5259a295jw1e5a6fael8mj20m80gpaeb.jpg
     */
    static final String PIC_LARGE = "large";

    protected Context mContext;
    protected TimeHelper mTimeHelper;
    protected Type mType;
    protected LayoutParams mLayoutParams;

    public StatusBinder(Context context, Type type) {
        mContext = context;
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
            ViewUtil.setImage(v, profileImage);
        }
    }

    /**
     * @param status
     */
    protected void setStatusViews(View view, Data status) {
        View usernameView = view.findViewById(R.id.user_name);
        ViewUtil.setText(usernameView, optUsername(status));
        ViewUtil.addLinks(usernameView, ViewUtil.USER);

        View textView = view.findViewById(R.id.text);
        ViewUtil.setText(textView, optText(status));
        ViewUtil.addLinks(textView, ViewUtil.ALL);

        ViewUtil.setText(view.findViewById(R.id.created_at),
                optCreateTime(status));

        ViewUtil.setText(view.findViewById(R.id.source), optSource(status));

        Bundle args = new Bundle(1);
        args.putLong("id", optStatusId(status));
        OnClickListener listener = new StartActivityClickListener(args);

        ViewUtil.setText(view.findViewById(R.id.attitudes_count),
                optCount(status, Column.attitudes_count), listener);

        ViewUtil.setText(view.findViewById(R.id.reposts_count),
                optCount(status, Column.reposts_count), listener);

        ViewUtil.setText(view.findViewById(R.id.comments_count),
                optCount(status, Column.comments_count), listener);
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
        ViewUtil.setText(usernameView, optUsername(retweetedStatus));
        ViewUtil.addLinks(usernameView, ViewUtil.USER);

        View textView = view.findViewById(R.id.retweeted_text);
        ViewUtil.setText(textView, optText(retweetedStatus));
        ViewUtil.addLinks(textView, ViewUtil.ALL);

        ViewUtil.setText(view.findViewById(R.id.retweeted_created_at),
                optCreateTime(retweetedStatus));

        ViewUtil.setText(view.findViewById(R.id.retweeted_source),
                optSource(retweetedStatus));

        Bundle args = new Bundle(1);
        args.putLong("id", optStatusId(retweetedStatus));
        OnClickListener listener = new StartActivityClickListener(args);

        ViewUtil.setText(view.findViewById(R.id.retweeted_attitudes_count),
                optCount(retweetedStatus, Column.attitudes_count), listener);

        ViewUtil.setText(view.findViewById(R.id.retweeted_reposts_count),
                optCount(retweetedStatus, Column.reposts_count), listener);

        ViewUtil.setText(view.findViewById(R.id.retweeted_comments_count),
                optCount(retweetedStatus, Column.comments_count), listener);
    }

    /**
     * @param status
     */
    protected void setThumbnailPic(View view, Data status) {
        View v = view.findViewById(R.id.thumbnail_pic);
        View progress = view.findViewById(R.id.progress);

        v.setVisibility(View.GONE);
        ImageQuality imgQuality = getImageQuality(mType);

        if (imgQuality == ImageQuality.NONE) {
            // 無圖, 直接返回即可
            hideProgress(progress);
            return;
        }

        String thumbnailPic = optThumbnailPic(status, imgQuality);
        if (thumbnailPic == null) {
            hideProgress(progress);
        } else {
            ViewUtil.setImage(v, thumbnailPic, progress);

            if (mType == Type.DETAIL) {
                ImageQuality picViewerQuality = getImageQuality(Type.PIC_VIEWER);
                if (picViewerQuality == ImageQuality.NONE) {
                    // 圖片查看設置爲無圖, 那麼直接就不用打開這個activity 了
                    // 顯示其他的圖片
                    setPics(view, status, imgQuality, null);
                    return;
                }

                String[] pics = optPics(status, picViewerQuality);
                OnClickListener l = new ViewImageClickListener(mContext, pics);

                v.setTag(0);
                v.setOnClickListener(l);

                // 顯示其他的圖片
                setPics(view, status, imgQuality, l);
            }
        }
    }

    /**
     * 隱藏進度條
     * 
     * @param progress
     */
    protected void hideProgress(View progress) {
        if (progress != null) {
            progress.setVisibility(View.GONE);
        }
    }

    /**
     * 根據設置值, 獲取要使用的圖片質量
     * 
     * @return
     */
    protected ImageQuality getImageQuality(Type type) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(mContext);

        ImageQuality defQuality = getDefaultImageQuality(type);
        String key = type.imgQualityKey;
        if (StateKeeper.isWifi) {
            // 如果是WIFI, 則使用WIFI 對於的設置
            key += "_wifi";
        }
        String qualityStr = pref.getString(key, defQuality.name());
        return ImageQuality.valueOf(qualityStr);
    }

    /**
     * 獲取默認的圖片質量
     * 
     * @return
     */
    protected ImageQuality getDefaultImageQuality(Type type) {
        if (type == Type.LIST) {
            return ImageQuality.THUMBNAIL;
        }
        if (type == Type.DETAIL) {
            return ImageQuality.LARGE;
        }
        if (type == Type.PIC_VIEWER) {
            return ImageQuality.ORIGINAL;
        }
        return ImageQuality.THUMBNAIL;
    }

    /**
     * 如果有多張圖片, 則進行顯示
     * 
     * @param view
     * @param status
     * @param imgQuality
     * @param listener
     */
    protected void setPics(View view, Data status, ImageQuality imgQuality,
            OnClickListener listener) {
        String[] pics = optPics(status, imgQuality);
        if (pics == null || pics.length < 2) {
            // 第一張圖片已經在前面顯示了
            return;
        }
        LinearLayout content = (LinearLayout) view
                .findViewById(R.id.status_content);
        LayoutParams params = getLayoutParams();
        for (int i = 1; i < pics.length; i++) {
            String pic = pics[i];
            // <ImageView
            // android:id="@+id/thumbnail_pic"
            // android:layout_width="wrap_content"
            // android:layout_height="wrap_content"
            // android:layout_gravity="center"
            // android:layout_margin="@dimen/five_dp"
            // android:contentDescription="@string/thumbnail_pic"
            // android:src="@drawable/ic_launcher"
            // android:visibility="gone" />
            ImageView v = new ImageView(mContext);
            v.setVisibility(View.GONE);
            v.setLayoutParams(params);
            if (listener != null) {
                v.setTag(i);
                v.setOnClickListener(listener);
            }
            content.addView(v);

            ViewUtil.setImage(v, pic);
        }
    }

    /**
     * 獲取用於顯示其他圖片的佈局參數
     * 
     * @return the mLayoutParams
     */
    private LayoutParams getLayoutParams() {
        if (mLayoutParams == null) {
            mLayoutParams = initLayoutParams();
        }
        return mLayoutParams;
    }

    /**
     * 構造用於顯示其他圖片的佈局參數
     * 
     * @return
     */
    protected LayoutParams initLayoutParams() {
        LayoutParams params = new LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        Resources resources = mContext.getResources();
        int margin = resources.getDimensionPixelSize(R.dimen.five_dp);
        params.setMargins(margin, margin, margin, margin);
        return params;
    }

    /**
     * 獲取微博的ID
     * 
     * @param status
     * @return
     */
    protected abstract long optStatusId(Data status);

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
     * @param imgQuality
     * @return
     */
    protected abstract String optThumbnailPic(Data status,
            ImageQuality imgQuality);

    /**
     * 獲取微博的圖片內容
     * 
     * @param status
     * @param quality
     * @return
     */
    protected abstract String[] optPics(Data status, ImageQuality quality);

    /**
     * 獲取圖片列表
     * 
     * <pre>
     * "pic_urls": [
     *      {
     *          "thumbnail_pic": "http://ww2.sinaimg.cn/thumbnail/5259a295jw1e5a6fael8mj20m80gpaeb.jpg"
     *      },
     *      {
     *          "thumbnail_pic": "http://ww2.sinaimg.cn/thumbnail/5259a295jw1e5a6figbbzj20m80gogo7.jpg"
     *      }
     * ]
     * </pre>
     * 
     * @param arr
     * @param quality
     * @return
     */
    protected String[] optPics(JSONArray arr, ImageQuality quality) {
        if (arr.length() == 0) {
            return null;
        }
        // 第一張圖片不需要再處理了, 直接通過外層的數據可以獲取到
        String[] pics = new String[arr.length()];
        for (int i = 0; i < arr.length(); i++) {
            JSONObject json = arr.optJSONObject(i);
            String pic = json.optString(Column.thumbnail_pic.name(), null);
            if (quality != ImageQuality.THUMBNAIL) {
                // pic_urls 只有縮略圖的url, 如果要獲取其他的url, 只能進行關鍵詞替換操作
                pic = pic.replace(ImageQuality.THUMBNAIL.keywork,
                        quality.keywork);
            }
            pics[i] = pic;
        }
        return pics;
    }

    /**
     * 獲取微博的一些數量數據, 比如轉發數, 評論數
     * 
     * @param status
     * @param c
     * @return
     */
    protected abstract String optCount(Data status, Column c);

    /**
     * 是否獲取大圖
     * 
     * @return
     */
    protected boolean isOptMiddlePic() {
        // return false;
        return mType == Type.DETAIL;// && StateKeeper.isWifi;
    }

    public enum Type {
        LIST("image_quality_list"), DETAIL("image_quality_detail"), PIC_VIEWER(
                "image_quality_original");

        private String imgQualityKey = null;

        private Type(String imgQualityPrefKey) {
            this.imgQualityKey = imgQualityPrefKey;
        }
    }

    public enum ImageQuality {
        /** 無圖 */
        NONE("", null),
        /** 縮略圖 */
        THUMBNAIL("thumbnail", Column.thumbnail_pic),
        /** 大圖 */
        LARGE("bmiddle", Column.bmiddle_pic),
        /** 原圖 */
        ORIGINAL("large", Column.original_pic);

        String keywork = null;
        Column column = null;

        private ImageQuality(String keywork, Column column) {
            this.keywork = keywork;
            this.column = column;
        }
    }

    private class ViewImageClickListener implements OnClickListener {
        private Context mContext;
        private String[] mPics;

        /**
         * @param mContext
         * @param status
         */
        public ViewImageClickListener(Context context, String[] pics) {
            mContext = context;
            mPics = pics;
        }

        @Override
        public void onClick(View v) {
            Integer position = (Integer) v.getTag();

            Intent intent = new Intent(mContext, PictureViewerActivity.class);
            intent.putExtra("pics", mPics);
            intent.putExtra("position", position);
            mContext.startActivity(intent);
        }
    }

}