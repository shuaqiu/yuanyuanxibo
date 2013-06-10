/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.shuaqiu.yuanyuanxibo.Actions.Status;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.StateKeeper;
import com.shuaqiu.yuanyuanxibo.comment.SendActivity;

/**
 * @author shuaqiu Jun 9, 2013
 */
public class RepostActivity extends FragmentActivity implements OnClickListener {

    // private static final String TAG = "CommentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_repost);

        findViewById(R.id.repost).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.repost:
            toSendRepostActivity();
            break;
        case R.id.back:
            finish();
            break;
        }
    }

    private void toSendRepostActivity() {
        Intent intent = new Intent();

        intent.putExtra("access_token",
                StateKeeper.accessToken.getAccessToken());
        Intent startIntent = getIntent();
        intent.putExtra("id", startIntent.getLongExtra("id", 0));
        intent.putExtra("isRetweeted",
                startIntent.getBooleanExtra("isRetweeted", false));

        intent.setAction(Status.REPOST);
        intent.setClass(this, SendActivity.class);

        startActivity(intent);
    }
}
