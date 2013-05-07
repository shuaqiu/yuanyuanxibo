package com.shuaqiu.yuanyuanxibo.status;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.shuaqiu.common.task.AsyncHttpGetTask;
import com.shuaqiu.common.task.AsyncTaskListener;
import com.shuaqiu.yuanyuanxibo.API.Status;
import com.shuaqiu.yuanyuanxibo.CursorBinderAdpater;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.StateKeeper;
import com.shuaqiu.yuanyuanxibo.content.CursorLoaderCallbacks;
import com.shuaqiu.yuanyuanxibo.content.DBOpenHelper;

/**
 * @author shuaqiu Apr 27, 2013
 */
public class StatusListFragment extends ListFragment implements
        AsyncTaskListener<JSONObject> {

    private static final String TAG = "statuslist";

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = getActivity();
        StatusBinder statusBinder = new StatusBinder(context,
                StatusBinder.Type.LIST);
        CursorBinderAdpater adapter = new CursorBinderAdpater(context,
                R.layout.listview_status, statusBinder,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        setListAdapter(adapter);

        CursorLoaderCallbacks loadCallback = new CursorLoaderCallbacks(context,
                adapter, "t_status", new String[] { "id", "content", "readed" });
        getLoaderManager().initLoader(0, null, loadCallback);
    }

    public void refreshData() {
        Bundle params = new Bundle();
        String accessToken = StateKeeper.accessToken.getAccessToken();
        params.putString("access_token", accessToken);

        new AsyncHttpGetTask(params, this).execute(Status.FRIEND_TIMELINE);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "click at " + position);

        Intent intent = new Intent(getActivity(), StatusActivity.class);
        intent.putExtra("data", getListAdapter().getItem(position).toString());
        intent.putExtra("position", position);
        startActivity(intent);
    }

    @Override
    public void onPostExecute(JSONObject data) {
        if (data == null) {
            return;
        }

        JSONArray statuses = data.optJSONArray("statuses");

        DBOpenHelper dbHelper = new DBOpenHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int i = 0; i < statuses.length(); i++) {
            JSONObject status = statuses.optJSONObject(i);
            long id = status.optLong("id");
            ContentValues values = new ContentValues(3);
            values.put("id", id);
            values.put("content", status.toString());
            values.put("readed", 0);
            db.replace("t_status", null, values);
        }
        db.close();

        // ListAdapter adapter = new StatusListAdapter(getActivity(),
        // R.layout.listview_status, new StatusBinder(getActivity(),
        // StatusBinder.Type.LIST), statuses);
        // setListAdapter(adapter);
    }
}
