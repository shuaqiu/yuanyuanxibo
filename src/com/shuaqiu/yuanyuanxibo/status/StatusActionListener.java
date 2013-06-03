/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.shuaqiu.yuanyuanxibo.Defs;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.comment.CommentActivity;

/**
 * @author shuaqiu 2013-6-3
 * 
 */
public class StatusActionListener implements OnClickListener {

    private long mStatusId;

    public StatusActionListener(long statusId) {
        mStatusId = statusId;

    }

    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        Intent intent = new Intent();

        switch (v.getId()) {
        case R.id.attitudes_count:
        case R.id.retweeted_attitudes_count:
            break;
        case R.id.reposts_count:
        case R.id.retweeted_reposts_count:
            break;
        case R.id.comments_count:
        case R.id.retweeted_comments_count:
            intent.setClass(context, CommentActivity.class);
            intent.setAction(Defs.Action.STATUS_COMMENT);
            intent.putExtra("id", mStatusId);
            break;
        }

        context.startActivity(intent);
    }

}
