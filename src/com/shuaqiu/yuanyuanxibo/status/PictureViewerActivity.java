/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ZoomControls;

import com.shuaqiu.common.util.ViewUtil;
import com.shuaqiu.yuanyuanxibo.R;

/**
 * @author shuaqiu Jun 3, 2013
 */
public class PictureViewerActivity extends Activity implements OnClickListener,
        ViewFactory, OnItemClickListener {

    private static final String TAG = "PictureViewerActivity";

    private static final String BIT_PIC_REG = String.format("(%s)|(%s)",
            StatusBinder.ImageQuality.ORIGINAL.keywork,
            StatusBinder.ImageQuality.LARGE.keywork);

    private Gallery mGalleryView;
    private ImageSwitcher mSwitcherView;
    private ZoomControls mZoomControls;

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

        initImageSwitcher(position);
        initGallery(position);
        initZoomControls();

        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
    }

    private void initImageSwitcher(int position) {
        mSwitcherView = (ImageSwitcher) findViewById(R.id.image_switcher);
        mSwitcherView.setFactory(this);

        mSwitcherView.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in));
        mSwitcherView.setOutAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out));

        setSwitcherSelection(position);
    }

    private void initGallery(int position) {
        mGalleryView = (Gallery) findViewById(R.id.gallery);
        mGalleryView.setAdapter(new ImageAdapter(this, toThumbnail(mPicUrls)));
        mGalleryView.setOnItemClickListener(this);
        mGalleryView.setSelection(position);
    }

    private void initZoomControls() {
        mZoomControls = (ZoomControls) findViewById(R.id.zoom_controller);
        mZoomControls.setOnZoomInClickListener(new ZoomClickListener(
                mZoomControls, mSwitcherView, ZoomClickListener.IN_SCALE));
        mZoomControls.setOnZoomOutClickListener(new ZoomClickListener(
                mZoomControls, mSwitcherView, ZoomClickListener.OUT_SCALE));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.save:
            // TODO save image
            break;
        case R.id.back:
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
        // 為了圖片的縮放進行的設置, 在切換圖片前, 清除之前的設置狀態
        View nextView = mSwitcherView.getNextView();
        nextView.setTag(null);
        nextView.setTag(ZoomClickListener.SCALE_SIZE_KEY, null);

        // 設置圖片
        ViewUtil.setImage(mSwitcherView, mPicUrls[position]);

        if (mZoomControls != null) {
            mZoomControls.setIsZoomInEnabled(true);
            mZoomControls.setIsZoomOutEnabled(true);
        }
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

    /**
     * 獲取縮略圖的URL, 用於在Gallery 裡面進行顯示
     * 
     * @param picUrls
     * @return
     */
    private String[] toThumbnail(String[] picUrls) {
        String[] thumbnailUrls = new String[picUrls.length];
        int i = 0;
        for (String picUrl : picUrls) {
            String url = picUrl.replaceFirst(BIT_PIC_REG,
                    StatusBinder.ImageQuality.THUMBNAIL.keywork);
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

    private static class ZoomClickListener implements OnClickListener {

        static final int SCALE_SIZE_KEY = R.id.tag_scale_size;

        static final double OUT_SCALE = 0.8;
        static final double IN_SCALE = 1.25;

        static final double MAX_SCALE = 2;
        static final double MIN_SCALE = 0.5;

        private ZoomControls zoomControls;
        private ImageSwitcher target;
        private double scale;

        private ZoomClickListener(ZoomControls zoomControls,
                ImageSwitcher target, double scale) {
            this.zoomControls = zoomControls;
            this.target = target;
            // 设置图片放大比例
            this.scale = scale;
        }

        @Override
        public void onClick(View v) {
            ImageView imageView = (ImageView) target.getCurrentView();

            Bitmap bitmap = getOriginalBitmap(imageView);

            int bmpWidth = bitmap.getWidth();
            int bmpHeight = bitmap.getHeight();

            float scaleSize = getScaleSize(imageView);
            Log.d(TAG, "scale size --> " + scaleSize);

            // 产生新的大小Bitmap对象
            Matrix matrix = new Matrix();
            matrix.postScale(scaleSize, scaleSize);

            Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth,
                    bmpHeight, matrix, true);

            imageView.setImageBitmap(resizeBmp);

            if (scale < 1) {
                zoomControls.setIsZoomInEnabled(true);
                if (scale * scaleSize < MIN_SCALE) {
                    zoomControls.setIsZoomOutEnabled(false);
                }
            } else {
                zoomControls.setIsZoomOutEnabled(true);
                if (scale * scaleSize > MAX_SCALE) {
                    zoomControls.setIsZoomInEnabled(false);
                }
            }
        }

        private Bitmap getOriginalBitmap(ImageView v) {
            Object tag = v.getTag();
            if (tag != null) {
                return (Bitmap) tag;
            }
            BitmapDrawable drawable = (BitmapDrawable) v.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            v.setTag(bitmap);
            return bitmap;
        }

        private float getScaleSize(ImageView v) {
            Object tag = v.getTag(SCALE_SIZE_KEY);
            float scaleSize = 1F; // 默認是原始大小
            if (tag != null) {
                scaleSize = (Float) tag;
            }
            scaleSize *= scale;
            v.setTag(SCALE_SIZE_KEY, scaleSize);
            return scaleSize;
        }
    }
}
