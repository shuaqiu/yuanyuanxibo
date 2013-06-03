/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.comment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.shuaqiu.common.task.AsyncHttpPostTask;
import com.shuaqiu.common.task.AsyncTaskListener;
import com.shuaqiu.yuanyuanxibo.API;
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
    }

    @Override
    public void onClick(View v) {
        Editable comment = mCommentView.getText();
        if (comment.length() == 0) {
            return;
        }

        Intent intent = getIntent();
        Bundle params = intent.getExtras();
        try {
            params.putString("comment",
                    URLEncoder.encode(comment.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage(), e);
            return;
        }

        new AsyncHttpPostTask(params, this).execute(API.Comment.REPLY);
    }

    @Override
    public void onPostExecute(JSONObject result) {
        Toast.makeText(this, R.string.sent, Toast.LENGTH_SHORT).show();
        finish();
    }
}
