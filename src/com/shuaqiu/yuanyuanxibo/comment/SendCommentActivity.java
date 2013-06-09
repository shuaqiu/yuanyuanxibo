/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.comment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.shuaqiu.common.promiss.Callback;
import com.shuaqiu.common.promiss.DeferredManager;
import com.shuaqiu.common.task.PostCallable;
import com.shuaqiu.yuanyuanxibo.API;
import com.shuaqiu.yuanyuanxibo.Actions;
import com.shuaqiu.yuanyuanxibo.R;

/**
 * @author shuaqiu Jun 3, 2013
 */
public class SendCommentActivity extends Activity implements OnClickListener,
        Callback<String> {

    // private static final String TAG = "SendCommentActivity";

    private EditText mCommentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_send_comment);

        mCommentView = (EditText) findViewById(R.id.comment);
        findViewById(R.id.send).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.send:
            sendComment();
            break;
        case R.id.back:
            finish();
            break;
        }
    }

    private void sendComment() {
        Editable comment = mCommentView.getText();
        if (comment.length() == 0) {
            return;
        }

        Intent intent = getIntent();
        Bundle param = intent.getExtras();
        param.putString("comment", comment.toString());

        String url = getUrl(intent);
        DeferredManager.when(new PostCallable(url, param)).then(this);
    }

    /**
     * @param intent
     * @return
     */
    private String getUrl(Intent intent) {
        String action = intent.getAction();
        if (action == null || action.equals(Actions.COMMENT_CREATE)) {
            return API.Comment.CREATE;
        }
        if (action.equals(Actions.COMMENT_REPLY)) {
            return API.Comment.REPLY;
        }
        return null;
    }

    @Override
    public void apply(String result) {
        Toast.makeText(this, R.string.sent, Toast.LENGTH_SHORT).show();
        finish();
    }
}
