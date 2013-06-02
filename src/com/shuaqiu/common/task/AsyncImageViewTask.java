/**
 * 
 */
package com.shuaqiu.common.task;

import java.io.IOException;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.shuaqiu.common.util.BitmapUtil;

/**
 * @author shuaqiu May 1, 2013
 */
public class AsyncImageViewTask extends AsyncTask<String, Integer, Bitmap> {

    private ImageView mImageView;
    private View mProgressView;

    public AsyncImageViewTask(ImageView imageView) {
        mImageView = imageView;
    }

    public AsyncImageViewTask(ImageView imageView, View progressView) {
        mImageView = imageView;
        mProgressView = progressView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            return BitmapUtil.fromUrl(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            mImageView.setImageBitmap(result);
            mImageView.setVisibility(View.VISIBLE);
        }

        if (mProgressView != null) {
            mProgressView.setVisibility(View.GONE);
        }
    }
}
