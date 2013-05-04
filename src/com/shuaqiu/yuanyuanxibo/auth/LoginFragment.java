package com.shuaqiu.yuanyuanxibo.auth;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.shuaqiu.common.HttpUtil;
import com.shuaqiu.common.task.AcessTokenTask;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.WeiboConstants;

public class LoginFragment extends Fragment {

    private static final String TAG = "login";

    private WebView mWebView;
    private ProgressDialog mSpinner;
    private AuthListener mAuthListener;

    /**
     * @param authListener
     */
    public void setAuthListener(AuthListener authListener) {
        mAuthListener = authListener;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mSpinner = new ProgressDialog(getActivity());
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage("Loading...");
        mSpinner.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                    KeyEvent event) {
                try {
                    mSpinner.dismiss();
                    if (null != mWebView) {
                        mWebView.stopLoading();
                        mWebView.destroy();
                    }
                } catch (Exception e) {
                }
                return false;
            }
        });

        mWebView = (WebView) view.findViewById(R.id.authorize);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WeiboWebViewClient());

        Bundle args = new Bundle();
        args.putString("client_id", WeiboConstants.CLIENT_ID);
        args.putString("redirect_uri", WeiboConstants.REDIRECT_URI);
        args.putString("display", "mobile");
        mWebView.loadUrl(WeiboConstants.API + WeiboConstants.OAUTH2_AUTHORIZE
                + "?" + HttpUtil.param(args));
        return view;
    }

    private class WeiboWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "Redirect URL: " + url);
            if (url.startsWith("sms:")) { // 针对webview里的短信注册流程，需要在此单独处理sms协议
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.putExtra("address", url.replace("sms:", ""));
                sendIntent.setType("vnd.android-dir/mms-sms");
                getActivity().startActivity(sendIntent);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            // onError(new WeiboDialogError(description, errorCode,
            // failingUrl));
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "onPageStarted URL: " + url);
            if (url.startsWith(WeiboConstants.REDIRECT_URI)) {
                handleRedirectUrl(view, url);
                view.stopLoading();
                return;
            }
            super.onPageStarted(view, url, favicon);
            mSpinner.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d(TAG, "onPageFinished URL: " + url);
            super.onPageFinished(view, url);
            if (mSpinner.isShowing()) {
                mSpinner.dismiss();
            }
            mWebView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                SslError error) {
            handler.proceed();
        }

    }

    private void handleRedirectUrl(WebView view, String url) {
        Bundle values = HttpUtil.decodeUrl(HttpUtil.parseUrl(url));

        String error = values.getString("error");
        String error_code = values.getString("error_code");

        if (error == null && error_code == null) {
            values.putString("client_id", WeiboConstants.CLIENT_ID);
            values.putString("client_secret", WeiboConstants.CLIENT_SECRET);
            values.putString("grant_type", "authorization_code");
            values.putString("redirect_uri", WeiboConstants.REDIRECT_URI);

            AcessTokenTask task = new AcessTokenTask(values, mAuthListener);
            task.execute(WeiboConstants.API
                    + WeiboConstants.OAUTH2_ACCESS_TOKEN);

        } else if (error.equals("access_denied")) {
            // 用户或授权服务器拒绝授予数据访问权限
            onCancel();
        } else {
            if (error_code == null) {
                onException(error, 0);
            } else {
                onException(error, Integer.parseInt(error_code));
            }
        }
    }

    /**
     * @param weiboException
     */
    private void onException(String error, int errorCode) {
    }

    /**
     * 
     */
    private void onCancel() {
    }

    private void onError() {

    }
}
