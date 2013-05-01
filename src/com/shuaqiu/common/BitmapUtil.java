/**
 * 
 */
package com.shuaqiu.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

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
        URL url = parseUrl(value);
        if (url == null) {
            return null;
        }

        String cacheFilename = SecurityUtil.md5(value);

        File cacheFile = new File(cacheFolder, cacheFilename);
        if (!cacheFile.exists()) {
            boolean isDownloaded = downloadTo(url, cacheFile);
            if (!isDownloaded) {
                return null;
            }
        }
        return BitmapFactory.decodeFile(cacheFile.getPath());
    }

    /**
     * @param url
     * @param file
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    private static boolean downloadTo(URL url, File file) {
        HttpURLConnection conn = null;
        InputStream in = null;
        OutputStream out = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            if (conn.getResponseCode() == 200) {
                in = conn.getInputStream();

                if (!cacheFolder.exists()) {
                    cacheFolder.mkdirs();
                }
                out = new FileOutputStream(file);

                StreamUtil.tranfer(in, out);
            }
            return true;
        } catch (Exception e) {
            Log.e("bitmap", e.getMessage(), e);
            return false;
        } finally {
            StreamUtil.close(out);
            StreamUtil.close(in);
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * @param value
     * @return
     */
    private static URL parseUrl(String value) {
        try {
            return new URL(value);
        } catch (Exception e) {
            Log.e("bitmap", e.getMessage(), e);
        }
        return null;
    }

}
