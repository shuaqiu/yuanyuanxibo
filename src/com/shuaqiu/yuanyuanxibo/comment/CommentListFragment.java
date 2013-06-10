/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.comment;

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
import com.shuaqiu.yuanyuanxibo.Actions.Comment;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.StateKeeper;

/**
 * @author shuaqiu 2013-5-1
 */
public class CommentListFragment extends ListFragment implements
        Callback<String> {

    private static final String TAG = "CommentListFragment";

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
        if (action == null || action.equals(Comment.FOR_USER)) {
            // 顯示用戶的評論列表
            url = API.Comment.TIMELINE;
        } else if (action.equals(Comment.FOR_STATUS)) {
            // 顯示微博的評論列表
            url = API.Comment.SHOW;

            // intent 中包含微博的id 值
            long statusId = intent.getLongExtra("id", 0);
            param.putLong("id", statusId);
            // params.putAll(intent.getExtras());
        }

        DeferredManager.when(new GetCallable(url, param)).then(this);
    }

    @Override
    public void apply(String result) {
        JSONObject data = null;
        try {
            data = new JSONObject(result);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            return;
        }

        JSONArray comments = data.optJSONArray("comments");

        FragmentActivity context = getActivity();

        CommentBinder binder = new CommentBinder(context,
                CommentBinder.Type.STATUS);
        ListAdapter adapter = new SimpleBindAdapter<JSONObject>(context,
                toList(comments), R.layout.listview_comment, binder);

        setListAdapter(adapter);
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
