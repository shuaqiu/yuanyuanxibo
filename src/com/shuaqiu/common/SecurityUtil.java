/**
 * 
 */
package com.shuaqiu.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author shuaqiu 2013-4-30
 */
public class SecurityUtil {
    private static MessageDigest digester = null;

    static {
        try {
            digester = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String md5(String data) {
        digester.reset();
        digester.update(data.getBytes());
        byte[] digest = digester.digest();

        StringBuilder builder = new StringBuilder();
        for (byte b : digest) {
            builder.append(Integer.toHexString((b >> 4) & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
        }
        return builder.toString();
    }

}
