package com.shuaqiu.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import com.shuaqiu.common.task.AsyncImageViewTask;
import com.shuaqiu.yuanyuanxibo.Defs;

/**
 * @author shuaqiu 2013-5-3
 */
public class ViewUtil {
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

    public static void setImage(View v, String url) {
        setImage(v, url, null);
    }

    public static void setImage(View v, String url, View progressView) {
        if (v == null) {
            return;
        }
        if (v instanceof ImageView || v instanceof ImageSwitcher) {
            AsyncImageViewTask task = new AsyncImageViewTask(v, progressView);
            task.execute(url);
        }
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
            // Linkify.addLinks(textView, LinkPattern.USER, LinkScheme.USER,
            // null,
            // GROUP_ONE_TRANSFORM_FILTER);
        }
    }

    private interface LinkPattern {

        Pattern USER = Pattern.compile(USER_PATTERN_STR);

        Pattern AT_USER = Pattern.compile("@" + USER_PATTERN_STR);;

        Pattern TREND = Pattern.compile("#([^\\s:\\)）]+)#");

        Pattern EMOTION = Pattern.compile("([\\w\\u4e00-\\u9fa5]+)");
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
