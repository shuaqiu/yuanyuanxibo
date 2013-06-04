/**
 * 
 */
package com.shuaqiu.common.task;

import java.io.IOException;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import com.shuaqiu.common.util.BitmapUtil;

/**
 * @author shuaqiu May 1, 2013
 */
public class AsyncImageViewTask extends AsyncTask<String, Integer, Bitmap> {

    private View mView;
    private View mProgressView;

    public AsyncImageViewTask(View view) {
        mView = view;
    }

    public AsyncImageViewTask(View imageView, View progressView) {
        mView = imageView;
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

    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            if (mView instanceof ImageView) {
                ((ImageView) mView).setImageBitmap(result);
            } else if (mView instanceof ImageSwitcher) {
                Context context = mView.getContext();
                Resources resources = context.getResources();
                BitmapDrawable drawable = new BitmapDrawable(resources, result);
                ((ImageSwitcher) mView).setImageDrawable(drawable);
            }
            mView.setVisibility(View.VISIBLE);
        }

        if (mProgressView != null) {
            mProgressView.setVisibility(View.GONE);
        }
    }
}
