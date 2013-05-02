package com.shuaqiu.yuanyuanxibo;

import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialog;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.util.Utility;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
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

public class LoginFragment extends Fragment {
	private static final  String TAG = "login";

	private static final String AUTH_LISTENER = "auth_listener";
	
	private WebView mWebView;
	private ProgressDialog mSpinner;
	private WeiboAuthListener mListener;
	
	public LoginFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login, container, false);

		mListener = (WeiboAuthListener) getArguments().get(AUTH_LISTENER);

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
		mWebView.loadUrl(WeiboConstants.API + "oauth2/authorize?client_id="
				+ WeiboConstants.CLIENT_ID + "&redirect_uri="
				+ WeiboConstants.REDIRECT_URI + "&display=mobile");
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
            mListener.onError(new WeiboDialogError(description, errorCode,
                    failingUrl));
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "onPageStarted URL: " + url);
            if (url.startsWith(Weibo.redirecturl)) {
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
	        Bundle values = Utility.parseUrl(url);

	        String error = values.getString("error");
	        String error_code = values.getString("error_code");

	        if (error == null && error_code == null) {
	            mListener.onComplete(values);
	        } else if (error.equals("access_denied")) {
	            // 用户或授权服务器拒绝授予数据访问权限
	            mListener.onCancel();
	        } else {
	            if (error_code == null) {
	                mListener.onWeiboException(new WeiboException(error, 0));
	            } else {
	                mListener.onWeiboException(new WeiboException(error, Integer
	                        .parseInt(error_code)));
	            }

	        }
	    }
}
