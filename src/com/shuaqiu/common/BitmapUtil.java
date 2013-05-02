/**
 * 
 */
package com.shuaqiu.common;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

/**
 * @author shuaqiu 2013-4-30
 */
public class BitmapUtil {

    private static final File cacheFolder = new File(
            Environment.getExternalStorageDirectory(), "yyxibo/imgcache");

    /**
     * @param value
     * @return
     * @throws IOException
     */
    public static Bitmap fromUrl(String value) throws IOException {
        if (value == null || "".equals(value.trim())) {
            return null;
        }
        URL url = HttpUtil.parseUrl(value);
        if (url == null) {
            return null;
        }

        String cacheFilename = SecurityUtil.md5(value);

        File cacheFile = new File(cacheFolder, cacheFilename);
        if (!cacheFile.exists()) {
            boolean isDownloaded = HttpUtil.downloadTo(url, cacheFile);
            if (!isDownloaded) {
                return null;
            }
        }
        return BitmapFactory.decodeFile(cacheFile.getPath());
    }

}
