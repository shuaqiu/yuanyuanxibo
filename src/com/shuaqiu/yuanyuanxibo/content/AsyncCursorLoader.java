/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.content;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

/**
 * @author shuaqiu May 6, 2013
 */
public class AsyncCursorLoader extends AsyncTaskLoader<Cursor> {
    final ForceLoadContentObserver mObserver;

    String mTable;
    String[] mProjection;
    String mSelection;
    String[] mSelectionArgs;
    String mGroupBy;
    String mHaving;
    String mSortOrder;
    String mLimit;

    SQLiteDatabase mDb;
    Cursor mCursor;

    /* Runs on a worker thread */
    @Override
    public Cursor loadInBackground() {
        if (mDb == null || !mDb.isOpen()) {
            mDb = new DatabaseOpenHelper(getContext()).getWritableDatabase();
        }
        Cursor cursor = mDb.query(mTable, mProjection, mSelection,
                mSelectionArgs, mGroupBy, mHaving, mSortOrder, mLimit);
        if (cursor != null) {
            // Ensure the cursor window is filled
            cursor.getCount();
            // Registers an observer to get notifications from the content
            // provider when
            // the cursor needs to be refreshed.
            cursor.registerContentObserver(mObserver);
        }
        return cursor;
    }

    /* Runs on the UI thread */
    @Override
    public void deliverResult(Cursor cursor) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            if (cursor != null) {
                cursor.close();
            }
            return;
        }
        Cursor oldCursor = mCursor;
        mCursor = cursor;

        if (isStarted()) {
            super.deliverResult(cursor);
        }

        if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }

    /**
     * Creates an empty unspecified CursorLoader. You must follow this with
     * calls to {@link #setUri(Uri)}, {@link #setSelection(String)}, etc to
     * specify the query to perform.
     */
    public AsyncCursorLoader(Context context) {
        super(context);
        mObserver = new ForceLoadContentObserver();
    }

    /**
     * Creates a fully-specified CursorLoader. See
     * {@link android.content.ContentResolver#query(Uri, String[], String, String[], String)
     * ContentResolver.query()} for documentation on the meaning of the
     * parameters. These will be passed as-is to that call.
     */
    public AsyncCursorLoader(Context context, String table,
            String[] projection, String selection, String[] selectionArgs,
            String groupBy, String having, String sortOrder) {
        this(context, table, projection, selection, selectionArgs, groupBy,
                having, sortOrder, null);
    }

    public AsyncCursorLoader(Context context, String table,
            String[] projection, String selection, String[] selectionArgs,
            String groupBy, String having, String sortOrder, String limit) {
        super(context);
        mObserver = new ForceLoadContentObserver();
        mTable = table;
        mProjection = projection;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mGroupBy = groupBy;
        mHaving = having;
        mSortOrder = sortOrder;
        mLimit = limit;
    }

    /**
     * Starts an asynchronous load of the contacts list data. When the result is
     * ready the callbacks will be called on the UI thread. If a previous load
     * has been completed and is still valid the result may be passed to the
     * callbacks immediately. Must be called from the UI thread
     */
    @Override
    protected void onStartLoading() {
        if (mCursor != null) {
            deliverResult(mCursor);
        }
        if (takeContentChanged() || mCursor == null) {
            forceLoad();
        }
    }

    /**
     * Must be called from the UI thread
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        if (mDb != null && mDb.isOpen()) {
            mDb.close();
        }
        mCursor = null;
    }

    public String getTable() {
        return mTable;
    }

    public void setTable(String table) {
        mTable = table;
    }

    public String[] getProjection() {
        return mProjection;
    }

    public void setProjection(String[] projection) {
        mProjection = projection;
    }

    public String getSelection() {
        return mSelection;
    }

    public void setSelection(String selection) {
        mSelection = selection;
    }

    public String[] getSelectionArgs() {
        return mSelectionArgs;
    }

    public void setSelectionArgs(String[] selectionArgs) {
        mSelectionArgs = selectionArgs;
    }

    public String getGroupBy() {
        return mGroupBy;
    }

    public void setGroupBy(String groupBy) {
        mGroupBy = groupBy;
    }

    public String getHaving() {
        return mHaving;
    }

    public void setHaving(String having) {
        mHaving = having;
    }

    public String getSortOrder() {
        return mSortOrder;
    }

    public void setSortOrder(String sortOrder) {
        mSortOrder = sortOrder;
    }

    public String getLimit() {
        return mLimit;
    }

    public void setLimit(String limit) {
        mLimit = limit;
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer,
            String[] args) {
        super.dump(prefix, fd, writer, args);
        writer.print(prefix);
        writer.print("mTable=");
        writer.println(mTable);
        writer.print(prefix);
        writer.print("mProjection=");
        writer.println(Arrays.toString(mProjection));
        writer.print(prefix);
        writer.print("mSelection=");
        writer.println(mSelection);
        writer.print(prefix);
        writer.print("mSelectionArgs=");
        writer.println(Arrays.toString(mSelectionArgs));
        writer.print(prefix);
        writer.print("mGroupBy=");
        writer.println(mGroupBy);
        writer.print(prefix);
        writer.print("mHaving=");
        writer.println(mHaving);
        writer.print(prefix);
        writer.print("mSortOrder=");
        writer.println(mSortOrder);
        writer.print(prefix);
        writer.print("mLimit=");
        writer.println(mLimit);
        writer.print(prefix);
        writer.print("mCursor=");
        writer.println(mCursor);
    }
}
