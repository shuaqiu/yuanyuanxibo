package com.shuaqiu.yuanyuanxibo.status;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.shuaqiu.yuanyuanxibo.CursorBinderAdpater;
import com.shuaqiu.yuanyuanxibo.Defs;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.content.CursorLoaderCallbacks;
import com.shuaqiu.yuanyuanxibo.content.DatabaseHelper;

/**
 * @author shuaqiu Apr 27, 2013
 */
public class StatusListFragment extends ListFragment {

    private static final String TAG = "statuslist";

    String table = DatabaseHelper.Status.TABLE_NAME;
    String[] columns = new String[] { DatabaseHelper.Status.ID,
            DatabaseHelper.Status.CONTENT, DatabaseHelper.Status.READED };
    String orderBy = DatabaseHelper.Status.ID + " desc";

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
                adapter, table, columns, orderBy);
        getLoaderManager().initLoader(0, null, loadCallback);

        Intent service = new Intent(context, StatusService.class);
        context.startService(service);

        receiveBroadcast();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "click at " + position);

        Intent intent = new Intent(getActivity(), StatusActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    private void receiveBroadcast() {
        NewStatusReceiver receiver = new NewStatusReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Defs.NEW_STATUS);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                receiver, filter);
    }
}
