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
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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
import android.widget.ImageView.ScaleType;
import android.widget.ViewSwitcher.ViewFactory;
import android.widget.ZoomControls;

import com.shuaqiu.common.util.ViewUtil;
import com.shuaqiu.yuanyuanxibo.R;

/**
 * @author shuaqiu Jun 3, 2013
 */
public class PictureViewerActivity extends Activity implements OnClickListener,
        ViewFactory, OnItemClickListener, OnTouchListener {

    private static final String TAG = "PictureViewerActivity";

    private static final String BIT_PIC_REG = String.format("(%s)|(%s)",
            StatusBinder.ImageQuality.ORIGINAL.keywork,
            StatusBinder.ImageQuality.LARGE.keywork);

    private ViewHolder mHolder;

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

        initViewHolder();

        initImageSwitcher(position);
        initGallery(position);
        initZoomControls();

        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
    }

    private void initViewHolder() {
        if (mHolder != null) {
            return;
        }
        mHolder = new ViewHolder();

        mHolder.mSwitcher = (ImageSwitcher) findViewById(R.id.image_switcher);
        mHolder.mGallery = (Gallery) findViewById(R.id.gallery);
        mHolder.mZoomer = (ZoomControls) findViewById(R.id.zoom_controller);
    }

    private void initImageSwitcher(int position) {
        mHolder.mSwitcher.setFactory(this);
        mHolder.mSwitcher.setOnClickListener(this);
        mHolder.mSwitcher.setOnTouchListener(this);

        mHolder.mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in));
        mHolder.mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out));

        setSwitcherSelection(position);
    }

    private void initGallery(int position) {
        mHolder.mGallery.setAdapter(new ImageAdapter(this,
                toThumbnail(mPicUrls)));
        mHolder.mGallery.setOnItemClickListener(this);
        mHolder.mGallery.setSelection(position);
    }

    private void initZoomControls() {
        mHolder.mZoomer.setOnZoomInClickListener(new ZoomClickListener(mHolder,
                ZoomClickListener.IN_SCALE));
        mHolder.mZoomer.setOnZoomOutClickListener(new ZoomClickListener(
                mHolder, ZoomClickListener.OUT_SCALE));
        mHolder.mZoomer.hide();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.save:
            saveImage();
            break;
        case R.id.back:
            finish();
            break;
        case R.id.image_switcher:
            showZoom();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        setSwitcherSelection(position);
    }

    float x = 0;
    float y = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            x = event.getRawX();
            y = event.getRawY();
            break;
        case MotionEvent.ACTION_MOVE:
            float dx = x - event.getRawX();
            float dy = y - event.getRawY();
            View currentView = mHolder.mSwitcher.getCurrentView();
            float newScrollX = currentView.getScrollX() + dx;
            float newScrollY = currentView.getScrollY() + dy;
            Log.d(TAG, newScrollX + "," + newScrollY);
            Log.d(TAG,
                    (currentView.getWidth()) + "," + (currentView.getHeight()));
            Log.d(TAG, (mHolder.mSwitcher.getWidth()) + ","
                    + (mHolder.mSwitcher.getHeight()));
            if (newScrollX < 0) {
                newScrollX = currentView.getScrollX();
            }
            if (newScrollY < 0) {
                newScrollY = currentView.getScrollY();
            }
            if (newScrollX + currentView.getWidth() > mHolder.mSwitcher
                    .getWidth()) {
                newScrollX = mHolder.mSwitcher.getWidth()
                        - currentView.getWidth();
            }
            if (newScrollY + currentView.getHeight() > mHolder.mSwitcher
                    .getHeight()) {
                newScrollY = mHolder.mSwitcher.getHeight()
                        - currentView.getHeight();
            }
            currentView.scrollTo((int) newScrollX, (int) newScrollY);
            break;
        case MotionEvent.ACTION_UP:
            break;
        }
        return false;
    }

    @Override
    public View makeView() {
        ImageView v = new ImageView(this);
        v.setLayoutParams(getLayoutParams());
        v.setImageResource(R.drawable.ic_launcher);
        v.setScaleType(ScaleType.CENTER);
        v.setScrollContainer(true);

        return v;
    }

    private void setSwitcherSelection(int position) {
        // 為了圖片的縮放進行的設置, 在切換圖片前, 清除之前的設置狀態
        View nextView = mHolder.mSwitcher.getNextView();
        nextView.setTag(null);
        nextView.setTag(ZoomClickListener.SCALE_SIZE_KEY, null);

        // 設置圖片
        ViewUtil.setImage(mHolder.mSwitcher, mPicUrls[position]);

        if (mHolder.mZoomer != null) {
            mHolder.mZoomer.setIsZoomInEnabled(true);
            mHolder.mZoomer.setIsZoomOutEnabled(true);
        }
    }

    private void saveImage() {
        // TODO save image
    }

    private void showZoom() {
        if (mHolder.mZoomer.getVisibility() != View.VISIBLE) {
            // 只有在隱藏的情況下才調用顯示的方法
            mHolder.mZoomer.show();
        }

        // 設置隱藏的定時器
        hideZoomTime = SystemClock.uptimeMillis() + 3000;
        mHandler.postAtTime(hideZoomInstance, hideZoomTime + 10);
    }

    private long hideZoomTime;
    private Handler mHandler = new Handler();
    private Runnable hideZoomInstance = new Runnable() {
        @Override
        public void run() {
            if (SystemClock.uptimeMillis() > hideZoomTime) {
                // 只有當時間大於設置的隱藏時間時, 才進行隱藏
                mHolder.mZoomer.hide();
            }
        }
    };

    /**
     * @return
     */
    private LayoutParams getLayoutParams() {
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

    private static class ViewHolder {
        private Gallery mGallery;
        private ImageSwitcher mSwitcher;
        private ZoomControls mZoomer;
    }

    /**
     * @author shuaqiu Jun 4, 2013
     */
    private static class ImageAdapter extends BaseAdapter {

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

        private ViewHolder mHolder;
        private double scale;

        private ZoomClickListener(ViewHolder holder, double scale) {
            mHolder = holder;
            // 设置图片放大比例
            this.scale = scale;
        }

        @Override
        public void onClick(View v) {
            ImageView imageView = (ImageView) mHolder.mSwitcher
                    .getCurrentView();

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

            toggleZoomButtonEnable(scaleSize);

        }

        /**
         * @param scaleSize
         */
        protected void toggleZoomButtonEnable(float scaleSize) {
            if (scale < 1) {
                mHolder.mZoomer.setIsZoomInEnabled(true);
                if (scale * scaleSize < MIN_SCALE) {
                    mHolder.mZoomer.setIsZoomOutEnabled(false);
                }
            } else {
                mHolder.mZoomer.setIsZoomOutEnabled(true);
                if (scale * scaleSize > MAX_SCALE) {
                    mHolder.mZoomer.setIsZoomInEnabled(false);
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
