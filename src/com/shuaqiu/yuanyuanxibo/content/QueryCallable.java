/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.content;

import java.util.Arrays;
import java.util.concurrent.Callable;

import android.database.Cursor;
import android.util.Log;

public class QueryCallable implements Callable<Cursor> {

    private static final String TAG = "QueryCallable";

    private AbsObjectHelper mHelper;
    private String mSelection;
    private String[] mSelectionArgs;
    private String mLimit;

    public QueryCallable(AbsObjectHelper helper) {
        this(helper, null, null, "100");
    }

    public QueryCallable(AbsObjectHelper helper, String selection,
            String[] selectionArgs) {
        this(helper, selection, selectionArgs, "100");
    }

    public QueryCallable(AbsObjectHelper helper, String selection,
            String[] selectionArgs, String limit) {
        mHelper = helper;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mLimit = limit;
    }

    /**
     * @return
     */
    @Override
    public Cursor call() {
        Log.d(TAG, "query by " + mHelper + ", condition: " + mSelection
                + ", args:  " + Arrays.deepToString(mSelectionArgs)
                + ", limit: " + mLimit);
        return mHelper.query(mSelection, mSelectionArgs, mLimit);
    }
}