/**
 * 
 */
package com.shuaqiu.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

public class FileHandler implements InputStreamHandler<Boolean> {
    private static final String TAG = "file";

    private File file = null;

    public FileHandler(File file) {
        this.file = file;
    }

    @Override
    public Boolean handle(InputStream in) throws IOException {
        OutputStream out = null;
        try {
            File parentFile = file.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                parentFile.mkdirs();
            }
            out = new FileOutputStream(file);

            StreamUtil.tranfer(in, out);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            StreamUtil.close(out);
        }
        return false;
    }
}