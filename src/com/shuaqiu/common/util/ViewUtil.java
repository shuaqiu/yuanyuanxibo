package com.shuaqiu.common.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.util.LruCache;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import com.shuaqiu.common.ImageType;
import com.shuaqiu.common.Tuple;
import com.shuaqiu.common.promiss.DeferredManager;
import com.shuaqiu.common.promiss.Promiss;
import com.shuaqiu.common.promiss.impl.DeferredTask.TaskJob;
import com.shuaqiu.yuanyuanxibo.Defs;
import com.shuaqiu.yuanyuanxibo.content.EmotionHelper;

/**
 * @author shuaqiu 2013-5-3
 */
public class ViewUtil {
    private static final String TAG = "ViewUtil";

    /**
     * 中英文，數字，減號，下橫槓
     */
    private static final String USER_PATTERN_STR = "([\\w\\-_\\u4e00-\\u9fa5]+)";

    private static final GroupOneTransformFilter GROUP_ONE_TRANSFORM_FILTER = new GroupOneTransformFilter();

    /** URL */
    public static final int WEB_URLS = 0x01;

    /** 話題 */
    public static final int TREND = 0x02;

    /** @用戶名 */
    public static final int AT_USER = 0x04;

    /** 用戶名 */
    public static final int USER = 0x08;

    /** 表情 */
    public static final int EMOTION = 0x10;

    /** 全部 */
    public static final int ALL = WEB_URLS | TREND | AT_USER | EMOTION;

    /**
     * 設置圖片, 這裡未指定圖片的類型, 則當作微博圖片({@link ImageType#PIC}) 處理
     * 
     * @param v
     *            要設置圖片的view, 現在支持ImageView 或者ImageSwitcher
     * @param date
     *            微博的日期
     * @param url
     *            圖片的URL 地址
     * @return 設置圖片的Promise 對象, 用於對在設置圖片後可能還有的後續動作進行處理
     * @see ImageType#PIC
     */
    public static Promiss<Bitmap, Throwable> setImage(View v, String date,
            String url) {
        return setImage(v, date, url, null);
    }

    /**
     * 設置圖片, 這裡未指定圖片的類型, 則當作微博圖片({@link ImageType#PIC}) 處理
     * 
     * @param v
     *            要設置圖片的view, 現在支持ImageView 或者ImageSwitcher
     * @param date
     *            微博的日期
     * @param url
     *            圖片的URL 地址
     * @param progress
     *            在加載圖片時顯示的進度
     * @return 設置圖片的Promise 對象, 用於對在設置圖片後可能還有的後續動作進行處理
     * @see ImageType#PIC
     */
    public static Promiss<Bitmap, Throwable> setImage(View v, String date,
            String url, View progress) {
        return setImage(v, ImageType.PIC, date, url, progress);
    }

    /**
     * 設置頭像圖片
     * 
     * @param v
     *            要設置圖片的view, 現在支持ImageView 或者ImageSwitcher
     * @param url
     *            圖片的URL 地址
     * @return 設置圖片的Promise 對象, 用於對在設置圖片後可能還有的後續動作進行處理
     */
    public static Promiss<Bitmap, Throwable> setProfileImage(View v, String url) {
        return setImage(v, ImageType.PROFILE, null, url, null);
    }

    /**
     * 設置表情圖片
     * 
     * @param v
     *            要設置圖片的view, 現在支持ImageView 或者ImageSwitcher
     * @param url
     *            圖片的URL 地址
     * @return 設置圖片的Promise 對象, 用於對在設置圖片後可能還有的後續動作進行處理
     */
    public static Promiss<Bitmap, Throwable> setEmotionImage(View v, String url) {
        return setImage(v, ImageType.EMOTION, null, url, null);
    }

