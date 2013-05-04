/**
 * 
 */
package com.shuaqiu.yuanyuanxibo;

import com.shuaqiu.yuanyuanxibo.status.StatusListAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * @author shuaqiu 2013-5-1
 * 
 */
public class CommentsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ListView listView = (ListView) inflater.inflate(
                R.layout.fragment_comment_list, container, false);

        ListAdapter weiboAdapter = new StatusListAdapter(getActivity(),
                R.layout.listview_comment,
                new CommentBinder(getActivity()));

        listView.setAdapter(weiboAdapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
