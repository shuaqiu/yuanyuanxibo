package com.shuaqiu.yuanyuanxibo.comment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.shuaqiu.yuanyuanxibo.Actions.Comment;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.StateKeeper;
import com.shuaqiu.yuanyuanxibo.status.StatusBinder;

public class CommentActivity extends FragmentActivity implements
        OnClickListener {

    // private static final String TAG = "CommentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_comment);

        findViewById(R.id.comment).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.comment:
            toSendActivity();
            break;
        case R.id.back:
            finish();
            break;
        }
    }

    /**
     * 
     @see StatusBinder#buildClickArg()
     */
    private void toSendActivity() {
        Intent intent = new Intent();

        intent.putExtra("access_token",
                StateKeeper.accessToken.getAccessToken());
        intent.putExtras(getIntent());

        intent.setAction(Comment.CREATE);
        intent.setClass(this, SendActivity.class);

        startActivity(intent);
    }
}
