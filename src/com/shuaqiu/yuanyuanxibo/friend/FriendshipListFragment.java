/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.friend;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.shuaqiu.common.ImageType;
import com.shuaqiu.common.promiss.Callback;
import com.shuaqiu.common.promiss.DeferredManager;
import com.shuaqiu.common.util.ViewUtil;
import com.shuaqiu.common.widget.SimpleBindAdapter;
import com.shuaqiu.common.widget.ViewBinder;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.content.FriendshipHelper;
import com.shuaqiu.yuanyuanxibo.content.FriendshipHelper.Column;
import com.shuaqiu.yuanyuanxibo.content.QueryCallable;

/**
 * @author shuaqiu 2013-6-14
 */
public class FriendshipListFragment extends ListFragment implements
        OnScrollListener, Callback<Cursor> {

    private static final String TAG = "FriendshipListFragment";

    private EditText mSelected;

    private FriendshipHelper mStatusHelper;
    private List<Bundle> mFriends;

    private boolean isLoading;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initListView();

        mStatusHelper = new FriendshipHelper(getActivity());
        mStatusHelper.openForRead();

        String limit = "20";
        doQuery(limit);
    }

    private void initListView() {
        mFriends = new ArrayList<Bundle>();

        FragmentActivity context = getActivity();
        ViewBinder<Bundle> binder = new FriendshipBinder();

        BaseAdapter adapter = new SimpleBindAdapter<Bundle>(context, mFriends,
                R.layout.listview_friendship, binder);
        setListAdapter(adapter);

        getListView().setOnScrollListener(this);
    }

    private void doQuery(String limit) {
        QueryCallable query = new QueryCallable(mStatusHelper, null, null,
                limit);
        DeferredManager.when(query).then(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mStatusHelper.close();
        mFriends = null;
    }

    @Override
    public void apply(Cursor cursor) {
        mFriends.addAll(FriendshipHelper.toBundles(cursor));
        ((BaseAdapter) getListAdapter()).notifyDataSetChanged();

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "click at " + position + " and on " + v + " id " + id);

        // 這裡的v, 是ListView 中的item 的view, 在這裡就是listview_friendship
        // 中的最頂層的LinearLayout
        ViewHolder holder = ViewHolder.from(v);

        String name = holder.mScreenName.getText().toString();

        Editable text = getSelectedFriendsView().getText();
        if (holder.mSelected.isChecked()) {
            // 如果之前已經選擇, 則進行刪除處理
            holder.mSelected.setChecked(false);

            String atName = "@" + name + " ";
            int index = text.toString().indexOf(atName);
            if (index != -1) {
                text.replace(index, index + atName.length(), "");
            }
        } else {
            // 如果之前沒有選擇, 則增加
            holder.mSelected.setChecked(true);

            text.append(name).append(" @");
        }
    }

    private EditText getSelectedFriendsView() {
        if (mSelected == null) {
            FragmentActivity activity = getActivity();
            mSelected = (EditText) activity.findViewById(R.id.selected_friends);
        }
        return mSelected;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        boolean isAtBottom = firstVisibleItem + visibleItemCount >= totalItemCount;
        if (!isLoading && firstVisibleItem > 0 && isAtBottom) {
            Log.d(TAG, "load more after " + totalItemCount);

            isLoading = true;
            doQuery(totalItemCount + ", 20");
        } else {
            isLoading = false;
        }
    }

    private static final class FriendshipBinder implements ViewBinder<Bundle> {
        @Override
        public void bindView(View view, Bundle data) {
            ViewHolder holder = ViewHolder.from(view);

            // 由於view 是循環使用的, 所以這裡要重新設置一下這裡的選中狀態
            holder.mSelected.setChecked(false);

            String url = data.getString(Column.profile_image_url.name());
            ViewUtil.setImage(holder.mProfileImage, ImageType.PROFILE, url);

            String name = data.getString(Column.screen_name.name());
            ViewUtil.setText(holder.mScreenName, name);
        }
    }

    private static final class ViewHolder {
        private CheckBox mSelected;
        private View mProfileImage;
        private TextView mScreenName;

        static ViewHolder from(View v) {
            Object tag = v.getTag();
            if (tag instanceof ViewHolder) {
                return (ViewHolder) tag;
            }

            ViewHolder holder = new ViewHolder();
            v.setTag(holder);

            holder.mSelected = (CheckBox) v.findViewById(R.id.selected);
            holder.mProfileImage = v.findViewById(R.id.profile_image);
            holder.mScreenName = (TextView) v.findViewById(R.id.screen_name);
            return holder;
        }
    }
}
