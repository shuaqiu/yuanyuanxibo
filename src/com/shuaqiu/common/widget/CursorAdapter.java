/**
 * 
 */
package com.shuaqiu.common.widget;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author shuaqiu 2013-6-2
 */
public abstract class CursorAdapter extends BaseAdapter {

    private static final String ID = "id";

    protected boolean mDataValid;

    protected Cursor mCursor;

    protected Context mContext;

    protected String mRowIdName = ID;

    protected int mRowIDColumn;

    protected ChangeObserver mChangeObserver;

    protected DataSetObserver mDataSetObserver;

    /**
     * If set the adapter will register a content observer on the cursor and
     * will call {@link #onContentChanged()} when a notification comes in. Be
     * careful when
     * using this flag: you will need to unset the current Cursor from the
     * adapter
     * to avoid leaks due to its registered observers. This flag is not needed
     * when using a CursorAdapter with a {@link android.content.CursorLoader}.
     */
    public static final int FLAG_REGISTER_CONTENT_OBSERVER = 0x02;

    /**
     * Recommended constructor.
     * 
     * @param context
     *            The context
     * @param flags
     *            Flags used to determine the behavior of the adapter; may
     *            be any combination of {@link #FLAG_AUTO_REQUERY} and
     *            {@link #FLAG_REGISTER_CONTENT_OBSERVER}.
     */
    public CursorAdapter(Context context, int flags) {
        init(context, null, flags);
    }

    /**
     * Recommended constructor.
     * 
     * @param c
     *            The cursor from which to get the data.
     * @param context
     *            The context
     * @param flags
     *            Flags used to determine the behavior of the adapter; may
     *            be any combination of {@link #FLAG_AUTO_REQUERY} and
     *            {@link #FLAG_REGISTER_CONTENT_OBSERVER}.
     */
    public CursorAdapter(Context context, Cursor c, int flags) {
        init(context, c, flags);
    }

    void init(Context context, Cursor c, int flags) {
        boolean cursorPresent = c != null;
        mCursor = c;
        mDataValid = cursorPresent;
        mContext = context;
        mRowIDColumn = cursorPresent ? c.getColumnIndexOrThrow(mRowIdName) : -1;
        if ((flags & FLAG_REGISTER_CONTENT_OBSERVER) == FLAG_REGISTER_CONTENT_OBSERVER) {
            mChangeObserver = new ChangeObserver();
            mDataSetObserver = new MyDataSetObserver();
        } else {
            mChangeObserver = null;
            mDataSetObserver = null;
        }

        if (cursorPresent) {
            if (mChangeObserver != null) {
                c.registerContentObserver(mChangeObserver);
            }
            if (mDataSetObserver != null) {
                c.registerDataSetObserver(mDataSetObserver);
            }
        }
    }

    /**
     * Returns the cursor.
     * 
     * @return the cursor.
     */
    public Cursor getCursor() {
        return mCursor;
    }

    /**
     * @see android.widget.ListAdapter#getCount()
     */
    @Override
    public int getCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    /**
     * @see android.widget.ListAdapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        if (mDataValid && mCursor != null) {
            mCursor.moveToPosition(position);
            return mCursor;
        }
        return null;
    }

    /**
     * @see android.widget.ListAdapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null) {
            if (mCursor.moveToPosition(position)) {
                return mCursor.getLong(mRowIDColumn);
            }
        }
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * @see android.widget.ListAdapter#getView(int, View, ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (!mDataValid) {
            throw new IllegalStateException(
                    "this should only be called when the cursor is valmRowIdName");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position "
                    + position);
        }
        View v;
        if (convertView == null) {
            v = newView(mContext, mCursor, parent);
        } else {
            v = convertView;
        }
        bindView(v, mContext, mCursor);
        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (mDataValid) {
            mCursor.moveToPosition(position);
            View v;
            if (convertView == null) {
                v = newDropDownView(mContext, mCursor, parent);
            } else {
                v = convertView;
            }
            bindView(v, mContext, mCursor);
            return v;
        } else {
            return null;
        }
    }

    /**
     * Makes a new view to hold the data pointed to by cursor.
     * 
     * @param context
     *            Interface to application's global information
     * @param cursor
     *            The cursor from which to get the data. The cursor is already
     *            moved to the correct position.
     * @param parent
     *            The parent to which the new view is attached to
     * @return the newly created view.
     */
    protected abstract View newView(Context context, Cursor cursor,
            ViewGroup parent);

    /**
     * Makes a new drop down view to hold the data pointed to by cursor.
     * 
     * @param context
     *            Interface to application's global information
     * @param cursor
     *            The cursor from which to get the data. The cursor is already
     *            moved to the correct position.
     * @param parent
     *            The parent to which the new view is attached to
     * @return the newly created view.
     */
    protected View newDropDownView(Context context, Cursor cursor,
            ViewGroup parent) {
        return newView(context, cursor, parent);
    }

    /**
     * Bind an existing view to the data pointed to by cursor
     * 
     * @param view
     *            Existing view, returned earlier by newView
     * @param context
     *            Interface to application's global information
     * @param cursor
     *            The cursor from which to get the data. The cursor is already
     *            moved to the correct position.
     */
    protected abstract void bindView(View view, Context context, Cursor cursor);

    /**
     * Change the underlying cursor to a new cursor. If there is an existing
     * cursor it will be closed.
     * 
     * @param cursor
     *            The new cursor to be used
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor. Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     * 
     * @param newCursor
     *            The new cursor to be used.
     * @return Returns the previously set Cursor, or null if there wasa not one.
     *         If the given new Cursor is the same instance is the previously
     *         set Cursor, null is also returned.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        if (oldCursor != null) {
            if (mChangeObserver != null) {
                oldCursor.unregisterContentObserver(mChangeObserver);
            }
            if (mDataSetObserver != null) {
                oldCursor.unregisterDataSetObserver(mDataSetObserver);
            }
        }
        mCursor = newCursor;
        if (newCursor != null) {
            if (mChangeObserver != null) {
                newCursor.registerContentObserver(mChangeObserver);
            }
            if (mDataSetObserver != null) {
                newCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIDColumn = newCursor.getColumnIndexOrThrow(mRowIdName);
            mDataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            mRowIDColumn = -1;
            mDataValid = false;
            // notify the observers about the lack of a data set
            notifyDataSetInvalidated();
        }
        return oldCursor;
    }

    /**
     * Called when the {@link ContentObserver} on the cursor receives a change
     * notification.
     * The default implementation provides the auto-requery logic, but may be
     * overridden by
     * sub classes.
     * 
     * @see ContentObserver#onChange(boolean)
     */
    protected void onContentChanged() {
        if (mCursor != null && !mCursor.isClosed()) {
            mDataValid = mCursor.requery();
        }
    }

    private class ChangeObserver extends ContentObserver {
        public ChangeObserver() {
            super(new Handler());
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            onContentChanged();
        }
    }

    private class MyDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            mDataValid = false;
            notifyDataSetInvalidated();
        }
    }

}
