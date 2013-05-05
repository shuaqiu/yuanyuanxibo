package com.shuaqiu.yuanyuanxibo.user;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

import com.shuaqiu.common.ViewUtil;
import com.shuaqiu.common.task.AsyncHttpGetTask;
import com.shuaqiu.common.task.AsyncTaskListener;
import com.shuaqiu.yuanyuanxibo.API.User;
import com.shuaqiu.yuanyuanxibo.Defs;
import com.shuaqiu.yuanyuanxibo.StateKeeper;
import com.shuaqiu.yuanyuanxibo.R;

public class UserInfoActivity extends Activity implements
        AsyncTaskListener<JSONObject> {
    static final String TAG = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_user_info);

        String accessToken = StateKeeper.accessToken.getAccessToken();
        String username = extractUsername();

        Bundle args = new Bundle();
        args.putString("access_token", accessToken);
        args.putString("screen_name", username);
        new AsyncHttpGetTask(args, this).execute(User.USER_INFO);
    }

    private String extractUsername() {
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri == null) {
            return null;
        }
        if (uri.getScheme().equals(Defs.SCHEME)) {
            return uri.getQueryParameter(Defs.USER_NAME);
        }
        return null;
    }

    @Override
    public void onPostExecute(JSONObject data) {
        if (data == null) {
            return;
        }
        ViewUtil.setViewImage(findViewById(R.id.profile_image),
                data.optString("profile_image_url", null));
        ViewUtil.setViewText(findViewById(R.id.user_name),
                data.optString("screen_name"));
        ViewUtil.setViewText(findViewById(R.id.location),
                data.optString("location"));
        ViewUtil.setViewText(findViewById(R.id.description),
                data.optString("description"));
    }
}
