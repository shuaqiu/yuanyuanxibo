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

    private static final int OPER_MENTIONS = 1;
    private static final int OPER_REPOSTS = 2;

    private int oper = OPER_MENTIONS;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initOper();
        DeferredManager.when(buildCallable()).then(this);
    }

    private void initOper() {
        Intent intent = getActivity().getIntent();
        String action = intent.getAction();

        if (action != null && action.equals(Status.REPOST_LIST)) {
            // 獲取指定微博的轉發微博列表
            oper = OPER_REPOSTS;
        } else {
            // 最新的提到登錄用戶的微博列表
            // action.equals(Status.AT_ME_LIST) ||
            // action.equals(Intent.ACTION_MAIN)
            oper = OPER_MENTIONS;
        }
    }

    private GetCallable buildCallable() {
        Bundle param = new Bundle(2);
        String accessToken = StateKeeper.accessToken.getAccessToken();
        param.putString("access_token", accessToken);

        String url = null;
        if (oper == OPER_MENTIONS) {
            // 最新的提到登錄用戶的微博列表
            url = API.Status.MENTIONS;
        } else if (oper == OPER_REPOSTS) {
            // 獲取指定微博的轉發微博列表
            url = API.Status.REPOST_TIMELINE;

            Intent intent = getActivity().getIntent();
            // intent 中包含微博的id 值
            long statusId = intent.getLongExtra("id", 0);
            param.putLong("id", statusId);
            // params.putAll(intent.getExtras());
        }

        return new GetCallable(url, param);
    }

    @Override
    public void apply(String result) {
        JSONArray statuses = getStatuses(result);
        Log.d(TAG, "length: " + statuses.length());

        FragmentActivity context = getActivity();

        JsonStatusBinder binder = new JsonStatusBinder(context, getListType());
        ListAdapter adapter = new SimpleBindAdapter<JSONObject>(context,
                toList(statuses), R.layout.listview_status, binder);

        setListAdapter(adapter);
    }

    /**
     * @return
     */
    private Type getListType() {
        if (oper == OPER_REPOSTS) {
            return Type.REPOST;
        }
        return Type.LIST;
    }

    /**
     * 根據結果獲取微博列表
     * 
     * @param result
     * @return
     */
    private JSONArray getStatuses(String result) {
        if (result == null) {
            // 這裏返回一個空的array, 避免界面上的進度條一直在轉
            return new JSONArray();
        }
        JSONObject data = null;
        try {
            data = new JSONObject(result);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            return new JSONArray();
        }

        return data.optJSONArray(getStatusesKey());
    }

    /**
     * 獲取返回的數據中微博的key, 兩種請求的key 是不一樣的
     * 
     * @return
     */
    private String getStatusesKey() {
        if (oper == OPER_REPOSTS) {
            return "reposts";
        }
        return "statuses";
    }

    private List<JSONObject> toList(JSONArray arr) {
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
