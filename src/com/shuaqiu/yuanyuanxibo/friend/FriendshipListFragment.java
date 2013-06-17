/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.friend;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ListView;

import com.shuaqiu.common.util.ViewUtil;
import com.shuaqiu.common.widget.SimpleCursorAdapter;
import com.shuaqiu.common.widget.ViewBinder;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.content.CursorLoaderCallbacks;
import com.shuaqiu.yuanyuanxibo.content.FriendshipHelper;
import com.shuaqiu.yuanyuanxibo.content.FriendshipHelper.Column;

/**
 * @author shuaqiu 2013-6-14
 */
public class FriendshipListFragment extends ListFragment implements
        OnScrollListener {

    private static final String TAG = "FriendshipListFragment";

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = getActivity();
        ViewBinder<Bundle> binder = new ViewBinder<Bundle>() {
            @Override
            public void bindView(View view, Bundle data) {
                View v = view.findViewById(R.id.screen_name);
                String name = data.getString(Column.screen_name.name());
                ViewUtil.setText(v, name);
            }
        };
        SimpleCursorAdapter<Bundle> adapter = new FriendshipCursorAdapter(
                context, R.layout.listview_friendship, binder);
        setListAdapter(adapter);

        CursorLoaderCallbacks loadCallback = new CursorLoaderCallbacks(context,
                adapter, FriendshipHelper.TABLE, FriendshipHelper.names(),
                FriendshipHelper.ORDER_BY, "20");
        getLoaderManager().initLoader(0, null, loadCallback);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "click at " + position);

        FragmentActivity activity = getActivity();
        EditText selectedFriends = (EditText) activity
                .findViewById(R.id.selected_friends);

        Cursor item = (Cursor) getListAdapter().getItem(position);
        String name = item.getString(Column.screen_name.ordinal());

        selectedFriends.getText().append(name).append(" @");
    }

    /**
     * @author shuaqiu 2013-6-2
     */
    private final class FriendshipCursorAdapter extends
            SimpleCursorAdapter<Bundle> {
        /**
         * @param context
         * @param resource
         * @param binder
         */
        private FriendshipCursorAdapter(Context context, int resource,
                ViewBinder<Bundle> binder) {
            super(context, resource, binder);
        }

        @Override
        protected Bundle toData(Cursor cursor) {
            return FriendshipHelper.toBundle(cursor);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
    }
}
