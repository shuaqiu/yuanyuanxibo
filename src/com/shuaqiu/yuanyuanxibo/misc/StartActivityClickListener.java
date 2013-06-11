/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.misc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.shuaqiu.yuanyuanxibo.Actions.Comment;
import com.shuaqiu.yuanyuanxibo.Actions.Status;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.StateKeeper;
import com.shuaqiu.yuanyuanxibo.comment.CommentActivity;
import com.shuaqiu.yuanyuanxibo.comment.SendActivity;
import com.shuaqiu.yuanyuanxibo.status.RepostActivity;

/**
 * @author shuaqiu 2013-6-3
 */
public class StartActivityClickListener implements OnClickListener {

    private Bundle mArgs;

    public StartActivityClickListener(Bundle args) {
        mArgs = args;

    }

    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        Intent intent = new Intent();
        intent.putExtras(mArgs);
        intent.putExtra("access_token",
                StateKeeper.accessToken.getAccessToken());

        switch (v.getId()) {
        case R.id.attitudes_count:
        case R.id.retweeted_attitudes_count:
            break;
        case R.id.reposts_count:
        case R.id.retweeted_reposts_count:
            intent.setClass(context, RepostActivity.class);
            intent.setAction(Status.REPOST_LIST);
            break;
        case R.id.comments_count:
        case R.id.retweeted_comments_count:
            intent.setClass(context, CommentActivity.class);
            intent.setAction(Comment.FOR_STATUS);
            break;
        case R.id.to_reply:
            intent.setClass(context, SendActivity.class);
            intent.setAction(Comment.REPLY);
            break;
        }

        context.startActivity(intent);
    }

}
