/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.trend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.SimpleExpandableListAdapter;

import com.shuaqiu.common.promiss.Callback;
import com.shuaqiu.common.promiss.DeferredManager;
import com.shuaqiu.common.task.GetCallable;
import com.shuaqiu.yuanyuanxibo.API.Trend;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.StateKeeper;
import com.shuaqiu.yuanyuanxibo.auth.Oauth2AccessToken;

/**
 * @author shuaqiu 2013-6-19
 */
public class TrendSelectionActivity extends Activity implements
        OnClickListener, OnGroupExpandListener, OnChildClickListener {

    private static final String TAG = "TrendSelectionActivity";

    private static final int[] GROUPS = new int[] { R.string.hourly,
            R.string.daily, R.string.weekly };

    private static final String KEY = "data";

    private ViewHolder mHolder;

    private List<List<Map<String, String>>> childData;

    private BaseExpandableListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_trend_selection);

        initViewHolder();
        initListView();
        initViewAction();

    }

    private void initViewHolder() {
        if (mHolder != null) {
            return;
        }

        View decorView = getWindow().getDecorView();
        mHolder = ViewHolder.from(decorView);
    }

    private void initViewAction() {
        mHolder.mOk.setOnClickListener(this);
        mHolder.mBack.setOnClickListener(this);
        mHolder.mTrends.setOnChildClickListener(this);
        mHolder.mTrends.setOnGroupExpandListener(this);
    }

    private void initListView() {
        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>(
                GROUPS.length);
        childData = new ArrayList<List<Map<String, String>>>(GROUPS.length);

        for (int strId : GROUPS) {
            Map<String, String> group = new HashMap<String, String>();
            group.put(KEY, getString(strId));
            groupData.add(group);

            childData.add(new ArrayList<Map<String, String>>(0));
        }

        String[] from = new String[] { KEY };
        int[] to = new int[] { android.R.id.text1 };
        adapter = new SimpleExpandableListAdapter(this, groupData,
                android.R.layout.simple_expandable_list_item_2, from, to,
                childData, android.R.layout.simple_list_item_1, from, to);
        mHolder.mTrends.setAdapter(adapter);
    }

    private void req(int resId, final int groupPosition) {
        Bundle params = new Bundle(1);
        Oauth2AccessToken accessToken = StateKeeper.accessToken;
        params.putString("access_token", accessToken.getAccessToken());

        String api = Trend.HOURLY;
        switch (resId) {
        case R.string.hourly:
            api = Trend.HOURLY;
            break;
        case R.string.daily:
            api = Trend.DAILY;
            break;
        case R.string.weekly:
            api = Trend.WEEKLY;
            break;
        }
        DeferredManager.when(new GetCallable(api, params)).then(
                new TrendCallback(adapter, childData, groupPosition));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.ok:
            selected();
            break;
        case R.id.back:
            finish();
            break;
        }
    }

    private void selected() {
        Editable text = mHolder.mSelectedTrend.getText();
        String trend = text.insert(0, "#").append("#").toString();
        selected(trend);
    }

    private void selected(String trend) {
        Intent data = new Intent();
        data.putExtra("selected", trend);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        List<Map<String, String>> child = childData.get(groupPosition);
        if (child == null || child.size() == 0) {
            req(GROUPS[groupPosition], groupPosition);
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
            int groupPosition, int childPosition, long id) {
        String trend = childData.get(groupPosition).get(childPosition).get(KEY);
        Log.d(TAG, "select -> " + trend);
        selected(trend);
        return false;
    }

    /**
     * @author shuaqiu 2013-6-19
     * 
     */
    private static final class TrendCallback implements Callback<String> {
        private BaseExpandableListAdapter adapter;
        private List<List<Map<String, String>>> childData;
        private int groupPosition;

        /**
         * @param adapter
         * @param childData
         * @param groupPosition
         */
        public TrendCallback(BaseExpandableListAdapter adapter,
                List<List<Map<String, String>>> childData, int groupPosition) {
            this.adapter = adapter;
            this.childData = childData;
            this.groupPosition = groupPosition;
        }

        @Override
        public void apply(String result) {
            JSONArray trends = getTrends(result);

            List<Map<String, String>> child = new ArrayList<Map<String, String>>(
                    trends.length());
            for (int i = 0; i < trends.length(); i++) {
                JSONObject trend = trends.optJSONObject(i);
                String name = trend.optString("name");

                Map<String, String> m = new HashMap<String, String>(1);
                m.put(KEY, "#" + name + "#");
                child.add(m);
            }
            childData.set(groupPosition, child);
            adapter.notifyDataSetChanged();
        }

        private JSONArray getTrends(String result) {
            if (result == null) {
                return new JSONArray();
            }

            JSONObject json = null;
            try {
                json = new JSONObject(result);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                return new JSONArray();
            }

            JSONObject trends = json.optJSONObject("trends");
            if (trends == null) {
                return new JSONArray();
            }

            @SuppressWarnings("unchecked")
            Iterator<String> keys = trends.keys();
            if (!keys.hasNext()) {
                return new JSONArray();
            }
            return trends.optJSONArray(keys.next());
        }
    }

    private static class ViewHolder {
        private EditText mSelectedTrend;

        private View mOk;
        private View mBack;

        private ExpandableListView mTrends;

        static ViewHolder from(View v) {
            Object tag = v.getTag();
            if (tag != null && tag instanceof ViewHolder) {
                return (ViewHolder) tag;
            }

            ViewHolder holder = new ViewHolder();
            v.setTag(holder);

            holder.mSelectedTrend = (EditText) v
                    .findViewById(R.id.selected_trend);
            holder.mOk = v.findViewById(R.id.ok);
            holder.mBack = v.findViewById(R.id.back);

            holder.mTrends = (ExpandableListView) v.findViewById(R.id.trends);

            return holder;
        }
    }
}
