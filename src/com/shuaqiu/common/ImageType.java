/**
 * 
 */
package com.shuaqiu.common;

import java.io.File;

import com.shuaqiu.yuanyuanxibo.StateKeeper;

/**
 * 圖片類型
 * 
 * @author shuaqiu 2013-6-13
 * 
 */
public enum ImageType {
    /** 表情圖片 */
    EMOTION("emotion"),

    /** 頭像 */
    PROFILE("profile"),

    /** 微博圖片 */
    PIC("pic");

    private String folder;

    /**
     * @param folder
     */
    private ImageType(String folder) {
        this.folder = folder;
    }

    public File getFolder() {
        return new File(StateKeeper.pictureDir, folder);
    }
}
