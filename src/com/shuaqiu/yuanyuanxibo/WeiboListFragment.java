package com.shuaqiu.yuanyuanxibo;

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

/**
 * @author shuaqiu Apr 27, 2013
 */
public class WeiboListFragment extends Fragment implements OnItemClickListener {

    private ListView weiboList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_weibo_list,
                container, false);

        weiboList = (ListView) fragmentView;

        ListAdapter weiboAdapter = new WeiboListAdapter(getActivity(),
                R.layout.listview_weibo_list, new StatusBinder(getActivity()));

        weiboList.setAdapter(weiboAdapter);

        weiboList.setOnItemClickListener(this);

        return fragmentView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Intent intent = new Intent(getActivity(), WeiboDetailActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

}
