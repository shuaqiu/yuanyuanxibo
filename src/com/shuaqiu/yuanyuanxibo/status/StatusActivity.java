package com.shuaqiu.yuanyuanxibo.status;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;

import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.ViewBinder;
import com.shuaqiu.yuanyuanxibo.WeiboStatus;

public class StatusActivity extends Activity implements OnTouchListener {

    private ViewBinder mBinder;

    private int position;
    private boolean isTouchMove;
    private float touchX;

    private JSONArray mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_status);

        mBinder = new StatusBinder(this);
        mData = WeiboStatus.getInstance().getStatus();

        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);

        bindView(getWindow().getDecorView(), position);

        getWindow().getDecorView().setOnTouchListener(this);
    }

    /**
     * @param decorView
     * @param position
     */
    private void bindView(View decorView, int position) {
        final JSONObject status = mData.optJSONObject(position);
        if (status != null) {
            mBinder.bindView(getWindow().getDecorView(), status);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            isTouchMove = true;
            touchX = event.getX();
            break;
        case MotionEvent.ACTION_UP:
            if (isTouchMove) {
                if (event.getX() - touchX < -20) {
                    // move to left
                    position++;
                    bindView(getWindow().getDecorView(), position);
                } else if (event.getX() - touchX > 20) {
                    // move to right
                    position--;
                    bindView(getWindow().getDecorView(), position);
                }
                isTouchMove = false;
            }
            break;
        }
        return false;
    }
}
