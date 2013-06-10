package com.shuaqiu.yuanyuanxibo.status;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;

import com.shuaqiu.common.promiss.Callback;
import com.shuaqiu.common.promiss.DeferredManager;
import com.shuaqiu.common.task.GetCallable;
import com.shuaqiu.common.widget.SimpleBindAdapter;
import com.shuaqiu.yuanyuanxibo.API;
import com.shuaqiu.yuanyuanxibo.Actions.Status;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.StateKeeper;
import com.shuaqiu.yuanyuanxibo.status.StatusBinder.Type;

/**
 * @author shuaqiu Apr 27, 2013
 */
public class RetweetedStatusListFragment extends ListFragment implements
        Callback<String> {

    private static final String TAG = "RetweetedStatusListFragment";

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentActivity context = getActivity();

        Intent intent = context.getIntent();
        String action = intent.getAction();

        Bundle param = new Bundle(2);
        String accessToken = StateKeeper.accessToken.getAccessToken();
        param.putString("access_token", accessToken);

        String url = null;
        if (action == null || action.equals(Status.AT_ME_LIST)) {
            // 最新的提到登錄用戶的微博列表
            url = API.Status.MENTIONS;
        } else if (action.equals(Status.REPOST_LIST)) {
            // 獲取指定微博的轉發微博列表
            url = API.Status.REPOST_TIMELINE;

            // intent 中包含微博的id 值
            long statusId = intent.getLongExtra("id", 0);
            param.putLong("id", statusId);
            // params.putAll(intent.getExtras());
        }

        DeferredManager.when(new GetCallable(url, param)).then(this);
    }

    @Override
    public void apply(String result) {
        JSONArray statuses = getStatuses(result);
        Log.d(TAG, "length: " + statuses.length());

        FragmentActivity context = getActivity();

        JsonStatusBinder binder = new JsonStatusBinder(context, Type.REPOST);
        ListAdapter adapter = new SimpleBindAdapter<JSONObject>(context,
                toList(statuses), R.layout.listview_status, binder);

        setListAdapter(adapter);
    }

    /**
     * @param result
     * @return
     */
    private JSONArray getStatuses(String result) {
        if (result == null) {
            return new JSONArray();
        }
        JSONObject data = null;
        try {
            data = new JSONObject(result);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            return new JSONArray();
        }

        JSONArray statuses = data.optJSONArray("reposts");
        return statuses;
    }

    protected List<JSONObject> toList(JSONArray arr) {
        if (arr == null) {
            return null;
        }
        List<JSONObject> list = new ArrayList<JSONObject>(arr.length());
        for (int i = 0; i < arr.length(); i++) {
            list.add(arr.optJSONObject(i));
        }
        return list;
    }
}
