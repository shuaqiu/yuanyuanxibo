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

import com.shuaqiu.common.ImageType;
import com.shuaqiu.common.TimeHelper;
import com.shuaqiu.common.util.ViewUtil;
import com.shuaqiu.common.widget.ViewBinder;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.StateKeeper;
import com.shuaqiu.yuanyuanxibo.content.StatusHelper.Column;
import com.shuaqiu.yuanyuanxibo.misc.StartActivityClickListener;

/**
 * @author shuaqiu Jun 2, 2013
 */
public abstract class StatusBinder<Data> implements ViewBinder<Data> {

    protected Context mContext;
    protected TimeHelper mTimeHelper;
    protected Type mType;
    protected LayoutParams mLayoutParams;

    public StatusBinder(Context context, Type type) {
        mContext = context;
        mTimeHelper = new TimeHelper(context);
        mType = type;
    }

    @Override
    public void bindView(View view, final Data data) {
        ViewHolder holder = initViewHolder(view);

        setProfileImage(holder, data);
        setStatusViews(holder, data);

        if (mType == Type.REPOST) {
            // 某條微博的轉發微博列表界面不需要再顯示原始微博和圖片了
            holder.mRetweeted.setVisibility(View.GONE);
            holder.mThumbnailPic.setVisibility(View.GONE);
            holder.mProgress.setVisibility(View.GONE);
        } else {
            setRetweetedViews(holder, data);
            setThumbnailPic(holder, data);
        }
    }

    protected ViewHolder initViewHolder(View view) {
        Object tag = view.getTag();
        if (tag != null && tag instanceof ViewHolder) {
            return (ViewHolder) tag;
        }

        ViewHolder holder = new ViewHolder();
        view.setTag(holder);

        holder.mProfileImage = view.findViewById(R.id.profile_image);

        holder.mUsername = view.findViewById(R.id.user_name);
        holder.mCreateAt = view.findViewById(R.id.created_at);
        holder.mText = view.findViewById(R.id.text);
        holder.mSource = view.findViewById(R.id.source);
        holder.mAttitudesCount = view.findViewById(R.id.attitudes_count);
        holder.mRepostsCount = view.findViewById(R.id.reposts_count);
        holder.mCommentsCount = view.findViewById(R.id.comments_count);

        holder.mRetweeted = view.findViewById(R.id.retweeted);

        holder.mRetweetedUsername = view.findViewById(R.id.retweeted_user_name);
        holder.mRetweetedCreateAt = view
                .findViewById(R.id.retweeted_created_at);
        holder.mRetweetedText = view.findViewById(R.id.retweeted_text);
        holder.mRetweetedSource = view.findViewById(R.id.retweeted_source);
        holder.mRetweetedAttitudesCount = view
                .findViewById(R.id.retweeted_attitudes_count);
        holder.mRetweetedRepostsCount = view
                .findViewById(R.id.retweeted_reposts_count);
        holder.mRetweetedCommentsCount = view
                .findViewById(R.id.retweeted_comments_count);

        holder.mStatusContent = (LinearLayout) view
                .findViewById(R.id.status_content);
        holder.mThumbnailPic = view.findViewById(R.id.thumbnail_pic);
        holder.mProgress = view.findViewById(R.id.progress);

        return holder;
    }

    protected void setProfileImage(ViewHolder holder, final Data status) {
        String profileImage = optProfileImage(status);
        if (profileImage != null) {
            ViewUtil.setImage(holder.mProfileImage, ImageType.PROFILE,
                    profileImage);
        }
    }

    protected void setStatusViews(ViewHolder holder, Data status) {
        ViewUtil.setText(holder.mUsername, optUsername(status));
        ViewUtil.addLinks(holder.mUsername, ViewUtil.USER);

        ViewUtil.setText(holder.mText, optText(status));
        ViewUtil.addLinks(holder.mText, ViewUtil.ALL);

        ViewUtil.setText(holder.mCreateAt, optCreateTime(status));

        ViewUtil.setText(holder.mSource, optSource(status));

        Bundle args = buildClickArgs(status);
        OnClickListener listener = new StartActivityClickListener(args);

        ViewUtil.setText(holder.mAttitudesCount,
                optCount(status, Column.attitudes_count), listener);

        ViewUtil.setText(holder.mRepostsCount,
                optCount(status, Column.reposts_count), listener);

        ViewUtil.setText(holder.mCommentsCount,
                optCount(status, Column.comments_count), listener);
    }

