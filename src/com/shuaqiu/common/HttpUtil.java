/**
 * 
 */
package com.shuaqiu.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.os.Bundle;
import android.util.Log;

/**
 * @author shuaqiu May 2, 2013
 */
public class HttpUtil {
    private static final String TAG = "http";

    private static final int HTTP_OK = 200;

    /**
     * @param value
     * @return
     */
    public static URL parseUrl(String value) {
        try {
            return new URL(value);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    public static String param(Bundle args) {
        StringBuilder p = new StringBuilder();

        Set<String> keys = args.keySet();
        for (String key : keys) {
            Object value = args.get(key);
            if (value instanceof Iterable) {
                Log.d(TAG, "the value is iterable:" + value);
                Iterable<?> iterable = (Iterable<?>) value;
                for (Object v : iterable) {
                    p.append(param(key, v));
                }
            } else if (value.getClass().isArray()) {
            } else {
                p.append(param(key, value));
            }
        }

        return p.toString();
    }

    /**
     * @param key
     * @param value
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String param(String key, Object value) {
        if (value == null) {
            return "";
        }
        try {
            return key + "=" + URLEncoder.encode(value.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return "";
    }

    public static <Result> Result httpGet(URL url,
            InputStreamHandler<Result> handler) {
        HttpURLConnection conn = null;
        InputStream in = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            if (conn.getResponseCode() == HTTP_OK) {
                in = conn.getInputStream();
                return handler.handle(in);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            StreamUtil.close(in);
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    /**
     * @param url
     * @param file
     * @return
     */
    public static boolean downloadTo(URL url, File file) {
        return httpGet(url, new FileHandler(file));
    }

    private static class FileHandler implements InputStreamHandler<Boolean> {
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

    public static <Result> Result httpPost(URL url, Bundle args,
            InputStreamHandler<Result> handler) {
        HttpURLConnection conn = null;
        InputStream in = null;
        OutputStream out = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            if (url.getProtocol().equals("https")) {
                HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
                httpsConn.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null,
                        new TrustManager[] { new X509TrustManager() {

                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }

                            @Override
                            public void checkServerTrusted(
                                    X509Certificate[] chain, String authType)
                                    throws CertificateException {
                            }

                            @Override
                            public void checkClientTrusted(
                                    X509Certificate[] chain, String authType)
                                    throws CertificateException {
                            }
                        } }, new SecureRandom());
                httpsConn.setSSLSocketFactory(sslContext.getSocketFactory());
            }
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);

            conn.connect();

            out = conn.getOutputStream();
            out.write(param(args).getBytes());
            out.flush();
            StreamUtil.close(out);

            // if (conn.getResponseCode() == HTTP_OK) {
            in = conn.getInputStream();
            return handler.handle(in);
            // }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            StreamUtil.close(in);
            StreamUtil.close(out);
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }
}
