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

import com.shuaqiu.common.UpdatableTuple;
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

        mZoomControls = (ZoomControls) findViewById(R.id.zoom_controller);
        mZoomControls.setOnZoomInClickListener(new ZoomInClickListener(
                mImageSwitcherView, 1.25));
        mZoomControls.setOnZoomOutClickListener(new ZoomInClickListener(
                mImageSwitcherView, 0.8));

        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
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
        Log.d(TAG, "" + v.getId());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        setSwitcherSelection(position);
    }

    private void setSwitcherSelection(int position) {
        View nextView = mImageSwitcherView.getNextView();
        nextView.setTag(null);
        nextView.setTag(ZoomInClickListener.SCALE_SIZE_KEY, null);
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

    private static class ZoomInClickListener implements OnClickListener {

        private static final int SCALE_SIZE_KEY = R.id.thumbnail_pic;

        private ImageSwitcher target;
        private double scale;

        private ZoomInClickListener(ImageSwitcher target, double scale) {
            this.target = target;
            // 设置图片放大比例
            this.scale = scale;
        }

        @Override
        public void onClick(View v) {
            ImageView imageView = (ImageView) target.getCurrentView();

            UpdatableTuple<Bitmap, Float> scaleInfo = getScaleInfo(imageView);
            Bitmap bitmap = scaleInfo.getValue1();

            int bmpWidth = bitmap.getWidth();
            int bmpHeight = bitmap.getHeight();

            float scaleSize = scaleInfo.getValue2();
            // 产生新的大小但Bitmap对象
            Matrix matrix = new Matrix();
            matrix.postScale(scaleSize, scaleSize);

            Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth,
                    bmpHeight, matrix, true);

            imageView.setImageBitmap(resizeBmp);
        }

        @SuppressWarnings("unchecked")
        private UpdatableTuple<Bitmap, Float> getScaleInfo(ImageView imageView) {
            Object tag = imageView.getTag();
            if (tag != null) {
                UpdatableTuple<Bitmap, Float> tuple = (UpdatableTuple<Bitmap, Float>) tag;
                float scaleSize = (float) (tuple.getValue2() * scale);
                return tuple.update(tuple.getValue1(), scaleSize);
            }

            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            return new UpdatableTuple<Bitmap, Float>(bitmap, (float) scale);
        }
    }
}