    protected void setRetweetedViews(ViewHolder holder, final Data status) {
        Data retweetedStatus = optRetweetedStatus(status);
        if (retweetedStatus == null) {
            holder.mRetweeted.setVisibility(View.GONE);
        } else {
            holder.mRetweeted.setVisibility(View.VISIBLE);
            setRetweetedStatusViews(holder, retweetedStatus);
        }
    }

    protected void setRetweetedStatusViews(ViewHolder holder,
            Data retweetedStatus) {
        String username = optUsername(retweetedStatus);

        ViewUtil.setText(holder.mRetweetedUsername, username);
        ViewUtil.addLinks(holder.mRetweetedUsername, ViewUtil.USER);

        ViewUtil.setText(holder.mRetweetedText, optText(retweetedStatus));
        ViewUtil.addLinks(holder.mRetweetedText, ViewUtil.ALL);

        ViewUtil.setText(holder.mRetweetedCreateAt,
                optCreateTime(retweetedStatus));

        ViewUtil.setText(holder.mRetweetedSource, optSource(retweetedStatus));

        Bundle args = buildClickArgs(retweetedStatus);
        OnClickListener listener = new StartActivityClickListener(args);

        ViewUtil.setText(holder.mRetweetedAttitudesCount,
                optCount(retweetedStatus, Column.attitudes_count), listener);

        ViewUtil.setText(holder.mRetweetedRepostsCount,
                optCount(retweetedStatus, Column.reposts_count), listener);

        ViewUtil.setText(holder.mRetweetedCommentsCount,
                optCount(retweetedStatus, Column.comments_count), listener);
    }

    /**
     * 顯示微博圖片
     */
    protected void setThumbnailPic(ViewHolder holder, Data status) {
        holder.mThumbnailPic.setVisibility(View.GONE);
        ImageQuality imgQuality = getImageQuality(mType);

        if (imgQuality == ImageQuality.NONE) {
            // 無圖, 直接返回即可
            hideProgress(holder.mProgress);
            return;
        }

        String thumbnailPic = optThumbnailPic(status, imgQuality);
        if (thumbnailPic == null) {
            // 微博沒有圖片, 直接返回
            hideProgress(holder.mProgress);
            return;
        }

        // 顯示微博圖片
        ViewUtil.setImage(holder.mThumbnailPic, thumbnailPic, holder.mProgress);

        if (mType == Type.DETAIL) {
            // 對於微博詳細界面, 要顯示微博的所有圖片
            ImageQuality picViewerQuality = getImageQuality(Type.PIC_VIEWER);
            if (picViewerQuality == ImageQuality.NONE) {
                // 圖片查看設置爲無圖, 那麼直接就不用打開這個activity 了
                // 顯示其他的圖片
                setPics(holder, status, imgQuality, null);
                return;
            }

            String[] pics = optPics(status, picViewerQuality);
            OnClickListener l = new ViewImageClickListener(mContext, pics);

            holder.mThumbnailPic.setTag(0);
            holder.mThumbnailPic.setOnClickListener(l);

            // 顯示其他的圖片
            setPics(holder, status, imgQuality, l);
        }
    }

    /**
     * @param status
     * @return
     */
    protected Bundle buildClickArgs(Data status) {
        Bundle args = new Bundle();
        args.putLong("id", optStatusId(status));
        args.putString("username", optUsername(status));
        Data retweetedStatus = optRetweetedStatus(status);
        if (retweetedStatus != null) {
            args.putBoolean("isRetweeted", true);
            args.putString("text", optText(status));
        }
        return args;
    }

    /**
     * 隱藏進度條
     */
    protected void hideProgress(View progress) {
        if (progress != null) {
            progress.setVisibility(View.GONE);
        }
    }

