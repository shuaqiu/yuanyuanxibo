/**
 * 
 */
package com.shuaqiu.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Log;

/**
 * @author shuaqiu 2013-4-30
 */
public class SecurityUtil {
    private static final String TAG = "SecurityUtil";

    private static MessageDigest digester = null;

    private static void initDigester() {
        if (digester != null) {
            return;
        }
        try {
            digester = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static String md5(String data) {
        initDigester();
        digester.reset();
        digester.update(data.getBytes());
        byte[] digest = digester.digest();

        StringBuilder builder = new StringBuilder();
        for (byte b : digest) {
            builder.append(Integer.toHexString(b >> 4 & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
        }
        return builder.toString();
    }

}
