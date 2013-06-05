/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.ViewSwitcher.ViewFactory;

import com.shuaqiu.common.util.ViewUtil;
import com.shuaqiu.yuanyuanxibo.R;

/**
 * @author shuaqiu Jun 3, 2013
 */
public class PictureViewerActivity extends Activity implements OnClickListener,
        ViewFactory, OnItemClickListener {

    private static final String TAG = "PictureViewerActivity";

    private static final String BIT_PIC_REG = String.format("(%s)|(%s)",
            StatusBinder.PIC_LARGE, StatusBinder.PIC_BMIDDLE);

    private Gallery mGalleryView;
    private ImageSwitcher mImageSwitcherView;

    private FrameLayout.LayoutParams mLayoutParams;

    private String[] mPicUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_picture_viewer);

        Intent intent = getIntent();
        mPicUrls = intent.getStringArrayExtra("pics");
        int position = intent.getIntExtra("position", 0);

        Log.d(TAG, Arrays.deepToString(mPicUrls));
        Log.d(TAG, "selected -> " + position);

        mImageSwitcherView = (ImageSwitcher) findViewById(R.id.image_switcher);
        mImageSwitcherView.setFactory(this);

        mImageSwitcherView.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in));
        mImageSwitcherView.setOutAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out));

        setSwitcherSelection(position);

        mGalleryView = (Gallery) findViewById(R.id.gallery);
        mGalleryView.setAdapter(new ImageAdapter(this, toThumbnail(mPicUrls)));
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
        setSwitcherSelection(position);
    }

    private void setSwitcherSelection(int position) {
        ViewUtil.setImage(mImageSwitcherView, mPicUrls[position]);
    }

    @Override
    public View makeView() {
        ImageView v = new ImageView(this);
        v.setLayoutParams(getLayoutParams());
        v.setImageResource(R.drawable.ic_launcher);

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

    private String[] toThumbnail(String[] picUrls) {
        String[] thumbnailUrls = new String[picUrls.length];
        int i = 0;
        for (String picUrl : picUrls) {
            String url = picUrl.replaceFirst(BIT_PIC_REG,
                    StatusBinder.PIC_THUMBNAIL);
            thumbnailUrls[i++] = url;
        }
        return thumbnailUrls;
    }

    /**
     * @author shuaqiu Jun 4, 2013
     */
    public static class ImageAdapter extends BaseAdapter {

        private Context mContext;
        private String[] mThumbnailUrls;

        private Gallery.LayoutParams mLayoutParams;

        public ImageAdapter(Context context, String[] thumbnailUrls) {
            mContext = context;
            mThumbnailUrls = thumbnailUrls;
        }

        @Override
        public int getCount() {
            return mThumbnailUrls.length;
        }

        @Override
        public Object getItem(int position) {
            return mThumbnailUrls[position];
        }

        @Override
        public long getItemId(int position) {
            return mThumbnailUrls[position].hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(mContext);
            // imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            // imageView.setBackgroundResource(itemBackground);
            imageView.setLayoutParams(getLayoutParams());
            imageView.setImageResource(R.drawable.ic_launcher);

            ViewUtil.setImage(imageView, mThumbnailUrls[position]);
            return imageView;
        }

        public Gallery.LayoutParams getLayoutParams() {
            if (mLayoutParams == null) {
                mLayoutParams = new Gallery.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            return mLayoutParams;
        }
    }

}
