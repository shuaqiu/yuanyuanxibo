/**
 * 
 */
package com.shuaqiu.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.os.Bundle;
import android.util.Log;

import com.shuaqiu.yuanyuanxibo.API;

/**
 * @author shuaqiu May 2, 2013
 */
public class HttpUtil {
    static final String TAG = "http";

    private static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

    /**
     * @param value
     * @return
     */
    public static URL parseUrl(String value) {
        try {
            if (!value.startsWith("http")) {
                value = API.API + value;
            }
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
                Log.d(TAG, "the value is array:" + value);
                int length = Array.getLength(value);
                for (int i = 0; i < length; i++) {
                    p.append(param(key, Array.get(value, i)));
                }
            } else {
                p.append(param(key, value));
            }
        }
        if (p.length() > 1) {
            return p.substring(1);
        }

        return p.toString();
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public static String param(String key, Object value) {
        if (value == null) {
            return "";
        }
        try {
            return "&" + key + "="
                    + URLEncoder.encode(value.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return "";
    }

    public static Bundle decodeUrl(URL url) {
        if (url == null) {
            return new Bundle();
        }
        Bundle b = decodeUrl(url.getQuery());
        b.putAll(decodeUrl(url.getRef()));
        return b;
    }

    public static Bundle decodeUrl(String s) {
        Bundle params = new Bundle();
        if (s != null) {
            String array[] = s.split("&");
            for (String parameter : array) {
                String v[] = parameter.split("=");
                try {
                    params.putString(URLDecoder.decode(v[0], "UTF-8"),
                            URLDecoder.decode(v[1], "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }
        return params;
    }

    /**
     * @param url
     * @param conn
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private static void setupHttps(HttpURLConnection conn)
            throws NoSuchAlgorithmException, KeyManagementException {
        if (conn instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) conn;

            httpsConn.setHostnameVerifier(new TrustAllHostnameVerifier());

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null,
                    new TrustManager[] { new TrustAllX509TrustManager() },
                    new SecureRandom());
            httpsConn.setSSLSocketFactory(sslContext.getSocketFactory());
        }
    }

    /**
     * @param conn
     * @param args
     * @return
     * @throws IOException
     */
    private static void writeRequestParam(HttpURLConnection conn, Bundle args)
            throws IOException {
        OutputStream out = null;
        try {
            out = conn.getOutputStream();
            String param = param(args);
            out.write(param.getBytes());
            out.flush();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        } finally {
            StreamUtil.close(out);
        }
    }

    /**
     * @param conn
     * @param handler
     * @param in
     * @return
     * @throws IOException
     * @throws SSLPeerUnverifiedException
     */
    private static <Result> Result readResponse(HttpURLConnection conn,
            InputStreamHandler<Result> handler) throws IOException {
        InputStream in = null;
        try {
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                String msg = conn.getResponseCode() + ":"
                        + conn.getResponseMessage();
                Log.d(TAG, msg);

                Map<String, List<String>> headerFields = conn.getHeaderFields();
                Log.d(TAG, headerFields.toString());
                return null;
            }

            in = conn.getInputStream();

            String contentEncoding = conn.getContentEncoding();
            if (contentEncoding != null && contentEncoding.indexOf("gzip") > -1) {
                in = new GZIPInputStream(in);
            }
            return handler.handle(in);

        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            StreamUtil.close(in);
        }
        return null;
    }

    public static <Result> Result httpGet(URL url,
            InputStreamHandler<Result> handler) {
        HttpURLConnection conn = null;
        try {
            Log.d(TAG, url.toString());
            conn = (HttpURLConnection) url.openConnection();
            // HTTPS
            setupHttps(conn);

            return readResponse(conn, handler);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    public static String httpGet(URL url) {
        return httpGet(url, new StringHandler());
    }

    public static String httpGet(String urlStr, Bundle params) {
        URL url = parseUrl(urlStr + "?" + param(params));
        if (url == null) {
            return null;
        }
        return httpGet(url, new StringHandler());
    }

    /**
     * @param url
     * @param file
     * @return
     */
    public static boolean downloadTo(URL url, File file) {
        Boolean isDownloaded = httpGet(url, new FileHandler(file));
        if (isDownloaded == null) {
            return false;
        }
        return isDownloaded;
    }

    public static <Result> Result httpPost(URL url, Bundle args,
            InputStreamHandler<Result> handler) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            // HTTPS
            setupHttps(conn);

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type", CONTENT_TYPE_FORM);

            conn.connect();

            writeRequestParam(conn, args);

            return readResponse(conn, handler);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    public static String httpPost(URL url, Bundle args) {
        return httpPost(url, args, new StringHandler());
    }

    /**
     * @author shuaqiu May 3, 2013
     */
    private static final class TrustAllX509TrustManager implements
            X509TrustManager {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
    }

    /**
     * @author shuaqiu May 3, 2013
     */
    private static final class TrustAllHostnameVerifier implements
            HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}
