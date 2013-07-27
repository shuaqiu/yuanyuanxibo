/**
 * 
 */
package com.shuaqiu.common.widget;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;

import com.shuaqiu.common.promiss.Callback;
import com.shuaqiu.common.promiss.DeferredManager;
import com.shuaqiu.yuanyuanxibo.content.AbsObjectHelper;
import com.shuaqiu.yuanyuanxibo.content.QueryCallable;
import com.shuaqiu.yuanyuanxibo.content.QueryCallable.Builder;

/**
 * @author shuaqiu Jul 19, 2013
 */
public abstract class AbsPaginationListFragment extends ListFragment implements
        OnScrollListener {

    private static final String TAG = "AbsPaginationListFragment";

    protected AbsObjectHelper mHelper;
    protected int mPageSize = 20;
    protected List<Bundle> mDatas;
    protected QueryCallback mQueryCallback;

    private boolean isLoading;
    private int mMaxLoadedItem = 0;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initListView();

        mQueryCallback = new QueryCallback(this);

        mHelper = initHelper();
        mHelper.openForRead();

        doQuery();
    }

    /**
     * 初始化查詢對象
     * 
     * @return
     */
    protected abstract AbsObjectHelper initHelper();

    protected void initListView() {
        mDatas = new ArrayList<Bundle>();

        BaseAdapter adapter = initAdapter();
        setListAdapter(adapter);

        initScollListener();
    }

    protected void initScollListener() {
        getListView().setOnScrollListener(this);
    }

    /**
     * 初始化ListView 的adapter
     * 
     * @return
     */
    protected BaseAdapter initAdapter() {
        ViewBinder<Bundle> binder = initViewBinder();
        BaseAdapter adapter = new SimpleBindAdapter<Bundle>(getActivity(),
                mDatas, getLayout(), binder);
        return adapter;
    }

    /**
     * @return
     */
    protected abstract ViewBinder<Bundle> initViewBinder();

    /**
     * @return
     */
    protected abstract int getLayout();

    protected void doQuery() {
        doQuery(mPageSize + "");
    }

    protected void doQuery(String limit) {
        Builder builder = new QueryCallable.Builder(mHelper).limit(limit);
        configQuery(builder);
        QueryCallable query = builder.build();
        DeferredManager.when(query).then(mQueryCallback);
    }

    /**
     * 配置查詢的條件等
     * 
     * @param builder
     */
    protected abstract void configQuery(Builder builder);

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHelper.close();
        mDatas = null;
    }

    protected void notifyDataSetChanged() {
        ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
    }

    /**
     * 將Cursor 中的數據放到List 中
     * 
     * @param cursor
     * @return
     */
    protected abstract List<Bundle> toList(Cursor cursor);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        boolean isAtBottom = firstVisibleItem + visibleItemCount >= totalItemCount;
        if (!isLoading && firstVisibleItem > 0 && isAtBottom) {
            isLoading = true;

            if (mMaxLoadedItem < totalItemCount) {
                Log.d(TAG, "load more after " + totalItemCount);
                doQuery(totalItemCount + ", " + mPageSize);
                mMaxLoadedItem = totalItemCount;
            }
        } else {
            isLoading = false;
        }
    }

    public void refresh() {
        mMaxLoadedItem = 0;

        mDatas.clear();
        notifyDataSetChanged();

        doQuery();
        ((ArrayList<Bundle>) mDatas).trimToSize();
    }

    protected static final class QueryCallback implements Callback<Cursor> {

        private AbsPaginationListFragment mFragment;

        public QueryCallback(AbsPaginationListFragment fragment) {
            mFragment = fragment;
        }

        @Override
        public void apply(Cursor cursor) {
            mFragment.mDatas.addAll(mFragment.toList(cursor));
            mFragment.notifyDataSetChanged();

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }
}