/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.comment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.shuaqiu.common.ImageType;
import com.shuaqiu.common.promiss.Callback;
import com.shuaqiu.common.promiss.DeferredManager;
import com.shuaqiu.common.promiss.impl.DeferredTask.TaskJob;
import com.shuaqiu.common.task.PostCallable;
import com.shuaqiu.common.util.ViewUtil;
import com.shuaqiu.common.widget.SimpleBindAdapter;
import com.shuaqiu.common.widget.ViewBinder;
import com.shuaqiu.yuanyuanxibo.API;
import com.shuaqiu.yuanyuanxibo.Actions.Comment;
import com.shuaqiu.yuanyuanxibo.Actions.Status;
import com.shuaqiu.yuanyuanxibo.R;
import com.shuaqiu.yuanyuanxibo.content.EmotionHelper;
import com.shuaqiu.yuanyuanxibo.content.EmotionHelper.Column;
import com.shuaqiu.yuanyuanxibo.content.QueryCallable;
import com.shuaqiu.yuanyuanxibo.content.QueryCallable.Builder;
import com.shuaqiu.yuanyuanxibo.friend.FriendSelectionActivity;
import com.shuaqiu.yuanyuanxibo.trend.TrendSelectionActivity;

/**
 * @author shuaqiu Jun 3, 2013
 */
