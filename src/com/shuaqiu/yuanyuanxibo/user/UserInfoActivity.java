package com.shuaqiu.yuanyuanxibo.user;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.shuaqiu.common.ImageType;
import com.shuaqiu.common.promiss.Callback;
import com.shuaqiu.common.promiss.DeferredManager;
import com.shuaqiu.common.task.GetCallable;
import com.shuaqiu.common.util.ViewUtil;
import com.shuaqiu.yuanyuanxibo.API.User;
import com.shuaqiu.yuanyuanxibo.Defs;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.StateKeeper;

public class UserInfoActivity extends Activity implements Callback<String> {
    static final String TAG = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_user_info);

        String accessToken = StateKeeper.accessToken.getAccessToken();
        String username = extractUsername();

        Bundle param = new Bundle();
        param.putString("access_token", accessToken);
        param.putString("screen_name", username);

        DeferredManager.when(new GetCallable(User.SHOW, param)).then(this);
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
    public void apply(String result) {
        JSONObject data = null;
        try {
            data = new JSONObject(result);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            return;
        }

        ViewUtil.setImage(findViewById(R.id.profile_image), ImageType.PROFILE,
                data.optString("profile_image_url", null));
        ViewUtil.setText(findViewById(R.id.user_name),
                data.optString("screen_name"));
        ViewUtil.setText(findViewById(R.id.location),
                data.optString("location"));
        ViewUtil.setText(findViewById(R.id.description),
                data.optString("description"));
    }
}
