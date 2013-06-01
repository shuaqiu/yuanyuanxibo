/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import com.shuaqiu.common.widget.ResourceCursorAdapter;
import com.shuaqiu.yuanyuanxibo.ViewBinder;

/**
 * @author shuaqiu Apr 27, 2013
 */
public abstract class SimpleCursorAdapter<Data> extends ResourceCursorAdapter {

    private ViewBinder<Data> mBinder;

    public SimpleCursorAdapter(Context context, int resource,
            ViewBinder<Data> binder) {
        super(context, resource);
        mBinder = binder;
    }

    public SimpleCursorAdapter(Context context, int resource, int flag,
            ViewBinder<Data> binder) {
        super(context, resource, flag);
        mBinder = binder;
    }

    public SimpleCursorAdapter(Context context, int resource, Cursor cursor,
            ViewBinder<Data> binder) {
        this(context, resource, cursor, 0, binder);
    }

    public SimpleCursorAdapter(Context context, int resource, Cursor cursor,
            int flag, ViewBinder<Data> binder) {
        super(context, resource, cursor, flag);
        mBinder = binder;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final Data bundle = toData(cursor);
        if (bundle != null) {
            mBinder.bindView(view, bundle);
        }
    }

    protected abstract Data toData(Cursor cursor);

}
