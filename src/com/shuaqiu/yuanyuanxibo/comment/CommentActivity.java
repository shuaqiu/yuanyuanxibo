package com.shuaqiu.yuanyuanxibo.comment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.shuaqiu.yuanyuanxibo.R;

public class CommentActivity extends FragmentActivity {

    private static final String TAG = "statusactivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_comment);
    }

}
