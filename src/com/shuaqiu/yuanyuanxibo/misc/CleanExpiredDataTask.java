/**
 * 
 */
package com.shuaqiu.yuanyuanxibo.misc;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

import android.content.Context;
import android.util.Log;

import com.shuaqiu.common.ImageType;
import com.shuaqiu.common.TimeHelper;
import com.shuaqiu.yuanyuanxibo.content.StatusHelper;
import com.shuaqiu.yuanyuanxibo.content.StatusHelper.Column;

/**
 * @author shuaqiu Jul 21, 2013
 */
public class CleanExpiredDataTask implements Runnable {

    private static final String TAG = "CleanExpiredDataTask";

    private static final int EXPIRED_DAY = 10 * 24 * 60 * 60 * 1000;

    private Context mContext;

    /**
     * @param mContext
     */
    public CleanExpiredDataTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void run() {
        cleanStatusData();
        cleanStatusImage();
    }

    private void cleanStatusData() {
        StatusHelper helper = new StatusHelper(mContext);
        helper.openForWrite();
        try {
            Log.d(TAG, "start to delete expired status data");
            int deleted = helper.delete(Column.created_at.name() + " < ?",
                    new String[] { getExpiredCreateDay() });
            Log.d(TAG, deleted + " expired status data have been deleted");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            helper.close();
        }
    }

    private void cleanStatusImage() {
        File baseFolder = getPictureBaseFolder();
        File[] folders = baseFolder.listFiles(new ExpiredFolderFilter());

        for (File folder : folders) {
            Log.d(TAG, "handle picture folder --> " + folder.getName());
            File[] expiredFiles = folder.listFiles(new ExpiredFileFilter());
            Log.d(TAG, "handle picture folder --> find " + expiredFiles.length
                    + " expired pictures");

            for (File file : expiredFiles) {
                file.delete();
            }

            if (folder.list().length == 0) {
                Log.d(TAG,
                        "handle picture folder --> all picture deleted, delete folder too");
                // there are not file in this folder
                folder.delete();
            }
        }
    }

    /**
     * @return
     */
    private File getPictureBaseFolder() {
        return ImageType.PIC.getFolder("clean").getParentFile();
    }

    /**
     * @return
     */
    private static String getExpiredCreateDay() {
        long expiredDay = System.currentTimeMillis() - EXPIRED_DAY;
        return expiredDay + "";
    }

    private static String getExpiredDay() {
        long expiredDay = System.currentTimeMillis() - EXPIRED_DAY;
        return TimeHelper.getDayFormat().format(expiredDay);
    }

    /**
     * @param time
     * @return
     */
    private static boolean isExpired(long time) {
        return System.currentTimeMillis() - time > EXPIRED_DAY;
    }

    /**
     * @author shuaqiu Jul 22, 2013
     */
    private static final class ExpiredFolderFilter implements FilenameFilter {
        private final String expiredDay;

        /**
         * @param expiredDay
         */
        private ExpiredFolderFilter() {
            expiredDay = getExpiredDay();
        }

        @Override
        public boolean accept(File file, String filename) {
            return filename.compareTo(expiredDay) < 0;
        }
    }

    /**
     * @author shuaqiu Jul 22, 2013
     */
    private static final class ExpiredFileFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            return isExpired(pathname.lastModified());
        }
    }
}
