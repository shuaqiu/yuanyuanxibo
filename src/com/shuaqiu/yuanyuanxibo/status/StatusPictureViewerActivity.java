/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.shuaqiu.common.task.AsyncImageViewTask;
import com.shuaqiu.common.task.AsyncTaskListener;
import com.shuaqiu.yuanyuanxibo.R;

/**
 * @author shuaqiu Jun 3, 2013
 */
public class StatusPictureViewerActivity extends Activity implements
        OnClickListener, AsyncTaskListener<JSONObject>, ViewFactory,
        OnItemClickListener {

    private static final String TAG = "SendCommentActivity";

    private Gallery mGalleryView;
    private ImageSwitcher mImageSwitcherView;

    private FrameLayout.LayoutParams mLayoutParams;

    private String[] mPicUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_status_pics);

        Intent intent = getIntent();
        mPicUrls = intent.getStringArrayExtra("pics");
        int position = intent.getIntExtra("position", 0);

        mImageSwitcherView = (ImageSwitcher) findViewById(R.id.image_switcher);
        mImageSwitcherView.setFactory(this);

        mImageSwitcherView.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left));
        mImageSwitcherView.setOutAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.slide_out_right));

        mGalleryView = (Gallery) findViewById(R.id.gallery);
        mGalleryView.setAdapter(new ImageAdapter(this, mPicUrls));
        mGalleryView.setOnItemClickListener(this);
        mGalleryView.setSelection(position);

        // findViewById(R.id.send).setOnClickListener(this);
        // findViewById(R.id.cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.send:
            break;
        case R.id.cancel:
            finish();
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        new AsyncImageViewTask(mImageSwitcherView).execute(mPicUrls[position]);
    }

    @Override
    public void onPostExecute(JSONObject result) {
        Toast.makeText(this, R.string.sent, Toast.LENGTH_SHORT).show();
        finish();
    }

    /*
     * (non-Javadoc)
     * @see android.widget.ViewSwitcher.ViewFactory#makeView()
     */
    @Override
    public View makeView() {
        ImageView v = new ImageView(this);
        v.setLayoutParams(getLayoutParams());
        // v.setVisibility(View.GONE);
        // content.addView(v);

        return v;
    }

    /**
     * @return
     */
    protected LayoutParams getLayoutParams() {
        if (mLayoutParams == null) {
            mLayoutParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        return mLayoutParams;
    }

    /**
     * @author shuaqiu Jun 4, 2013
     */
    public static class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private String[] mPicUrls;

        public ImageAdapter(Context context, String[] picUrls) {
            mContext = context;

            mPicUrls = new String[picUrls.length];
            int i = 0;
            for (String picUrl : picUrls) {
                mPicUrls[i++] = picUrl.replace("original", "thumbnail");
            }
        }

        @Override
        public int getCount() {
            return mPicUrls.length;
        }

        @Override
        public Object getItem(int position) {
            return mPicUrls[position];
        }

        @Override
        public long getItemId(int position) {
            return mPicUrls[position].hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(mContext);

            // imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new Gallery.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            // imageView.setBackgroundResource(itemBackground);

            new AsyncImageViewTask(imageView).execute(mPicUrls[position]);

            return imageView;
        }
    }

}
