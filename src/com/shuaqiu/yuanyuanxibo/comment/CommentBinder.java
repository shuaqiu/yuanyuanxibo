/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.comment;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.shuaqiu.common.TimeHelper;
import com.shuaqiu.common.util.ViewUtil;
import com.shuaqiu.common.widget.ViewBinder;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.misc.StartActivityClickListener;

/**
 * @author shuaqiu 2013-5-1
 */
public class CommentBinder implements ViewBinder<JSONObject> {
    public enum Type {
        USER, STATUS
    }

    // private Context mContext;
    private TimeHelper mTimeHelper;
    private Type mType;

    public CommentBinder(Context context, Type type) {
        // mContext = context;
        mTimeHelper = new TimeHelper(context);
        mType = type;
    }

    @Override
    public void bindView(View view, final JSONObject comment) {
        View usernameView = view.findViewById(R.id.user_name);
        ViewUtil.setText(usernameView, optUsername(comment), ViewUtil.USER);

        ViewUtil.setText(view.findViewById(R.id.created_at),
                optCreateTime(comment));

        ViewUtil.setText(view.findViewById(R.id.text),
                comment.optString("text", ""), ViewUtil.ALL);

        ViewUtil.setText(view.findViewById(R.id.source), optSource(comment));

        setReplyFor(view, comment);

        setReplyAction(view, comment);
    }

    /**
     * @param view
     * @param comment
     */
    protected void setReplyFor(View view, JSONObject comment) {
        String content = null;

        JSONObject target = comment.optJSONObject("reply_comment");
        if (target == null) {
            // 這個評論是直接對微博的
            if (mType == Type.STATUS) {
                // 如果是顯示微博的評論列表, 則不需要顯示原來的微博信息了
                view.findViewById(R.id.reply_container)
                        .setVisibility(View.GONE);
                return;
            }

            // 改爲顯示評論的微博內容
            target = comment.optJSONObject("status");
        }
        content = target.optString("text", "");

        ViewUtil.setText(view.findViewById(R.id.reply_content), content,
                ViewUtil.ALL);
    }

    /**
     * @param view
     * @param comment
     */
    protected void setReplyAction(View view, final JSONObject comment) {
        Bundle args = new Bundle(2);
        JSONObject status = comment.optJSONObject("status");
        long statusId = status.optLong("id");
        args.putLong("id", statusId);
        args.putLong("cid", comment.optLong("id"));
        OnClickListener l = new StartActivityClickListener(args);
        view.findViewById(R.id.to_reply).setOnClickListener(l);
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

    protected String optSource(JSONObject status) {
        String source = status.optString("source");
        // return Html.fromHtml(source);
        return source.replaceAll("<a.*?>(.*?)</a>", "$1");
    }
}
