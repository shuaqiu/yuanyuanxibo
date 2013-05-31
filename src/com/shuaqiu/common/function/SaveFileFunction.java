/**
 * 
 */
package com.shuaqiu.common.function;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

import com.shuaqiu.common.util.StreamUtil;

public class SaveFileFunction implements InputStreamFunction<Boolean> {
    private static final String TAG = "file";

    private File file = null;

    public SaveFileFunction(File file) {
        this.file = file;
    }

    @Override
    public Boolean apply(InputStream in) {
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