    /**
     * 設置圖片, 這是通用方法, 大多數情況下, 直接調用前面的方法即可
     * 
     * @param v
     *            要設置圖片的view, 現在支持ImageView 或者ImageSwitcher
     * @param type
     *            圖片的類型
     * @param date
     *            微博的日期
     * @param url
     *            圖片的URL 地址
     * @param progress
     *            在加載圖片時顯示的進度
     * @return 設置圖片的Promise 對象, 用於對在設置圖片後可能還有的後續動作進行處理
     */
    public static Promiss<Bitmap, Throwable> setImage(View v, ImageType type,
            String date, String url, View progress) {
        if (v == null) {
            return null;
        }
        if (v instanceof ImageView || v instanceof ImageSwitcher) {
            TaskJob<Bitmap> job = new ImageJob(v, type, date, url, progress);
            return DeferredManager.when(job).then(job);
        }
        return null;
    }

    public static void setText(View v, CharSequence text) {
        if (v == null) {
            return;
        }
        if (v instanceof TextView) {
            TextView textView = (TextView) v;
            textView.setText(text);
        }
    }

    public static void setText(View v, CharSequence text, OnClickListener l) {
        if (v == null) {
            return;
        }
        if (v instanceof TextView) {
            TextView textView = (TextView) v;
            textView.setText(text);
            textView.setOnClickListener(l);
        }
    }

    public static void setText(View v, CharSequence text, int mask) {
        if (v == null) {
            return;
        }
        if (v instanceof TextView) {
            TextView textView = (TextView) v;
            textView.setText(text);

            addLinks(textView, mask);
        }
    }

    /**
     * @param textView
     * @param mask
     */
    public static void addLinks(View v, int mask) {
        if (!(v instanceof TextView)) {
            return;
        }
        TextView textView = (TextView) v;
        if ((mask & WEB_URLS) != 0) {
            Linkify.addLinks(textView, Patterns.WEB_URL, null,
                    Linkify.sUrlMatchFilter, null);
        }
        if ((mask & TREND) != 0) {
            Linkify.addLinks(textView, LinkPattern.TREND, LinkScheme.TREND,
                    null, GROUP_ONE_TRANSFORM_FILTER);
        }
        if ((mask & AT_USER) != 0) {
            Linkify.addLinks(textView, LinkPattern.AT_USER, LinkScheme.USER,
                    null, GROUP_ONE_TRANSFORM_FILTER);
        }
        if ((mask & USER) != 0) {
            Linkify.addLinks(textView, LinkPattern.USER, LinkScheme.USER, null,
                    GROUP_ONE_TRANSFORM_FILTER);
        }
        if ((mask & EMOTION) != 0) {
            EmotionJob job = new EmotionJob(textView);
            DeferredManager.when(job).then(job);
        }
    }

