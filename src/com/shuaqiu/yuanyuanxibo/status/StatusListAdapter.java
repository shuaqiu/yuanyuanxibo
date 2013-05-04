/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import com.shuaqiu.yuanyuanxibo.ViewBinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author shuaqiu Apr 27, 2013
 * 
 */
public class StatusListAdapter extends BaseAdapter {

    private ViewBinder mBinder;

    private int mResource;
    private LayoutInflater mInflater;

    public StatusListAdapter(Context context, int resource, ViewBinder binder) {
        mResource = resource;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mBinder = binder;
    }

    @Override
    public int getCount() {
        return mBinder.getDataItems().length();
    }

    @Override
    public Object getItem(int position) {
        return mBinder.getDataItems().opt(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = mInflater.inflate(mResource, parent, false);
        } else {
            v = convertView;
        }

        mBinder.bindView(position, v);

        return v;
    }

}
