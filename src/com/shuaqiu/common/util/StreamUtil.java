/**
 * 
 */
package com.shuaqiu.common.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

/**
 * @author shuaqiu May 1, 2013
 */
public class StreamUtil {

    private static final String TAG = "stream";

    public static void tranfer(InputStream in, OutputStream out)
            throws IOException {
        byte[] buf = new byte[5120];
        int readed = in.read(buf);
        while (readed != -1) {
            out.write(buf, 0, readed);
            readed = in.read(buf);
        }
    }

    public static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
