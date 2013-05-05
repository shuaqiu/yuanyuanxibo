package com.shuaqiu.yuanyuanxibo.status;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.shuaqiu.common.task.AsyncHttpGetTask;
import com.shuaqiu.common.task.AsyncTaskListener;
import com.shuaqiu.yuanyuanxibo.API.Status;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.StateKeeper;

/**
 * @author shuaqiu Apr 27, 2013
 */
public class StatusListFragment extends ListFragment implements
        AsyncTaskListener<JSONObject> {

    private static final String TAG = "statuslist";

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle params = new Bundle();
        String accessToken = StateKeeper.accessToken.getAccessToken();
        params.putString("access_token", accessToken);

        new AsyncHttpGetTask(params, this).execute(Status.FRIEND_TIMELINE);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "click at " + position);

        Intent intent = new Intent(getActivity(), StatusActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    @Override
    public void onPostExecute(JSONObject data) {
        if (data == null) {
            return;
        }

        JSONArray statuses = data.optJSONArray("statuses");
        ListAdapter adapter = new StatusListAdapter(getActivity(),
                R.layout.listview_status, new StatusBinder(getActivity()),
                statuses);
        setListAdapter(adapter);

        ListView listView = getListView();
        listView.focusableViewAvailable(listView);
        listView.requestFocusFromTouch();
    }
}
