/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.shuaqiu.yuanyuanxibo.ViewBinder;

/**
 * @author shuaqiu Apr 27, 2013
 */
public class StatusListAdapter extends BaseAdapter {

    private ViewBinder mBinder;

    private int mResource;
    private LayoutInflater mInflater;

    private JSONArray mStatuses;

    public StatusListAdapter(Context context, int resource, ViewBinder binder,
            JSONArray statuses) {
        mResource = resource;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mBinder = binder;

        mStatuses = statuses;
    }

    @Override
    public int getCount() {
        return mStatuses.length();
    }

    @Override
    public Object getItem(int position) {
        return mStatuses.opt(position);
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

        final JSONObject status = mStatuses.optJSONObject(position);
        if (status != null) {
            mBinder.bindView(v, status);
        }

        return v;
    }

}
