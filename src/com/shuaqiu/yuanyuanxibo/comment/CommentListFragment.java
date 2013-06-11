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
import com.shuaqiu.yuanyuanxibo.comment.CommentBinder.Type;

/**
 * @author shuaqiu 2013-5-1
 */
public class CommentListFragment extends ListFragment implements
        Callback<String> {

    private static final String TAG = "CommentListFragment";

    private static final int OPER_FOR_USER = 1;
    private static final int OPER_FOR_STATUS = 2;

    private int oper = OPER_FOR_USER;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initOper();
        DeferredManager.when(buildCallable()).then(this);
    }

    private void initOper() {
        Intent intent = getActivity().getIntent();
        String action = intent.getAction();

        if (action != null && action.equals(Comment.FOR_STATUS)) {
            // 顯示微博的評論列表
            oper = OPER_FOR_STATUS;
        } else {
            // 顯示用戶的評論列表
            // action.equals(Comment.FOR_USER) ||
            // action.equals(Intent.ACTION_MAIN)
            oper = OPER_FOR_USER;
        }
    }

    private GetCallable buildCallable() {
        Bundle param = new Bundle(2);
        String accessToken = StateKeeper.accessToken.getAccessToken();
        param.putString("access_token", accessToken);

        String url = null;
        if (oper == OPER_FOR_USER) {
            // 最新的提到登錄用戶的微博列表
            url = API.Comment.TIMELINE;
        } else if (oper == OPER_FOR_STATUS) {
            // 獲取指定微博的轉發微博列表
            url = API.Comment.SHOW;

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
        JSONArray comments = getComments(result);

        FragmentActivity context = getActivity();

        CommentBinder binder = new CommentBinder(context, getListType());
        ListAdapter adapter = new SimpleBindAdapter<JSONObject>(context,
                toList(comments), R.layout.listview_comment, binder);

        setListAdapter(adapter);
    }

    private Type getListType() {
        if (oper == OPER_FOR_STATUS) {
            return CommentBinder.Type.STATUS;
        }
        return CommentBinder.Type.USER;
    }

    private JSONArray getComments(String result) {
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

        return data.optJSONArray("comments");
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
