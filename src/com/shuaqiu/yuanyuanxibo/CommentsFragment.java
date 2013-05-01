/**
 * 
 */
package com.shuaqiu.yuanyuanxibo;

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
                R.layout.fragment_comments, container, false);

        ListAdapter weiboAdapter = new WeiboListAdapter(getActivity(),
                R.layout.listview_comment_list,
                new CommentBinder(getActivity()));

        listView.setAdapter(weiboAdapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
