/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.comment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
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
import com.shuaqiu.yuanyuanxibo.friend.FriendSelectionActivity;
import com.shuaqiu.yuanyuanxibo.trend.TrendSelectionActivity;

/**
 * @author shuaqiu Jun 3, 2013
 */
public class SendActivity extends Activity implements OnClickListener,
        Callback<String>, TextWatcher, DialogInterface.OnClickListener {

    private static final String TAG = "SendActivity";

    private static final int MAX_CHAR_COUNT = 140;

    private static final int CODE_TREND = 1;
    private static final int CODE_FRIEND = 2;

    private ViewHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_send);

        initViewHolder();

        // 調整一下順序, 先綁定事件處理, 然後才設置界面的顯示 (主要是為了計數的顯示)
        initAction();

        initViewState();
    }

    private void initViewHolder() {
        if (mHolder != null) {
            return;
        }
        View decorView = getWindow().getDecorView();
        mHolder = ViewHolder.from(decorView);
    }

    /**
     * 
     */
    private void initAction() {
        mHolder.mContent.addTextChangedListener(this);

        mHolder.mSend.setOnClickListener(this);
        mHolder.mBack.setOnClickListener(this);

        mHolder.mRemainCharCount.setOnClickListener(this);

        mHolder.mTrend.setOnClickListener(this);
        mHolder.mAtUser.setOnClickListener(this);
        mHolder.mEmotion.setOnClickListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        int remainCharCount = MAX_CHAR_COUNT - s.length();
        mHolder.mRemainCharCount.setText(Integer.toString(remainCharCount));
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
        case R.id.remain_char_count:
            confirmDeleteContent();
            break;
        case R.id.action_trend:
            showTrends();
            break;
        case R.id.action_at_user:
            showFriends();
            break;
        case R.id.action_emotion:
            showEmotions();
            break;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
            deleteContent();
            break;
        case DialogInterface.BUTTON_NEGATIVE:
            dialog.dismiss();
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

    /**
     * 轉發微博API 調用的參數設置
     * 
     * @param content
     *            內容
     * @return 參數設置
     * @see API.Status.REPOST
     * 
     */
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

    /**
     * 評論微博或回复評論API 調用的參數設置
     * 
     * @param content
     *            內容
     * @return 參數設置
     * @see API.Status.CREATE
     * @see API.Status.REPLY
     * 
     */
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

        if (action != null && action.equals(Status.REPOST)
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

    private void confirmDeleteContent() {
        new AlertDialog.Builder(this).setTitle(R.string.tip)
                .setIcon(R.drawable.ic_action_delete)
                .setMessage(R.string.confirm_delete_conent)
                .setPositiveButton(R.string.ok, this)
                .setNegativeButton(R.string.cancel, this).show();
    }

    private void deleteContent() {
        mHolder.mContent.setText("");
    }

    /**
     * 
     */
    private void showTrends() {
        Intent intent = new Intent(this, TrendSelectionActivity.class);
        startActivityForResult(intent, CODE_TREND);
    }

    /**
     * 
     */
    private void showFriends() {
        Intent intent = new Intent(this, FriendSelectionActivity.class);
        startActivityForResult(intent, CODE_FRIEND);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case CODE_TREND:
        case CODE_FRIEND:
            if (resultCode == RESULT_OK) {
                String selected = data.getStringExtra("selected");
                int cursorPosition = mHolder.mContent.getSelectionEnd();
                mHolder.mContent.getText().insert(cursorPosition, selected);
            }
            break;
        }

    }

    /**
     * 
     */
    private void showEmotions() {
        if (mHolder.mEmotions == null) {
            mHolder.mEmotions = mHolder.mStubEmotions.inflate();
        }
        mHolder.mEmotions.setVisibility(View.VISIBLE);
    }

    /**
     * 設置界面的顯示
     */
    private void initViewState() {
        Intent intent = getIntent();
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
            // 將當前的內容全選了, 便於直接刪除
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
            // 設置輸入光標的位置
            mHolder.mContent.setSelection(replyTo.length());
        }

        mHolder.mRepost.setVisibility(View.VISIBLE);
        mHolder.mComment.setVisibility(View.GONE);
        mHolder.mCommentOriginal.setVisibility(View.GONE);
    }

    private static class ViewHolder {
        private TextView mTitle;
        private EditText mContent;
        private TextView mRemainCharCount;
        private CheckBox mRepost;
        private CheckBox mComment;
        private CheckBox mCommentOriginal;
        private View mBack;
        private View mSend;
        private View mTrend;
        private View mAtUser;
        private View mEmotion;

        private ViewStub mStubEmotions;
        private View mEmotions;

        static ViewHolder from(View v) {
            Object tag = v.getTag();
            if (tag != null && tag instanceof ViewHolder) {
                return (ViewHolder) tag;
            }

            ViewHolder holder = new ViewHolder();
            v.setTag(holder);

            holder.mContent = (EditText) v.findViewById(R.id.content);
            holder.mTitle = (TextView) v.findViewById(R.id.title);
            holder.mRemainCharCount = (TextView) v
                    .findViewById(R.id.remain_char_count);

            holder.mRepost = (CheckBox) v.findViewById(R.id.repost_weibo);
            holder.mComment = (CheckBox) v.findViewById(R.id.comment_weibo);
            holder.mCommentOriginal = (CheckBox) v
                    .findViewById(R.id.comment_original_weibo);

            holder.mBack = v.findViewById(R.id.back);
            holder.mSend = v.findViewById(R.id.send);
            holder.mTrend = v.findViewById(R.id.action_trend);
            holder.mAtUser = v.findViewById(R.id.action_at_user);
            holder.mEmotion = v.findViewById(R.id.action_emotion);

            holder.mStubEmotions = (ViewStub) v.findViewById(R.id.emotions);

            return holder;
        }
    }
}
