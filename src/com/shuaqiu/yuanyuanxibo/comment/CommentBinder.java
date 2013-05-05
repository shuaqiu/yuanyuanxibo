/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.comment;

import org.json.JSONObject;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.shuaqiu.common.TimeHelper;
import com.shuaqiu.common.ViewUtil;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.ViewBinder;

/**
 * @author shuaqiu 2013-5-1
 */
public class CommentBinder implements ViewBinder {
    private TimeHelper mTimeHelper;

    public CommentBinder(Context context) {
        mTimeHelper = new TimeHelper(context);
    }

    @Override
    public void bindView(View view, final JSONObject comment) {
        View usernameView = view.findViewById(R.id.user_name);
        ViewUtil.setViewText(usernameView, optUsername(comment));
        ViewUtil.addUserLinks((TextView) usernameView, ViewUtil.USER_PATTERN);

        ViewUtil.setViewText(view.findViewById(R.id.created_at),
                optCreateTime(comment));

        ViewUtil.setViewStatusText(view.findViewById(R.id.text),
                comment.optString("text", ""));

        ViewUtil.setViewText(view.findViewById(R.id.source), optSource(comment));
    }

    protected String optUsername(JSONObject status) {
        JSONObject user = status.optJSONObject("user");
        if (user == null) {
            return "";
        }
        return user.optString("screen_name", "");
    }

    // "Sat Apr 27 00:59:08 +0800 2013"
    protected String optCreateTime(JSONObject status) {
        String createdAt = status.optString("created_at");
        return mTimeHelper.beautyTime(createdAt);
    }

    protected Spanned optSource(JSONObject status) {
        String source = status.optString("source");
        return Html.fromHtml(source);
        // return source.replaceAll("<a.*?>(.*?)</a>", "$1");
    }
}