    /**
     * 根據設置值, 獲取要使用的圖片質量
     * 
     * @return 圖片質量
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
     * @return 默認的圖片質量
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
     * @param holder
     * @param status
     * @param quality
     * @param listener
     */
    protected void setPics(ViewHolder holder, Data status,
            ImageQuality quality, OnClickListener listener) {
        String[] pics = optPics(status, quality);
        if (pics == null || pics.length < 2) {
            // 第一張圖片已經在前面顯示了
            return;
        }

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
            holder.mStatusContent.addView(v);

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
     *            微博信息
     * @return 要顯示的時間
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
     * 獲取微博的第一張圖片URL
     * 
     * @param status
     *            微博信息
     * @param quality
     *            要獲取的圖片質量
     * @return 第一張圖片的URL
     */
    protected abstract String optThumbnailPic(Data status, ImageQuality quality);

    /**
     * 獲取微博的所有圖片的URL
     * 
     * @param status
     *            微博信息
     * @param quality
     *            要獲取的圖片質量
     * @return 所有圖片的URL 數組
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

    public enum Type {
        /** 微博列表界面 */
        LIST("image_quality_list"),

        /** 微博詳細界面 */
        DETAIL("image_quality_detail"),

        /** 微博原圖查看界面 */
        PIC_VIEWER("image_quality_original"),

        /** 某條微博的轉發微博列表界面 */
        REPOST(null);

        /**
         * 對應的圖片質量屬性key, 用於獲取系統設置的圖片質量.<br/>
         * 如果是WIFI 網絡下, 則該key 還需要加上"_wifi"
         * 
         * @see res/xml/preferences.xml
         */
        private String imgQualityKey = null;

        private Type(String imgQualityPrefKey) {
            this.imgQualityKey = imgQualityPrefKey;
        }
    }

    /**
     * 要顯示的圖片質量
     * 
     * @author shuaqiu 2013-6-5
     */
    public enum ImageQuality {
        /** 無圖 */
        NONE("", null),
        /** 縮略圖 */
        THUMBNAIL("thumbnail", Column.thumbnail_pic),
        /** 大圖 */
        LARGE("bmiddle", Column.bmiddle_pic),
        /** 原圖 */
        ORIGINAL("large", Column.original_pic);

        /**
         * 圖片URL 中的的關鍵字, 比如:
         * <ul>
         * <li>
         * http://ww2.sinaimg.cn/thumbnail/5259a295jw1e5a6fael8mj20m80gpaeb.jpg
         * 中的thumbnail</li>
         * <li>
         * http://ww2.sinaimg.cn/bmiddle/5259a295jw1e5a6fael8mj20m80gpaeb.jpg
         * 中的bmiddle</li>
         * <li>http://ww2.sinaimg.cn/large/5259a295jw1e5a6fael8mj20m80gpaeb.jpg
         * 中的large</li>
         * </ul>
         */
        String keywork = null;

        /**
         * 對於的微博字段名稱
         */
        Column column = null;

        private ImageQuality(String keywork, Column column) {
            this.keywork = keywork;
            this.column = column;
        }
    }

    /**
     * 當點擊圖片時, 打開原圖顯示
     * 
     * @author shuaqiu 2013-6-5
     */
    private static class ViewImageClickListener implements OnClickListener {
        private Context mContext;
        private String[] mPics;

        /**
         * @param mContext
         * @param pics
         *            圖片的URL 路徑列表
         */
        public ViewImageClickListener(Context context, String[] pics) {
            mContext = context;
            mPics = pics;
        }

        @Override
        public void onClick(View v) {
            // 在顯示圖片時, 將圖片的順序放到ImageView 中保存起來了
            Integer position = (Integer) v.getTag();

            Intent intent = new Intent(mContext, PictureViewerActivity.class);
            intent.putExtra("pics", mPics);
            intent.putExtra("position", position);
            mContext.startActivity(intent);
        }
    }

    private static class ViewHolder {
        private View mProfileImage;

        private View mUsername;
        private View mCreateAt;
        private View mText;
        private View mSource;
        private View mAttitudesCount;
        private View mRepostsCount;
        private View mCommentsCount;

        private View mRetweeted;
        private View mRetweetedUsername;
        private View mRetweetedCreateAt;
        private View mRetweetedText;
        private View mRetweetedSource;
        private View mRetweetedAttitudesCount;
        private View mRetweetedRepostsCount;
        private View mRetweetedCommentsCount;

        private LinearLayout mStatusContent;
        private View mThumbnailPic;
        private View mProgress;
    }
}