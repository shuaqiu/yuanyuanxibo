/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.ViewBinder;

/**
 * @author shuaqiu 2013-5-29
 * 
 */
public class StatusFragment extends Fragment {
    public static final String STATUS_CONTENT = "STATUS_CONTENT";

    private static final String TAG = "status";

    private static ViewBinder sBinder;

    private String mContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater
                .inflate(R.layout.activity_status, container, false);
        bindView(view);
        return view;
    }

    /**
     * @param view
     * @param position
     */
    private void bindView(View view) {
        if (mContent == null) {
            mContent = getArguments().getString(STATUS_CONTENT);
        }
        if (mContent == null) {
            return;
        }
        try {
            JSONObject status = new JSONObject(mContent);
            getViewBinder().bindView(view, status);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            return;
        }
    }

    private ViewBinder getViewBinder() {
        if (sBinder == null) {
            sBinder = new StatusBinder(getActivity(), StatusBinder.Type.DETAIL);
        }
        return sBinder;
    }

}
