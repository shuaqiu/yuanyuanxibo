/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.comment;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;

import com.shuaqiu.common.task.AsyncTaskListener;
import com.shuaqiu.yuanyuanxibo.StateKeeper;

/**
 * @author shuaqiu 2013-5-1
 */
public class CommentListFragment extends ListFragment implements
        AsyncTaskListener<JSONObject> {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle params = new Bundle();
        String accessToken = StateKeeper.accessToken.getAccessToken();
        params.putString("access_token", accessToken);

        // new AsyncHttpGetTask(params, this).execute(Comment.TIMELINE);

    }

    @Override
    public void onPostExecute(JSONObject data) {
        if (data == null) {
            return;
        }

        // JSONArray comments = data.optJSONArray("comments");

        // ListAdapter adapter = new StatusListAdapter(getActivity(),
        // R.layout.listview_comment, new CommentBinder(getActivity()),
        // comments);

        // setListAdapter(adapter);
    }

}
