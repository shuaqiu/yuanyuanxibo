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
    /** 顯示評論的方式 */
    public enum Type {
        /** 顯示當前登錄用戶的評論列表 */
        USER,
        /** 顯示某條微博的評論 */
        STATUS
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
        ViewHolder holder = ViewHolder.from(view);

        setComment(holder, comment);
        setReplyFor(holder, comment);
        setAction(holder, comment);
    }

    protected void setComment(ViewHolder holder, final JSONObject comment) {
        ViewUtil.setText(holder.mUsername, optUsername(comment), ViewUtil.USER);
        ViewUtil.setText(holder.mCreateAt, optCreateTime(comment));
        ViewUtil.setText(holder.mComment, comment.optString("text", ""),
                ViewUtil.ALL);
        ViewUtil.setText(holder.mSource, optSource(comment));
    }

    /**
     * 顯示評論的對象, 也就是評論是針對什麼的
     * 
     * @param holder
     * @param comment
     */
    protected void setReplyFor(ViewHolder holder, final JSONObject comment) {
        JSONObject target = comment.optJSONObject("reply_comment");
        if (target == null) {
            // 這個評論是直接對微博的

            if (mType == Type.STATUS) {
                // 如果是顯示微博的評論列表, 則不需要顯示原來的微博信息了(因爲剛剛才從微博界面過來)
                holder.mReplyContainer.setVisibility(View.GONE);
                return;
            }

            // 改爲顯示評論的微博內容
            target = comment.optJSONObject("status");
        }
        String content = target.optString("text", "");
        ViewUtil.setText(holder.mReplyContent, content, ViewUtil.ALL);
    }

    /**
     * 綁定按鈕的事件
     * 
     * @param holder
     * @param comment
     */
    protected void setAction(ViewHolder holder, final JSONObject comment) {
        Bundle args = buildClickArgs(comment);
        OnClickListener l = new StartActivityClickListener(args);
        holder.mToReply.setOnClickListener(l);
    }

    /**
     * 構造在點擊"回復" 按鈕時, 傳遞的參數
     * 
     * @param comment
     * @return
     */
    protected Bundle buildClickArgs(final JSONObject comment) {
        Bundle args = new Bundle(3);
        JSONObject status = comment.optJSONObject("status");
        args.putLong("id", status.optLong("id"));
        args.putLong("cid", comment.optLong("id"));
        args.putString("username", optUsername(comment));
        return args;
    }

    /**
     * 獲取評論的作者名稱
     * 
     * @param comment
     * @return
     */
    protected String optUsername(final JSONObject comment) {
        JSONObject user = comment.optJSONObject("user");
        if (user == null) {
            return "";
        }
        return user.optString("screen_name", "");
    }

    // "Sat Apr 27 00:59:08 +0800 2013"
    protected String optCreateTime(final JSONObject comment) {
        String createdAt = comment.optString("created_at");
        return mTimeHelper.beautyTime(createdAt);
    }

    /**
     * 獲取評論的來源(客戶端)
     * 
     * @param comment
     * @return
     */
    protected String optSource(final JSONObject comment) {
        String source = comment.optString("source");
        // return Html.fromHtml(source);
        return source.replaceAll("<a.*?>(.*?)</a>", "$1");
    }

    protected static class ViewHolder {
        protected View mUsername;
        protected View mCreateAt;
        protected View mComment;
        protected View mSource;
        protected View mReplyContainer;
        protected View mReplyContent;
        protected View mToReply;

        protected static ViewHolder from(View v) {
            Object tag = v.getTag();
            if (tag != null && tag instanceof ViewHolder) {
                return (ViewHolder) tag;
            }

            ViewHolder holder = new ViewHolder();
            v.setTag(holder);

            holder.mUsername = v.findViewById(R.id.user_name);
            holder.mCreateAt = v.findViewById(R.id.created_at);
            holder.mComment = v.findViewById(R.id.text);
            holder.mSource = v.findViewById(R.id.source);
            holder.mReplyContainer = v.findViewById(R.id.reply_container);
            holder.mReplyContent = v.findViewById(R.id.reply_content);
            holder.mToReply = v.findViewById(R.id.to_reply);

            return holder;
        }
    }
}
