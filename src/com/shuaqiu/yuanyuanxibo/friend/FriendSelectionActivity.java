/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.friend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;

import com.shuaqiu.yuanyuanxibo.R;

/**
 * @author shuaqiu 2013-6-13
 * 
 */
public class FriendSelectionActivity extends FragmentActivity implements
        OnClickListener {

    private ViewHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friend_selection);

        initViewHolder();

        mHolder.mOk.setOnClickListener(this);
        mHolder.mBack.setOnClickListener(this);
    }

    private void initViewHolder() {
        if (mHolder != null) {
            return;
        }

        View decorView = getWindow().getDecorView();
        Object tag = decorView.getTag();
        if (tag != null && tag instanceof ViewHolder) {
            mHolder = (ViewHolder) tag;
            return;
        }

        mHolder = new ViewHolder();

        mHolder.mSelectedFriends = (EditText) findViewById(R.id.selected_friends);
        mHolder.mOk = findViewById(R.id.ok);
        mHolder.mBack = findViewById(R.id.back);

        mHolder.mPager = (ViewPager) findViewById(R.id.pager);

        decorView.setTag(mHolder);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.ok:
            selected();
            break;
        case R.id.back:
            finish();
            break;
        }
    }

    private void selected() {
        Editable text = mHolder.mSelectedFriends.getText();
        Intent data = new Intent();
        data.putExtra("selectedFriends", text.toString());
        setResult(RESULT_OK, data);
        finishActivity(1);
    }

    private static class ViewHolder {
        private EditText mSelectedFriends;

        private View mOk;
        private View mBack;

        private ViewPager mPager;
    }
}
