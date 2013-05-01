package com.shuaqiu.yuanyuanxibo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.webkit.WebView;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        WebView webView = (WebView) findViewById(R.id.authorize);
        webView.loadUrl(WeiboConstants.API + "oauth2/authorize?client_id="
                + WeiboConstants.CLIENT_ID + "&redirect_uri="
                + WeiboConstants.REDIRECT_URI + "&display=mobile");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

}
