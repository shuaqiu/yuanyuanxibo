package com.shuaqiu.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shuaqiu.common.task.AsyncImageViewTask;
import com.shuaqiu.yuanyuanxibo.Defs;

/**
 * @author shuaqiu 2013-5-3
 */
public class ViewUtil {

    public static void setViewImage(View v, String url) {
        if (v == null) {
            return;
        }
        if (v instanceof ImageView) {
            ImageView imageView = (ImageView) v;
            AsyncImageViewTask task = new AsyncImageViewTask(imageView);
            task.execute(url);
        }
    }

    public static void setViewImage(View v, String url, View progressView) {
        if (v == null) {
            return;
        }
        if (v instanceof ImageView) {
            ImageView imageView = (ImageView) v;
            AsyncImageViewTask task = new AsyncImageViewTask(imageView,
                    progressView);
            task.execute(url);
        }
    }

    public static void setViewText(View v, CharSequence text) {
        if (v == null) {
            return;
        }
        if (v instanceof TextView) {
            TextView textView = (TextView) v;
            textView.setText(text);
        }
    }

    public static void setViewStatusText(View v, CharSequence text) {
        if (v == null) {
            return;
        }
        if (v instanceof TextView) {
            TextView textView = (TextView) v;
            textView.setText(text);

            addUserLinks(textView);
            addTrendLinks(textView);
            // Linkify.addLinks(textView, Linkify.WEB_URLS);
        }
    }

    /**
     * @param textView
     */
    private static void addUserLinks(TextView textView) {
        Pattern pattern = Pattern.compile("@([^\\s:\\)）]+)");
        String scheme = String.format("%s/?%s=", Defs.USER_SCHEME,
                Defs.USER_NAME);
        Linkify.addLinks(textView, pattern, scheme, null,
                new GroupOneTransformFilter());
    }

    /**
     * @param textView
     */
    private static void addTrendLinks(TextView textView) {
        Pattern pattern = Pattern.compile("#([^\\s:\\)）]+)#");
        String scheme = String.format("%s/?%s=", Defs.USER_SCHEME,
                Defs.USER_NAME);
        Linkify.addLinks(textView, pattern, scheme, null,
                new GroupOneTransformFilter());
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
