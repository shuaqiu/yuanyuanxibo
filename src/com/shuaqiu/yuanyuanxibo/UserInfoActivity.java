package com.shuaqiu.yuanyuanxibo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;

public class UserInfoActivity extends Activity {

    private Uri SCHEME = Uri.parse(Defs.USER_SCHEME);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_user_info);

        String uid = extractUid();

        findViewById(R.id.profile_image);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_user_info, menu);
        return true;
    }

    private String extractUid() {
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri == null) {
            return null;
        }
        if (uri.getScheme().equals(SCHEME.getScheme())) {
            return uri.getQueryParameter(Defs.USER_UID);
        }
        return null;
    }
}
