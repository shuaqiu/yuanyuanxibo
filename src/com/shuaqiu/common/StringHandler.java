/**
 * 
 */
package com.shuaqiu.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringHandler implements InputStreamHandler<String> {

    @Override
    public String handle(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        StringBuilder p = new StringBuilder();

        char[] buf = new char[5120];
        int readed = reader.read(buf);
        while (readed != -1) {
            p.append(buf, 0, readed);
            readed = reader.read(buf);
        }

        return p.toString();
    }
}