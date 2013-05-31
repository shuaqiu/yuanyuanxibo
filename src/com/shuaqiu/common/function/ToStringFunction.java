/**
 * 
 */
package com.shuaqiu.common.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

import com.shuaqiu.common.util.StreamUtil;

public class ToStringFunction implements InputStreamFunction<String> {

    private static final String TAG = "StringHandler";

    private static ToStringFunction instance = null;

    public static ToStringFunction getInstance() {
        if (instance == null) {
            instance = new ToStringFunction();
        }
        return instance;
    }

    private ToStringFunction() {
    }

    @Override
    public String apply(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        StringBuilder p = new StringBuilder();

        char[] buf = new char[5120];
        try {
            int readed = reader.read(buf);
            while (readed != -1) {
                p.append(buf, 0, readed);
                readed = reader.read(buf);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            StreamUtil.close(reader);
        }

        return p.toString();
    }
}