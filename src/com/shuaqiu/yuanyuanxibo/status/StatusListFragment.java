package com.shuaqiu.yuanyuanxibo.status;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.shuaqiu.common.promiss.Callback;
import com.shuaqiu.common.promiss.DeferredManager;
import com.shuaqiu.common.widget.SimpleCursorAdapter;
import com.shuaqiu.common.widget.ViewBinder;
import com.shuaqiu.yuanyuanxibo.Actions.Status;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.Refreshable;
import com.shuaqiu.yuanyuanxibo.content.CursorLoaderCallbacks;
import com.shuaqiu.yuanyuanxibo.content.StatusHelper;

/**
 * @author shuaqiu Apr 27, 2013
 */
public class StatusListFragment extends ListFragment implements Refreshable {

    private static final String TAG = "StatusListFragment";

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showFriendStatuses();
    }

    /**
     * 
     */
    private void showFriendStatuses() {
        Context context = getActivity();
        StatusBinder<Bundle> binder = new BundleStatusBinder(context,
                StatusBinder.Type.LIST);
        SimpleCursorAdapter<Bundle> adapter = new StatusCursorAdapter(context,
                R.layout.listview_status, binder);
        setListAdapter(adapter);

        CursorLoaderCallbacks loadCallback = new CursorLoaderCallbacks(context,
                adapter, StatusHelper.TABLE, StatusHelper.names(),
                StatusHelper.ORDER_BY);
        getLoaderManager().initLoader(0, null, loadCallback);

        startService(context);
        receiveBroadcast();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "click at " + position);

        Intent intent = new Intent(getActivity(), StatusActivity.class);
        intent.putExtra("position", position);

        long maxId = getListAdapter().getItemId(0);
        Log.d(TAG, "maxId " + maxId);
        if (maxId == 0) {
            maxId = -1;
        }
        intent.putExtra("maxId", maxId);

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
        filter.addAction(Status.NEW_RECEIVED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                receiver, filter);
    }

    @Override
    public void refresh() {
        Runnable runner = new StatusDownloader(getActivity(), false);
        DeferredManager.when(runner).then(new Callback<Void>() {
            @Override
            public void apply(Void result) {
                Loader<Object> loader = getLoaderManager().getLoader(0);
                loader.forceLoad();
            }
        });
    }

    /**
     * @author shuaqiu 2013-6-2
     */
    private final class StatusCursorAdapter extends SimpleCursorAdapter<Bundle> {
        /**
         * @param context
         * @param resource
         * @param binder
         */
        private StatusCursorAdapter(Context context, int resource,
                ViewBinder<Bundle> binder) {
            super(context, resource, binder);
        }

        @Override
        protected Bundle toData(Cursor cursor) {
            return StatusHelper.toBundle(cursor);
        }
    }
}
