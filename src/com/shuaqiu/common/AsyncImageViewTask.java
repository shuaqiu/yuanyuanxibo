/**
 * 
 */
package com.shuaqiu.common;

import java.io.IOException;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

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
        mImageView.setVisibility(View.VISIBLE);
        if (result != null) {
            mImageView.setImageBitmap(result);
        }

        if (mProgressView != null) {
            mProgressView.setVisibility(View.GONE);
        }
    }
}