public class SendActivity extends FragmentActivity implements OnClickListener,
        Callback<String>, TextWatcher, DialogInterface.OnClickListener {

    private static final String TAG = "SendActivity";

    private static final int MAX_CHAR_COUNT = 140;

    private static final int CODE_TREND = 1;
    private static final int CODE_FRIEND = 2;

    private ViewHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_send);

        initViewHolder();

        // 調整一下順序, 先綁定事件處理, 然後才設置界面的顯示 (主要是為了計數的顯示)
        initAction();

        initViewState();
    }

    private void initViewHolder() {
        if (mHolder != null) {
            return;
        }
        View decorView = getWindow().getDecorView();
        mHolder = ViewHolder.from(decorView);
    }

    /**
     * 
     */
    private void initAction() {
        mHolder.mContent.addTextChangedListener(this);

        mHolder.mSend.setOnClickListener(this);
        mHolder.mBack.setOnClickListener(this);

        mHolder.mRemainCharCount.setOnClickListener(this);

        mHolder.mTrend.setOnClickListener(this);
        mHolder.mAtUser.setOnClickListener(this);
        mHolder.mEmotion.setOnClickListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        int remainCharCount = MAX_CHAR_COUNT - s.length();
        mHolder.mRemainCharCount.setText(Integer.toString(remainCharCount));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.send:
            send();
            break;
        case R.id.back:
            finish();
            break;
        case R.id.remain_char_count:
            confirmDeleteContent();
            break;
        case R.id.action_trend:
            showTrends();
            break;
        case R.id.action_at_user:
            showFriends();
            break;
        case R.id.action_emotion:
            toggleEmotions();
            break;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
            deleteContent();
            break;
        case DialogInterface.BUTTON_NEGATIVE:
            dialog.dismiss();
            break;
        }
    }

    private void send() {
        Bundle param = buildParam();
        if (param == null) {
            Toast.makeText(this, R.string.empty, Toast.LENGTH_SHORT).show();
            return;
        }
        String url = getUrl();
        DeferredManager.when(new PostCallable(url, param)).then(this);
    }

    private Bundle buildParam() {
        Editable content = mHolder.mContent.getText();

        Intent intent = getIntent();
        String action = intent.getAction();
        if (action != null && action.equals(Status.REPOST)) {
            // 轉發微博
            return buildRepostParam(content);
        }

        // 評論微博或回復評論
        if (content.length() == 0) {
            // 內容不能爲空
            return null;
        }
        return buildCommentParam(content);
    }

    /**
     * 轉發微博API 調用的參數設置
     * 
     * @param content
     *            內容
     * @return 參數設置
     * @see API.Status.REPOST
     */
    private Bundle buildRepostParam(Editable content) {
        Intent intent = getIntent();
        Bundle param = intent.getExtras();

        if (content.length() > 0) {
            param.putString("status", content.toString());
        }
        if (mHolder.mComment.isChecked()
                && mHolder.mCommentOriginal.isChecked()) {
            param.putInt("is_comment", 3);
        } else if (mHolder.mComment.isChecked()) {
            param.putInt("is_comment", 1);
        } else if (mHolder.mCommentOriginal.isChecked()) {
            param.putInt("is_comment", 2);
        }
        return param;
    }

    /**
     * 評論微博或回复評論API 調用的參數設置
     * 
     * @param content
     *            內容
     * @return 參數設置
     * @see API.Status.CREATE
     * @see API.Status.REPLY
     */
    private Bundle buildCommentParam(Editable content) {
        Intent intent = getIntent();
        Bundle param = intent.getExtras();

        if (mHolder.mRepost.isChecked()) {
            // 如果在評論的同時轉發微博, 則直接用轉發微博的API
            param.putString("status", content.toString());
            param.putInt("is_comment", 1);
            if (mHolder.mCommentOriginal.isChecked()) {
                param.putInt("is_comment", 3);
            }
        } else {
            // 評論微博或回復評論
            param.putString("comment", content.toString());

            if (mHolder.mCommentOriginal.isChecked()) {
                param.putInt("comment_ori", 1);
            }
        }

        return param;
    }

    private String getUrl() {
        Intent intent = getIntent();
        String action = intent.getAction();

        if (action != null && action.equals(Status.REPOST)
                || mHolder.mRepost.isChecked()) {
            return API.Status.REPOST;
        }
        if (action == null || action.equals(Comment.CREATE)) {
            return API.Comment.CREATE;
        }
        if (action.equals(Comment.REPLY)) {
            return API.Comment.REPLY;
        }
        return null;
    }

    @Override
    public void apply(String result) {
        Toast.makeText(this, R.string.sent, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void confirmDeleteContent() {
        new AlertDialog.Builder(this).setTitle(R.string.tip)
                .setIcon(R.drawable.ic_action_delete)
                .setMessage(R.string.confirm_delete_conent)
                .setPositiveButton(R.string.ok, this)
                .setNegativeButton(R.string.cancel, this).show();
    }

    private void deleteContent() {
        mHolder.mContent.setText("");
    }

    /**
     * 
     */
    private void showTrends() {
        Intent intent = new Intent(this, TrendSelectionActivity.class);
        startActivityForResult(intent, CODE_TREND);
    }

    /**
     * 
     */
    private void showFriends() {
        Intent intent = new Intent(this, FriendSelectionActivity.class);
        startActivityForResult(intent, CODE_FRIEND);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case CODE_TREND:
        case CODE_FRIEND:
            if (resultCode == RESULT_OK) {
                String selected = data.getStringExtra("selected");
                int cursorPosition = mHolder.mContent.getSelectionEnd();
                mHolder.mContent.getText().insert(cursorPosition, selected);
            }
            break;
        }

    }

    /**
     * 
     */
    private void toggleEmotions() {
        if (mHolder.mEmotions == null) {
            mHolder.inflateViewStub();

            if (mHolder.mPager.getAdapter() == null) {
                Log.d(TAG, "init emotions view pager");
                initEmotionsViewPager(mHolder.mPager);
            }
        }

        if (mHolder.mEmotions.getVisibility() == View.VISIBLE) {
            mHolder.mEmotions.setVisibility(View.GONE);
        } else {
            mHolder.mEmotions.setVisibility(View.VISIBLE);
        }
    }

    private void initEmotionsViewPager(final ViewPager viewPager) {
        EmotionJob callback = new EmotionJob(this, viewPager);
        DeferredManager.when(callback).then(callback);
    }

    /**
     * 設置界面的顯示
     */
    private void initViewState() {
        Intent intent = getIntent();
        String action = intent.getAction();
        Log.d(TAG, "action: " + action);
        if (action != null && action.equals(Status.REPOST)) {
            initRepostState(intent);
        } else {
            initCommentState(intent);
        }
    }

    /**
     * 轉發微博
     * 
     * @param intent
     */
    private void initRepostState(Intent intent) {
        mHolder.mTitle.setText(R.string.repost_weibo);

        mHolder.mRepost.setVisibility(View.GONE);
        mHolder.mComment.setVisibility(View.VISIBLE);

        boolean isRetweeted = intent.getBooleanExtra("isRetweeted", false);
        Log.d(TAG, "isRetweeted: " + isRetweeted);
        if (isRetweeted) {
            // 設置當前的內容爲之前轉發的內容
            String username = intent.getStringExtra("username");
            String current = intent.getStringExtra("text");
            String content = String.format("//@%s:%s", username, current);
            mHolder.mContent.setText(content);
            ViewUtil.addLinks(mHolder.mContent, ViewUtil.EMOTION);

            mHolder.mCommentOriginal.setVisibility(View.VISIBLE);
        } else {
            String defaultConent = getString(R.string.default_repost_content);
            mHolder.mContent.setText(defaultConent);
            // 將當前的內容全選了, 便於直接刪除
            mHolder.mContent.setSelection(0, defaultConent.length());

            mHolder.mCommentOriginal.setVisibility(View.GONE);
        }
    }

    /**
     * 評論微博或回復評論
     * 
     * @param intent
     */
    private void initCommentState(Intent intent) {
        mHolder.mTitle.setText(R.string.send_comment);

        String action = intent.getAction();
        if (action == null || action.equals(Comment.CREATE)) {
        } else if (action.equals(Comment.REPLY)) {
            String username = intent.getStringExtra("username");
            String replyTo = getString(R.string.default_comment_content,
                    username);
            mHolder.mContent.setText(replyTo);
            // 設置輸入光標的位置
            mHolder.mContent.setSelection(replyTo.length());
        }

        mHolder.mRepost.setVisibility(View.VISIBLE);
        mHolder.mComment.setVisibility(View.GONE);
        mHolder.mCommentOriginal.setVisibility(View.GONE);
    }

    /**
     * @author shuaqiu Jun 22, 2013
     */
    private static final class EmotionJob implements TaskJob<Cursor> {
        private FragmentActivity mFragmentActivity;
        private final ViewPager mViewPager;
        private EmotionHelper mHelper;

        private EmotionJob(FragmentActivity fragmentActivity,
                ViewPager viewPager) {
            mFragmentActivity = fragmentActivity;
            mViewPager = viewPager;
        }

        @Override
        public Cursor call() throws Exception {
            mHelper = new EmotionHelper(mFragmentActivity);
            mHelper.openForRead();
            String[] columns = new String[] { Column.phrase.name(),
                    Column.url.name() };
            Builder builder = new QueryCallable.Builder(mHelper).columns(
                    columns).selection(Column.hot.name() + " = 1");
            return builder.build().call();
        }

        @Override
        public void apply(Cursor result) {
            List<Bundle> emotions = extract(result);

            result.close();
            mHelper.close();

            FragmentManager manager = mFragmentActivity
                    .getSupportFragmentManager();
            EmotionPagerAdapter adapter = new EmotionPagerAdapter(manager,
                    emotions);
            mViewPager.setAdapter(adapter);
        }

        private List<Bundle> extract(Cursor result) {
            List<Bundle> emotions = new ArrayList<Bundle>(result.getCount());
            while (result.moveToNext()) {
                Bundle emotion = new Bundle(2);
                emotion.putString(Column.phrase.name(), result.getString(0));
                emotion.putString(Column.url.name(), result.getString(1));
                emotions.add(emotion);
            }
            return emotions;
        }
    }

    private static class EmotionPagerAdapter extends FragmentStatePagerAdapter {

        private static final int PAGE_EMOTION_COUNT = 24;

        private List<Bundle> mEmotions;
        private Fragment[] items;

        public EmotionPagerAdapter(FragmentManager fm, List<Bundle> emotions) {
            super(fm);
            mEmotions = emotions;
            items = new Fragment[getCount()];
        }

        @Override
        public Fragment getItem(int position) {
            if (items[position] == null) {
                EmotionFragment fragment = new EmotionFragment();
                fragment.setArguments(buildArg(position));
                items[position] = fragment;
            }
            return items[position];
        }

        /**
         * @param position
         * @return
         */
        protected Bundle buildArg(int position) {
            Bundle args = new Bundle(1);
            args.putParcelableArray("emotions", getPageEmotions(position));
            return args;
        }

        /**
         * @param position
         * @return
         */
        protected Bundle[] getPageEmotions(int position) {
            int start = position * PAGE_EMOTION_COUNT;
            int end = Math.min(start + PAGE_EMOTION_COUNT, mEmotions.size());
            Bundle[] emotionsArr = new Bundle[PAGE_EMOTION_COUNT];
            emotionsArr = mEmotions.subList(start, end).toArray(emotionsArr);
            return emotionsArr;
        }

        @Override
        public int getCount() {
            return (mEmotions.size() - 1) / PAGE_EMOTION_COUNT + 1;
        }

    }

    public static final class EmotionFragment extends Fragment implements
            OnItemClickListener {
        private GridView mGridView;

        private EditText mContent;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_emotions, container,
                    false);
            initView(view);

            return view;
        }

        /**
         * @param view
         */
        protected void initView(View view) {
            mGridView = (GridView) view;

            List<Bundle> data = getData();
            EmotionBinder binder = new EmotionBinder();
            ListAdapter adapter = new SimpleBindAdapter<Bundle>(getActivity(),
                    data, R.layout.grid_emotion, binder);

            mGridView.setAdapter(adapter);
            mGridView.setOnItemClickListener(this);
        }

        /**
         * @return
         */
        protected List<Bundle> getData() {
            Bundle args = getArguments();
            Bundle[] emotions = (Bundle[]) args.getParcelableArray("emotions");
            return Arrays.asList(emotions);
        }

        private EditText getContentView() {
            if (mContent == null) {
                FragmentActivity activity = getActivity();
                mContent = (EditText) activity.findViewById(R.id.content);
            }
            return mContent;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            Bundle item = (Bundle) parent.getAdapter().getItem(position);
            String phrase = item.getString(Column.phrase.name());
            Log.d(TAG, "click on " + phrase);

            EditText contentView = getContentView();
            int cursorPosition = contentView.getSelectionEnd();
            contentView.getText().insert(cursorPosition, phrase);
            ViewUtil.addLinks(contentView, ViewUtil.EMOTION);
        }

    }

    /**
     * @author shuaqiu Jun 22, 2013
     */
    private static final class EmotionBinder implements ViewBinder<Bundle> {
        @Override
        public void bindView(View view, Bundle data) {
            String url = data.getString(Column.url.name());
            ViewUtil.setImage(view, ImageType.EMOTION, url);
        }
    }

    private static class ViewHolder {
        private TextView mTitle;
        private EditText mContent;
        private TextView mRemainCharCount;
        private CheckBox mRepost;
        private CheckBox mComment;
        private CheckBox mCommentOriginal;
        private View mBack;
        private View mSend;
        private View mTrend;
        private View mAtUser;
        private View mEmotion;

        private ViewStub mEmotionsStub;
        private View mEmotions;
        private ViewPager mPager;

        static ViewHolder from(View v) {
            Object tag = v.getTag();
            if (tag != null && tag instanceof ViewHolder) {
                return (ViewHolder) tag;
            }

            ViewHolder holder = new ViewHolder();
            v.setTag(holder);

            holder.mContent = (EditText) v.findViewById(R.id.content);
            holder.mTitle = (TextView) v.findViewById(R.id.title);
            holder.mRemainCharCount = (TextView) v
                    .findViewById(R.id.remain_char_count);

            holder.mRepost = (CheckBox) v.findViewById(R.id.repost_weibo);
            holder.mComment = (CheckBox) v.findViewById(R.id.comment_weibo);
            holder.mCommentOriginal = (CheckBox) v
                    .findViewById(R.id.comment_original_weibo);

            holder.mBack = v.findViewById(R.id.back);
            holder.mSend = v.findViewById(R.id.send);
            holder.mTrend = v.findViewById(R.id.action_trend);
            holder.mAtUser = v.findViewById(R.id.action_at_user);
            holder.mEmotion = v.findViewById(R.id.action_emotion);

            holder.mEmotionsStub = (ViewStub) v
                    .findViewById(R.id.stub_emotions);

            return holder;
        }

        void inflateViewStub() {
            if (mEmotions != null) {
                return;
            }

            Log.d(TAG, "inflate view stub");
            mEmotions = mEmotionsStub.inflate();
            mPager = (ViewPager) mEmotions.findViewById(R.id.pager);
        }
    }
}
