/**
 * 
 */
package com.shuaqiu.common.widget;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author shuaqiu Apr 27, 2013
 */
public class SimpleBindAdapter<Data> extends BaseAdapter {

    private ViewBinder<Data> mBinder;

    private LayoutInflater mInflater;

    private List<Data> mData;

    private int mResource;

    private int mDropDownResource;

    public SimpleBindAdapter(Context context, List<Data> data, int resource,
            ViewBinder<Data> binder) {
        mData = data;
        mResource = mDropDownResource = resource;
        mBinder = binder;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mResource);
    }

    private View createViewFromResource(int position, View convertView,
            ViewGroup parent, int resource) {
        View v;
        if (convertView == null) {
            v = mInflater.inflate(resource, parent, false);
        } else {
            v = convertView;
        }

        bindView(position, v);

        return v;
    }

    public void setDropDownViewResource(int resource) {
        this.mDropDownResource = resource;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent,
                mDropDownResource);
    }

    private void bindView(int position, View view) {
        final Data aData = mData.get(position);
        if (aData == null) {
            return;
        }
        mBinder.bindView(view, aData);
    }

}
