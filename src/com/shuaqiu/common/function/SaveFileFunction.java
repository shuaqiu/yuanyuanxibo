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
    private static final String TAG = "SaveFileFunction";

    private File mFile = null;

    public SaveFileFunction(File file) {
        mFile = file;
    }

    @Override
    public Boolean apply(InputStream in) {
        OutputStream out = null;
        try {
            File parentFile = mFile.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                boolean isMaked = parentFile.mkdirs();
                if (!isMaked) {
                    // 創建文件夾失敗, 可能是SD 卡沒有正常加載或者是不可寫
                    return false;
                }
            }
            out = new FileOutputStream(mFile);

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