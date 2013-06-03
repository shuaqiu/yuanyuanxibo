/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.comment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListAdapter;

import com.shuaqiu.common.task.AsyncHttpGetTask;
import com.shuaqiu.common.task.AsyncTaskListener;
import com.shuaqiu.common.widget.SimpleBindAdapter;
import com.shuaqiu.yuanyuanxibo.API;
import com.shuaqiu.yuanyuanxibo.Defs;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.StateKeeper;

/**
 * @author shuaqiu 2013-5-1
 */
public class CommentListFragment extends ListFragment implements
        AsyncTaskListener<JSONObject> {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentActivity context = getActivity();

        Intent intent = context.getIntent();
        String action = intent.getAction();

        Bundle params = new Bundle();
        String accessToken = StateKeeper.accessToken.getAccessToken();
        params.putString("access_token", accessToken);

        if (action == null || action.equals(Defs.Action.USER_COMMENT)) {
            new AsyncHttpGetTask(params, this).execute(API.Comment.TIMELINE);
        } else if (action.equals(Defs.Action.STATUS_COMMENT)) {
            // long statusId = intent.getLongExtra("id", 0);
            // params.putLong("id", statusId);
            params.putAll(intent.getExtras());
            new AsyncHttpGetTask(params, this).execute(API.Comment.SHOW);
        }

    }

    @Override
    public void onPostExecute(JSONObject data) {
        if (data == null) {
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
