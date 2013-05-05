/**
 * 
 */
package com.shuaqiu.yuanyuanxibo;

import org.json.JSONObject;

import android.view.View;

/**
 * @author shuaqiu 2013-5-1
 */
public interface ViewBinder {

    void bindView(View view, final JSONObject data);

}