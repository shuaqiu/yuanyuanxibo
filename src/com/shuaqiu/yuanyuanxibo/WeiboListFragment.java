package com.shuaqiu.yuanyuanxibo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * @author shuaqiu Apr 27, 2013
 */
public class WeiboListFragment extends Fragment {
    private static final String[] fromField = new String[] { "thumbnail_pic",
            "user_name", "created_at", "text", "retweeted_text", "source",
            "attitudes_count", "reposts_count", "comments_count" };
    private static final int[] toWidgetId = new int[] { R.id.thumbnail_pic,
            R.id.user_name, R.id.created_at, R.id.text, R.id.retweeted_text,
            R.id.source, R.id.attitudes_count, R.id.reposts_count,
            R.id.comments_count };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_weibo_list,
                container, false);

        ListView weiboList = (ListView) fragmentView;

        List<Map<String, Object>> weiboDatas = getWeiboData();
        ListAdapter weiboAdapter = new SimpleAdapter(getActivity(), weiboDatas,
                R.layout.listview_weibo_list, fromField, toWidgetId);

        weiboList.setAdapter(weiboAdapter);

        return fragmentView;
    }

    /**
     * @return
     */
    private List<Map<String, Object>> getWeiboData() {
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();

        try {
            JSONObject json = new JSONObject(getJson());
            JSONArray array = json.getJSONArray("statuses");

            for (int i = 0; i < array.length(); i++) {
                Map<String, Object> data = toMap(array.getJSONObject(i));

                JSONObject user = (JSONObject) data.get("user");
                data.put("user_name", user.get("name"));

                JSONObject retweetedStatus = (JSONObject) data
                        .get("retweeted_status");
                if (retweetedStatus != null) {
                    data.put("retweeted_text", retweetedStatus.get("text"));

                }

                datas.add(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return datas;
    }

    private Map<String, Object> toMap(JSONObject json) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            map.put(key, json.get(key));
        }

        return map;
    }

    private String getJson() throws IOException {
        InputStream stream = getClass().getResourceAsStream("/testdata.json");
        byte[] buf = new byte[stream.available()];
        stream.read(buf);
        return new String(buf);
    }
}
