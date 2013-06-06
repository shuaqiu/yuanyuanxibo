/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.shuaqiu.common.widget.ViewBinder;
import com.shuaqiu.yuanyuanxibo.R;

/**
 * @author shuaqiu 2013-5-29
 */
public class StatusFragment extends Fragment implements OnClickListener {
    public static final String STATUS = "STATUS_CONTENT";

    private static final String TAG = "status";

    private static ViewBinder<Bundle> sBinder;

    private Bundle mStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater
                .inflate(R.layout.fragment_status, container, false);
        bindView(view);

        view.findViewById(R.id.back).setOnClickListener(this);

        return view;
    }

    /**
     * @param view
     * @param position
     */
    private void bindView(View view) {
        if (mStatus == null) {
            mStatus = getArguments().getBundle(STATUS);
        }
        if (mStatus == null) {
            return;
        }

        getViewBinder().bindView(view, mStatus);

    }

    private ViewBinder<Bundle> getViewBinder() {
        if (sBinder == null) {
            Log.d(TAG, "init the view binder");
            sBinder = new BundleStatusBinder(getActivity(),
                    StatusBinder.Type.DETAIL);
        }
        return sBinder;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.back:
            getActivity().finish();
            break;
        }
    }

}