    private static final class EmotionJob implements
            TaskJob<Map<Tuple<Integer, Integer>, Bitmap>> {

        private static final LruCache<String, String> emotions = new LruCache<String, String>(
                100);
        private TextView mTextView;

        private EmotionJob(TextView mTextView) {
            this.mTextView = mTextView;
        }

        @Override
        public Map<Tuple<Integer, Integer>, Bitmap> call() {
            CharSequence s = mTextView.getText();
            Matcher m = LinkPattern.EMOTION.matcher(s);

            Map<Tuple<Integer, Integer>, Bitmap> map = new HashMap<Tuple<Integer, Integer>, Bitmap>();
            while (m.find()) {
                int start = m.start();
                int end = m.end();

                String url = getEmotionUrl(s, start, end);
                if (url != null) {
                    try {
                        Bitmap b = BitmapUtil.fromUrl(ImageType.EMOTION, null,
                                url);
                        map.put(new Tuple<Integer, Integer>(start, end), b);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }

            return map;
        }

        @Override
        public void apply(Map<Tuple<Integer, Integer>, Bitmap> result) {
            Context context = mTextView.getContext();
            SpannableString s = SpannableString.valueOf(mTextView.getText());
            for (Tuple<Integer, Integer> range : result.keySet()) {
                ImageSpan span = new ImageSpan(context, result.get(range));
                s.setSpan(span, range.getValue1(), range.getValue2(),
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            mTextView.setText(s);
        }

        /**
         * @param s
         * @param start
         * @param end
         * @return
         */
        private String getEmotionUrl(CharSequence s, int start, int end) {
            String emotionPhrase = s.subSequence(start, end).toString();
            Log.d(TAG, "sub: (" + start + "," + end + ") = " + emotionPhrase);

            String url = emotions.get(emotionPhrase);
            if (url != null) {
                return url;
            }

            url = queryUrl(emotionPhrase);
            if (url != null) {
                emotions.put(emotionPhrase, url);
                return url;
            }

            return null;
        }

        /**
         * 從數據庫查詢是否有這個表情
         * 
         * @param string
         *            表情字符串, 例如: [哈哈]
         * @return 表情的URL 地址
         */
        private String queryUrl(String emotion) {
            EmotionHelper helper = new EmotionHelper(mTextView.getContext());
            Cursor cursor = null;
            try {
                helper.openForWrite();

                helper.tryDownloadData();

                String selection = EmotionHelper.Column.phrase.name() + "= ?";
                String[] selectionArgs = new String[] { emotion };
                cursor = helper.query(selection, selectionArgs, null);
                if (cursor.getCount() == 0) {
                    return null;
                }
                if (cursor.moveToFirst()) {
                    return cursor.getString(EmotionHelper.Column.url.ordinal());
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                helper.close();
            }
            return null;
        }

    }

    /**
     * @author shuaqiu Jun 9, 2013
     */
    private static final class ImageJob implements TaskJob<Bitmap> {
        private View mView;
        private ImageType mType;
        private String mDate;
        private String mUrl;
        private View mProgressView;

        /**
         * @param view
         * @param type
         * @param date
         * @param url
         * @param progressView
         */
        public ImageJob(View view, ImageType type, String date, String url,
                View progressView) {
            mView = view;
            mType = type;
            mDate = date;
            mUrl = url;
            mProgressView = progressView;
        }

        @Override
        public Bitmap call() throws Exception {
            try {
                return BitmapUtil.fromUrl(mType, mDate, mUrl);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        public void apply(Bitmap result) {
            if (result != null) {
                setImageBitmap(result);
                mView.setVisibility(View.VISIBLE);
            }

            if (mProgressView != null) {
                mProgressView.setVisibility(View.GONE);
            }
        }

        /**
         * @param result
         */
        private void setImageBitmap(Bitmap result) {
            if (mView instanceof ImageView) {
                ((ImageView) mView).setImageBitmap(result);
            } else if (mView instanceof ImageSwitcher) {
                Context context = mView.getContext();
                Resources resources = context.getResources();
                BitmapDrawable drawable = new BitmapDrawable(resources, result);
                ((ImageSwitcher) mView).setImageDrawable(drawable);
            }
        }
    }

    private interface LinkPattern {

        Pattern USER = Pattern.compile(USER_PATTERN_STR);

        Pattern AT_USER = Pattern.compile("@" + USER_PATTERN_STR);;

        Pattern TREND = Pattern.compile("#([^\\s:\\)）]+)#");

        Pattern EMOTION = Pattern.compile("\\[[\\w\\u4e00-\\u9fa5]+\\]");
    }

    private interface LinkScheme {
        String USER = String
                .format("%s/?%s=", Defs.USER_SCHEME, Defs.USER_NAME);

        String TREND = String.format("%s/?%s=", Defs.TREND_SCHEME,
                Defs.TREND_NAME);
    }

    /**
     * @author shuaqiu May 4, 2013
     */
    private static final class GroupOneTransformFilter implements
            TransformFilter {
        @Override
        public String transformUrl(Matcher match, String url) {
            return match.group(1);
        }
    }
}
