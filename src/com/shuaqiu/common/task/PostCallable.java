/**
 * 
 */
package com.shuaqiu.common.task;

import android.os.Bundle;

import com.shuaqiu.common.util.HttpUtil;

/**
 * 簡單調用HTTP POST 方法的Callable
 * 
 * @author shuaqiu Jun 9, 2013
 */
public class PostCallable extends HttpCallable {

    public PostCallable(String url, Bundle param) {
        super(url, param);
    }

    @Override
    protected String httpCall() {
        return HttpUtil.httpPost(mUrl, mParam);
    }

}