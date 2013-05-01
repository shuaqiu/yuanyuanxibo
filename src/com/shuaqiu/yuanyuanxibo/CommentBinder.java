/**
 * 
 */
package com.shuaqiu.yuanyuanxibo;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;

/**
 * @author shuaqiu 2013-5-1
 * 
 */
public class CommentBinder implements ViewBinder {
    private JSONArray mData;

    public CommentBinder(Context context) {
    }

    @Override
    public void bindView(int position, View view) {
        final JSONObject comment = mData.optJSONObject(position);
        if (comment == null) {
            return;
        }

    }

    @Override
    public JSONArray getDataItems() {
        return mData;
    }

}
