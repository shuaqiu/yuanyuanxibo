package com.shuaqiu.yuanyuanxibo.status;

import java.util.List;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.shuaqiu.common.promiss.Callback;
import com.shuaqiu.common.promiss.DeferredManager;
import com.shuaqiu.common.widget.AbsPaginationListFragment;
import com.shuaqiu.common.widget.ViewBinder;
import com.shuaqiu.yuanyuanxibo.Defs;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.Refreshable;
import com.shuaqiu.yuanyuanxibo.content.AbsObjectHelper;
import com.shuaqiu.yuanyuanxibo.content.QueryCallable.Builder;
import com.shuaqiu.yuanyuanxibo.content.StatusHelper;

/**
 * @author shuaqiu Apr 27, 2013
 */
public class StatusListFragment extends AbsPaginationListFragment implements
        Refreshable, Callback<Void> {

    private static final String TAG = "StatusListFragment";

    @Override
    protected AbsObjectHelper initHelper() {
        return new StatusHelper(getActivity());
    }

    @Override
    protected ViewBinder<Bundle> initViewBinder() {
        return new BundleStatusBinder(getActivity(), StatusBinder.Type.LIST);
    }

    @Override
    protected int getLayout() {
        return R.layout.listview_status;
    }

    @Override
    protected void configQuery(Builder builder) {
    }

    @Override
    protected List<Bundle> toList(Cursor cursor) {
        return StatusHelper.toBundles(cursor);
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

    @Override
    public void refresh() {
        StatusDownloader downloader = new StatusDownloader(getActivity(), false);
        DeferredManager.when(downloader).then(this);
    }

    @Override
    public void apply(Void result) {
        super.refresh();

        cancelNotification();
    }

    protected void cancelNotification() {
        NotificationManager manager = (NotificationManager) getActivity()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(Defs.NOTIFICATION_ID_NEW_STATUS);
    }
}
