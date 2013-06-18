/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.content;

import java.util.concurrent.Callable;

import android.database.Cursor;
import android.util.Log;

public class QueryCallable implements Callable<Cursor> {

    private static final String TAG = "QueryCallable";

    private DatabaseHelper mHelper;

    // return mDb.query(table, columns, selection, selectionArgs, groupBy,
    // having, orderBy, limit);
    private String mTable;
    private String[] mColumns;
    private String mSelection;
    private String[] mArgs;
    private String mGroupBy;
    private String mHaving;
    private String mOrderBy;
    private String mLimit;

    private QueryCallable(DatabaseHelper helper) {
        mHelper = helper;
    }

    private QueryCallable(AbsObjectHelper helper) {
        mHelper = helper;
        mTable = helper.table;
        mColumns = helper.names;
        mOrderBy = helper.orderBy;
    }

    /**
     * @return
     */
    @Override
    public Cursor call() {
        if (mTable == null || mColumns == null) {
            throw new IllegalArgumentException(
                    "table and columns must be present!");
        }

        Log.d(TAG, "query by " + this);

        return mHelper.query(mTable, mColumns, mSelection, mArgs,
                mGroupBy, mHaving, mOrderBy, mLimit);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static class Builder {
        private QueryCallable query;

        public Builder(DatabaseHelper helper) {
            if (helper instanceof AbsObjectHelper) {
                query = new QueryCallable((AbsObjectHelper) helper);
            } else {
                query = new QueryCallable(helper);
            }
        }

        public Builder(AbsObjectHelper helper) {
            query = new QueryCallable(helper);
        }

        public Builder selection(String selection) {
            query.mSelection = selection;
            return this;
        }

        public Builder args(String[] args) {
            query.mArgs = args;
            return this;
        }

        public Builder limit(String limit) {
            query.mLimit = limit;
            return this;
        }

        public Builder limit(int limit) {
            query.mLimit = limit + "";
            return this;
        }

        public Builder limit(int skip, int limit) {
            query.mLimit = skip + ", " + limit;
            return this;
        }

        public Builder table(String table) {
            query.mTable = table;
            return this;
        }

        public Builder columns(String[] columns) {
            query.mColumns = columns;
            return this;
        }

        public Builder groupBy(String groupBy) {
            query.mGroupBy = groupBy;
            return this;
        }

        public Builder having(String having) {
            query.mHaving = having;
            return this;
        }

        public Builder orderBy(String orderBy) {
            query.mOrderBy = orderBy;
            return this;
        }

        public QueryCallable build() {
            return query;
        }
    }
}