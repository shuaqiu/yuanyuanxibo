/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.status;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.shuaqiu.common.ImageType;
import com.shuaqiu.common.util.BitmapUtil;
import com.shuaqiu.yuanyuanxibo.status.StatusBinder.ImageQuality;
import com.shuaqiu.yuanyuanxibo.status.StatusBinder.Type;

/**
 * 監聽新微博的下載, 下載微博中的圖片
 * 
 * @author shuaqiu Jun 19, 2013
 */
public class ImageTaskReceiver extends BroadcastReceiver {

    private static final String TAG = "ImageTaskReceiver";

    private Context mContext;
    /** 用來解析微博中的圖片, 直接用現成的代碼 */
    private JsonStatusBinder mBinder;

    private Handler mHandler;

    public ImageTaskReceiver(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        mBinder = new JsonStatusBinder(mContext, null);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Set<ImageQuality> qualities = getQualities();
        if (qualities.size() == 0) {
            return;
        }

        JSONArray statuses = getStatuses(intent);

        Collection<String> pics = resolvePics(statuses, qualities);

        mHandler.post(new ImageTask(pics));
    }

    /**
     * 獲取微博列表和微博詳細內容中顯示的圖片質量
     * 
     * @return
     */
    private Set<ImageQuality> getQualities() {
        Set<ImageQuality> qualities = new HashSet<ImageQuality>(2);
        qualities.add(mBinder.getImageQuality(Type.LIST));
        qualities.add(mBinder.getImageQuality(Type.DETAIL));

        qualities.remove(ImageQuality.NONE);
        return qualities;
    }

    /**
     * 根據intent 中的數據, 返回微博列表
     * 
     * @param intent
     * @return
     */
    private JSONArray getStatuses(Intent intent) {
        String result = intent.getStringExtra("result");
        if (result == null) {
            return new JSONArray();
        }

        JSONObject json = null;
        try {
            json = new JSONObject(result);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            return new JSONArray();
        }

        JSONArray statuses = json.optJSONArray("statuses");
        if (statuses == null) {
            return new JSONArray();
        }

        return statuses;
    }

    /**
     * 解析微博中的圖片
     * 
     * @param statuses
     *            微博列表
     * @param qualities
     *            圖片質量
     * @return
     */
    private Collection<String> resolvePics(JSONArray statuses,
            Set<ImageQuality> qualities) {
        Set<String> pics = new HashSet<String>();
        for (int i = 0; i < statuses.length(); i++) {
            JSONObject status = statuses.optJSONObject(i);

            String[] optPics = mBinder.optPics(status, ImageQuality.THUMBNAIL);
            if (optPics == null || optPics.length == 0) {
                // 微博沒有圖片
                continue;
            }
            for (ImageQuality quality : qualities) {
                if (quality == ImageQuality.NONE) {
                    continue;
                }
                if (quality == ImageQuality.THUMBNAIL) {
                    pics.addAll(Arrays.asList(optPics));
                    continue;
                }
                for (String pic : optPics) {
                    String targetPic = pic.replace(
                            ImageQuality.THUMBNAIL.keywork, quality.keywork);
                    pics.add(targetPic);
                }
            }
        }
        return pics;
    }

    /**
     * 圖片的下載任務
     * 
     * @author shuaqiu Jun 19, 2013
     */
    private static class ImageTask implements Runnable {
        private static final int INTERVEL = 500;

        private Collection<String> pics;

        private ImageTask(Collection<String> pics) {
            this.pics = pics;
        }

        @Override
        public void run() {
            for (String pic : pics) {
                try {
                    BitmapUtil.cacheBitmap(ImageType.PIC, pic);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                try {
                    Thread.sleep(INTERVEL);
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }

    }
}