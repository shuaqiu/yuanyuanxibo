/**
 * 
 */
package com.shuaqiu.common.task;

import android.os.Bundle;

import com.shuaqiu.common.util.HttpUtil;

public class GetCallable extends HttpCallable {

    public GetCallable(String url, Bundle param) {
        super(url, param);
    }

    @Override
    protected String httpCall() {
        return HttpUtil.httpGet(mUrl, mParam);
    }
}