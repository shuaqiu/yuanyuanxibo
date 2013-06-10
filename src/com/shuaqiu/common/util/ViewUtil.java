package com.shuaqiu.common.util;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.util.LruCache;
import android.text.Spannable;
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

import com.shuaqiu.common.promiss.DeferredManager;
import com.shuaqiu.common.promiss.Promiss;
import com.shuaqiu.common.promiss.impl.DeferredTask.TaskJob;
import com.shuaqiu.yuanyuanxibo.Defs;

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

    public static Promiss<Bitmap, Throwable> setImage(View v, String url) {
        return setImage(v, url, null);
    }

    public static Promiss<Bitmap, Throwable> setImage(View v, String url,
            View progress) {
        if (v == null) {
            return null;
        }
        if (v instanceof ImageView || v instanceof ImageSwitcher) {
            TaskJob<Bitmap> job = new ImageJob(v, url, progress);
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
            addEmotion(textView);
        }
    }

    private static void addEmotion(TextView textView) {
        Context context = textView.getContext();
        SpannableString s = SpannableString.valueOf(textView.getText());
        Matcher m = LinkPattern.EMOTION.matcher(s);

        while (m.find()) {
            int start = m.start();
            int end = m.end();

            String emotion = getEmotion(s, start, end);
            if (emotion != null) {
                Bitmap b = BitmapFactory.decodeFile(emotion);
                ImageSpan span = new ImageSpan(context, b);
                s.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }

        textView.setText(s);
    }

    /**
     * @param s
     * @param start
     * @param end
     * @return
     */
    private static String getEmotion(Spannable s, int start, int end) {
        CharSequence sub = s.subSequence(start, end);
        Log.d(TAG, "sub from " + start + " to " + end + " is " + sub);

        String emotion = emotions.get(sub.toString());
        if (emotion != null) {
            return emotion;
        }

        return null;
    }

    private static final LruCache<String, String> emotions = new LruCache<String, String>(
            100) {
        @Override
        protected String create(String key) {
            // TODO create emotion
            return key;
        };
    };

    /**
     * @author shuaqiu Jun 9, 2013
     */
    private static final class ImageJob implements TaskJob<Bitmap> {
        private View mView;
        private String mUrl;
        private View mProgressView;

        /**
         * @param view
         * @param url
         * @param progressView
         */
        public ImageJob(View view, String url, View progressView) {
            mView = view;
            mUrl = url;
            mProgressView = progressView;
        }

        @Override
        public Bitmap call() throws Exception {
            try {
                return BitmapUtil.fromUrl(mUrl);
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

        Pattern EMOTION = Pattern.compile("\\[[\\u4e00-\\u9fa5]+\\]");
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
