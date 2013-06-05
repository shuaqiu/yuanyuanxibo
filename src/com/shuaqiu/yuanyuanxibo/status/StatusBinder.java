/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
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
        String thumbnailPic = optThumbnailPic(status);
        if (thumbnailPic == null) {
            if (progress != null) {
                progress.setVisibility(View.GONE);
            }
        } else {
            ViewUtil.setImage(v, thumbnailPic, progress);
            if (mType == Type.DETAIL) {
                v.setTag(0);

                String[] pics = optPics(status, PIC_BMIDDLE);
                // String[] pics = optPics(status, PIC_LARGE);
                OnClickListener l = new ViewImageClickListener(mContext, pics);
                v.setOnClickListener(l);

                setPics(view, status, l);
            }
        }
    }

    /**
     * 如果有多張圖片, 則進行顯示
     * 
     * @param view
     * @param status
     * @param listener
     * 
     */
    protected void setPics(View view, Data status, OnClickListener listener) {
        String[] pics = optPics(status, PIC_BMIDDLE);
        if (pics == null || pics.length < 2) {
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
            v.setTag(i);
            v.setOnClickListener(listener);
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
     * @return
     */
    protected abstract String optThumbnailPic(Data status);

    /**
     * 獲取微博的圖片內容
     * 
     * @param status
     * @param type
     *            TODO
     * @return
     */
    protected abstract String[] optPics(Data status, String type);

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
     * @param type
     * @return
     */
    protected String[] optPics(JSONArray arr, String type) {
        if (arr.length() == 0) {
            return null;
        }
        // 第一張圖片不需要再處理了, 直接通過外層的數據可以獲取到
        String[] pics = new String[arr.length()];
        for (int i = 0; i < arr.length(); i++) {
            JSONObject json = arr.optJSONObject(i);
            String pic = json.optString(Column.thumbnail_pic.name(), null);
            if (isOptMiddlePic()) {
                pic = pic.replace(PIC_THUMBNAIL, type);
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
        LIST, DETAIL
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