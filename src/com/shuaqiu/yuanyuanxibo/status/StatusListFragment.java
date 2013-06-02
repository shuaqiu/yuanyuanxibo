package com.shuaqiu.yuanyuanxibo.status;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.shuaqiu.common.function.Function;
import com.shuaqiu.common.task.Promise;
import com.shuaqiu.yuanyuanxibo.Defs;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.RefreshableListFragment;
import com.shuaqiu.yuanyuanxibo.ViewBinder;
import com.shuaqiu.yuanyuanxibo.content.CursorLoaderCallbacks;
import com.shuaqiu.yuanyuanxibo.content.StatusHelper;

/**
 * @author shuaqiu Apr 27, 2013
 */
public class StatusListFragment extends RefreshableListFragment {

    private static final String TAG = "StatusListFragment";

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = getActivity();
        StatusBinder<Bundle> statusBinder = new BundleStatusBinder(context,
                StatusBinder.Type.LIST);
        // CursorBinderAdpater adapter = new CursorBinderAdpater(context,
        // R.layout.listview_status, statusBinder,
        // CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        SimpleCursorAdapter<Bundle> adapter = new StatusCursorAdapter(context,
                R.layout.listview_status, statusBinder);
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

    /**
     * @author shuaqiu 2013-5-31
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
     */
    private final class ReloadFunction implements Function<Void, Void> {
        @Override
        public Void apply(Void params) {
            Loader<Object> loader = getLoaderManager().getLoader(0);
            loader.forceLoad();
            return null;
        }
    }
}
