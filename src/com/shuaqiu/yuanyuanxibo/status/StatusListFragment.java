package com.shuaqiu.yuanyuanxibo.status;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.shuaqiu.common.function.Function;
import com.shuaqiu.common.task.Promise;
import com.shuaqiu.yuanyuanxibo.CursorBinderAdpater;
import com.shuaqiu.yuanyuanxibo.Defs;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.RefreshableListFragment;
import com.shuaqiu.yuanyuanxibo.content.CursorLoaderCallbacks;
import com.shuaqiu.yuanyuanxibo.content.DatabaseHelper;

/**
 * @author shuaqiu Apr 27, 2013
 */
public class StatusListFragment extends RefreshableListFragment {

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

        startService(context);

        receiveBroadcast();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "click at " + position);

        Intent intent = new Intent(getActivity(), StatusActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    private void startService(Context context) {
        if (StatusService.isRunning) {
            Log.d(TAG, "Service is running");
            return;
        }

        Intent service = new Intent(context, StatusService.class);
        context.startService(service);
    }

    private void receiveBroadcast() {
        NewStatusReceiver receiver = new NewStatusReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Defs.NEW_STATUS);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                receiver, filter);
    }

    @Override
    public void refresh() {
        Promise<Void, Void> promise = new Promise<Void, Void>(
                new DownloadFunction());
        promise.then(new ReloadFunction());
        promise.execute();
    }

    /**
     * @author shuaqiu 2013-5-31
     * 
     */
    private final class DownloadFunction implements Function<Void, Void> {
        @Override
        public Void apply(Void params) {
            StatusDownloader downloader = new StatusDownloader(getActivity());
            downloader.setBroadcast(false);
            downloader.run();
            return null;
        }
    }

    /**
     * @author shuaqiu 2013-5-31
     * 
     */
    private final class ReloadFunction implements Function<Void, Void> {
        @Override
        public Void apply(Void params) {
            getLoaderManager().getLoader(0).takeContentChanged();
            return null;
        }
    }
}
