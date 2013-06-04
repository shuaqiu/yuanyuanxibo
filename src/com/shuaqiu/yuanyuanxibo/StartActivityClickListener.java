/**
 * 
 */
package com.shuaqiu.yuanyuanxibo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.shuaqiu.yuanyuanxibo.comment.CommentActivity;
import com.shuaqiu.yuanyuanxibo.comment.SendCommentActivity;

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
            break;
        case R.id.comments_count:
        case R.id.retweeted_comments_count:
            intent.setClass(context, CommentActivity.class);
            intent.setAction(Actions.STATUS_COMMENT);
            intent.putExtras(mArgs);
            break;
        case R.id.to_reply:
            intent.setClass(context, SendCommentActivity.class);
            intent.setAction(Actions.COMMENT_REPLY);
            break;
        }

        context.startActivity(intent);
    }

}
