/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.comment;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.shuaqiu.common.task.AsyncHttpPostTask;
import com.shuaqiu.common.task.AsyncTaskListener;
import com.shuaqiu.yuanyuanxibo.API;
import com.shuaqiu.yuanyuanxibo.Actions;
import com.shuaqiu.yuanyuanxibo.R;

/**
 * @author shuaqiu Jun 3, 2013
 */
public class SendCommentActivity extends Activity implements OnClickListener,
        AsyncTaskListener<JSONObject> {

    private static final String TAG = "SendCommentActivity";

    private EditText mCommentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_send_comment);

        mCommentView = (EditText) findViewById(R.id.comment);
        findViewById(R.id.send).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.send:
            sendComment();
            break;
        case R.id.cancel:
            finish();
        }
    }

    /**
     * 
     */
    protected void sendComment() {
        Editable comment = mCommentView.getText();
        if (comment.length() == 0) {
            return;
        }

        Intent intent = getIntent();
        Bundle params = intent.getExtras();
        params.putString("comment", comment.toString());

        AsyncHttpPostTask task = new AsyncHttpPostTask(params, this);

        String action = intent.getAction();
        if (action == null || action.equals(Actions.COMMENT_CREATE)) {
            task.execute(API.Comment.CREATE);
        } else if (action.equals(Actions.COMMENT_REPLY)) {
            task.execute(API.Comment.REPLY);
        }
    }

    @Override
    public void onPostExecute(JSONObject result) {
        Toast.makeText(this, R.string.sent, Toast.LENGTH_SHORT).show();
        finish();
    }
}
