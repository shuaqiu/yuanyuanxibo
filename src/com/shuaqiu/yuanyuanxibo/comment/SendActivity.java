/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.comment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shuaqiu.common.promiss.Callback;
import com.shuaqiu.common.promiss.DeferredManager;
import com.shuaqiu.common.task.PostCallable;
import com.shuaqiu.yuanyuanxibo.API;
import com.shuaqiu.yuanyuanxibo.Actions.Comment;
import com.shuaqiu.yuanyuanxibo.Actions.Status;
import com.shuaqiu.yuanyuanxibo.R;

/**
 * @author shuaqiu Jun 3, 2013
 */
public class SendActivity extends Activity implements OnClickListener,
        Callback<String> {

    private static final String TAG = "SendActivity";

    private ViewHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_send);

        initViewHolder();

        Intent intent = getIntent();
        initViewState(intent);

        initAction();
    }

    private void initViewHolder() {
        if (mHolder != null) {
            return;
        }
        View decorView = getWindow().getDecorView();
        Object tag = decorView.getTag();
        if (tag != null) {
            mHolder = (ViewHolder) tag;
            return;
        }

        mHolder = new ViewHolder();

        mHolder.mContent = (EditText) findViewById(R.id.content);
        mHolder.mTitle = (TextView) findViewById(R.id.title);
        mHolder.mRepost = (CheckBox) findViewById(R.id.repost_weibo);
        mHolder.mComment = (CheckBox) findViewById(R.id.comment_weibo);
        mHolder.mCommentOriginal = (CheckBox) findViewById(R.id.comment_original_weibo);

        mHolder.mBack = findViewById(R.id.back);
        mHolder.mSend = findViewById(R.id.send);
        mHolder.mTrend = findViewById(R.id.action_trend);
        mHolder.mAt = findViewById(R.id.action_at);
        mHolder.mEmotion = findViewById(R.id.action_emotion);

        decorView.setTag(mHolder);
    }

    /**
     * @param intent
     */
    private void initViewState(Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "action: " + action);
        if (action != null && action.equals(Status.REPOST)) {
            initRepostState(intent);
        } else {
            initCommentState(intent);
        }
    }

    /**
     * 轉發微博
     * 
     * @param intent
     */
    private void initRepostState(Intent intent) {
        mHolder.mTitle.setText(R.string.repost_weibo);

        mHolder.mRepost.setVisibility(View.GONE);
        mHolder.mComment.setVisibility(View.VISIBLE);

        boolean isRetweeted = intent.getBooleanExtra("isRetweeted", false);
        Log.d(TAG, "isRetweeted: " + isRetweeted);
        if (isRetweeted) {
            // 設置當前的內容爲之前轉發的內容
            String username = intent.getStringExtra("username");
            String current = intent.getStringExtra("text");
            String content = String.format("//@%s:%s", username, current);
            mHolder.mContent.setText(content);

            mHolder.mCommentOriginal.setVisibility(View.VISIBLE);
        } else {
            String defaultConent = getString(R.string.default_repost_content);
            mHolder.mContent.setText(defaultConent);
            mHolder.mContent.setSelection(0, defaultConent.length());

            mHolder.mCommentOriginal.setVisibility(View.GONE);
        }
    }

    /**
     * 評論微博或回復評論
     * 
     * @param intent
     */
    private void initCommentState(Intent intent) {
        mHolder.mTitle.setText(R.string.send_comment);

        String action = intent.getAction();
        if (action == null || action.equals(Comment.CREATE)) {
        } else if (action.equals(Comment.REPLY)) {
            String username = intent.getStringExtra("username");
            String replyTo = getString(R.string.default_comment_content,
                    username);
            mHolder.mContent.setText(replyTo);
            mHolder.mContent.setSelection(replyTo.length());
        }

        mHolder.mRepost.setVisibility(View.VISIBLE);
        mHolder.mComment.setVisibility(View.GONE);
        mHolder.mCommentOriginal.setVisibility(View.GONE);
    }

    /**
     * 
     */
    private void initAction() {
        mHolder.mSend.setOnClickListener(this);
        mHolder.mBack.setOnClickListener(this);
        mHolder.mTrend.setOnClickListener(this);
        mHolder.mAt.setOnClickListener(this);
        mHolder.mEmotion.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.send:
            send();
            break;
        case R.id.back:
            finish();
            break;
        case R.id.action_trend:
            break;
        case R.id.action_at:
            break;
        case R.id.action_emotion:
            break;
        }
    }

    private void send() {
        Bundle param = buildParam();
        if (param == null) {
            Toast.makeText(this, R.string.empty, Toast.LENGTH_SHORT).show();
            return;
        }
        String url = getUrl();
        DeferredManager.when(new PostCallable(url, param)).then(this);
    }

    private Bundle buildParam() {
        Editable content = mHolder.mContent.getText();

        Intent intent = getIntent();
        String action = intent.getAction();
        if (action != null && action.equals(Status.REPOST)) {
            // 轉發微博
            return buildRepostParam(content);
        }

        // 評論微博或回復評論
        if (content.length() == 0) {
            // 內容不能爲空
            return null;
        }
        return buildCommentParam(content);
    }

    private Bundle buildRepostParam(Editable content) {
        Intent intent = getIntent();
        Bundle param = intent.getExtras();

        if (content.length() > 0) {
            param.putString("status", content.toString());
        }
        if (mHolder.mComment.isChecked()
                && mHolder.mCommentOriginal.isChecked()) {
            param.putInt("is_comment", 3);
        } else if (mHolder.mComment.isChecked()) {
            param.putInt("is_comment", 1);
        } else if (mHolder.mCommentOriginal.isChecked()) {
            param.putInt("is_comment", 2);
        }
        return param;
    }

    private Bundle buildCommentParam(Editable content) {
        Intent intent = getIntent();
        Bundle param = intent.getExtras();

        if (mHolder.mRepost.isChecked()) {
            // 如果在評論的同時轉發微博, 則直接用轉發微博的API
            param.putString("status", content.toString());
            param.putInt("is_comment", 1);
            if (mHolder.mCommentOriginal.isChecked()) {
                param.putInt("is_comment", 3);
            }
        } else {
            // 評論微博或回復評論
            param.putString("comment", content.toString());

            if (mHolder.mCommentOriginal.isChecked()) {
                param.putInt("comment_ori", 1);
            }
        }

        return param;
    }

    private String getUrl() {
        Intent intent = getIntent();
        String action = intent.getAction();

        if ((action != null && action.equals(Status.REPOST))
                || mHolder.mRepost.isChecked()) {
            return API.Status.REPOST;
        }
        if (action == null || action.equals(Comment.CREATE)) {
            return API.Comment.CREATE;
        }
        if (action.equals(Comment.REPLY)) {
            return API.Comment.REPLY;
        }
        return null;
    }

    @Override
    public void apply(String result) {
        Toast.makeText(this, R.string.sent, Toast.LENGTH_SHORT).show();
        finish();
    }

    private static class ViewHolder {
        private TextView mTitle;
        private EditText mContent;
        private TextView mCharCount;
        private CheckBox mRepost;
        private CheckBox mComment;
        private CheckBox mCommentOriginal;
        private View mBack;
        private View mSend;
        private View mTrend;
        private View mAt;
        private View mEmotion;

    }
}
