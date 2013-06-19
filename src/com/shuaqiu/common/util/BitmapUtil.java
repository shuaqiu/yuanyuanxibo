/**
 * 
 */
package com.shuaqiu.common.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.shuaqiu.common.ImageType;

/**
 * @author shuaqiu 2013-4-30
 */
public class BitmapUtil {

    /**
     * 根據圖片URL 獲取bitmap 對象. 首先嘗試從緩存文件中獲取, 如果沒有, 則進行下載
     * 
     * @param type
     *            圖片類型
     * @param urlStr
     *            URL 地址
     * @return 返回獲取到的Bitmap. 如果類型或URL 地址錯誤, 或者不能下載指定URL 的圖片, 則返回null.
     * @throws IOException
     */
    public static Bitmap fromUrl(ImageType type, String urlStr)
            throws IOException {
        File cacheFile = cacheBitmap(type, urlStr);
        if (cacheFile == null) {
            return null;
        }
        cacheFile.setLastModified(System.currentTimeMillis());

        return BitmapFactory.decodeFile(cacheFile.getPath());
    }

    /**
     * @param type
     * @param urlStr
     * @return
     */
    public static File cacheBitmap(ImageType type, String urlStr) {
        if (type == null || urlStr == null || "".equals(urlStr.trim())) {
            return null;
        }
        URL url = HttpUtil.parseUrl(urlStr);
        if (url == null) {
            return null;
        }

        String cacheFilename = SecurityUtil.md5(urlStr);

        File cacheFile = new File(type.getFolder(), cacheFilename);
        if (!cacheFile.exists()) {
            boolean isDownloaded = HttpUtil.downloadTo(url, cacheFile);
            if (!isDownloaded) {
                return null;
            }
        }
        return cacheFile;
    }

}
