package com.shuaqiu.yuanyuanxibo.status;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.shuaqiu.yuanyuanxibo.R;

/**
 * @author shuaqiu Apr 27, 2013
 */
public class StatusListFragment extends Fragment implements OnItemClickListener {

    private ListView mStatusList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_status_list,
                container, false);

        mStatusList = (ListView) fragmentView;

        ListAdapter statusListAdapter = new StatusListAdapter(getActivity(),
                R.layout.listview_status, new StatusBinder(getActivity()));

        mStatusList.setAdapter(statusListAdapter);

        mStatusList.setOnItemClickListener(this);

        return fragmentView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Intent intent = new Intent(getActivity(), StatusActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

}
