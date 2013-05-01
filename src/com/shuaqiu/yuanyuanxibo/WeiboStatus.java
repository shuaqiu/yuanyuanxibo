/**
 * 
 */
package com.shuaqiu.yuanyuanxibo;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author shuaqiu 2013-4-30
 * 
 */
public class WeiboStatus {

    private static WeiboStatus instance = null;

    public static WeiboStatus getInstance() {
        if (instance == null) {
            instance = new WeiboStatus();
        }
        return instance;
    }

    private WeiboStatus() {
    }

    public JSONArray getStatus() {
        try {
            JSONObject json = new JSONObject(getJson());
            JSONArray array = json.getJSONArray("statuses");

            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new JSONArray();

    }

    private String getJson() throws IOException {
        InputStream stream = getClass().getResourceAsStream("/testdata.json");
        byte[] buf = new byte[stream.available()];
        stream.read(buf);
        return new String(buf);
    }
}
