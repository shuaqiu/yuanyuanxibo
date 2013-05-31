/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.shuaqiu.common.task.Function;
import com.shuaqiu.yuanyuanxibo.HttpCursor;
import com.shuaqiu.yuanyuanxibo.HttpCursor.CursorPair;
import com.shuaqiu.yuanyuanxibo.HttpCursorKeeper;

/**
 * @author shuaqiu 2013-5-31
 * 
 */
public class CursorKeepFunction implements Function<JSONObject, Boolean> {

    private Context mContext;
    private HttpCursor mHttpCursor;

    /**
     * @param context
     * @param httpCursor
     */
    public CursorKeepFunction(Context context, HttpCursor httpCursor) {
        mContext = context;
        mHttpCursor = httpCursor;
    }

    @Override
    public Boolean apply(JSONObject param) {
        saveHttpCursor(param);
        return true;
    }

    private void saveHttpCursor(JSONObject data) {
        long min = getMin(data);
        long max = getMax(data);
        mHttpCursor.prepend(new CursorPair(min, max));
        HttpCursorKeeper.save(mContext, mHttpCursor);
    }

    private long getMin(JSONObject data) {
        return data.optLong("next_cursor");
    }

    private long getMax(JSONObject data) {
        long max = data.optLong("previous_cursor");
        if (max > 0) {
            return max;
        }
        JSONArray statuses = data.optJSONArray("statuses");
        if (statuses.length() == 0) {
            return max;
        }
        JSONObject status = statuses.optJSONObject(0);
        long id = status.optLong("id");
        return id;
    }

}
