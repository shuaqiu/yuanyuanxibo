package com.shuaqiu.yuanyuanxibo.status;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;

import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.ViewBinder;

public class StatusActivity extends Activity implements OnTouchListener {

    private ViewBinder binder;

    private int position;
    private boolean isTouchMove;
    private float touchX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_status);

        binder = new StatusBinder(this);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        binder.bindView(position, getWindow().getDecorView());

        getWindow().getDecorView().setOnTouchListener(this);
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
                    binder.bindView(position, getWindow().getDecorView());
                } else if (event.getX() - touchX > 20) {
                    // move to right
                    position--;
                    binder.bindView(position, getWindow().getDecorView());
                }
                isTouchMove = false;
            }
            break;
        }
        return false;
    }
}
