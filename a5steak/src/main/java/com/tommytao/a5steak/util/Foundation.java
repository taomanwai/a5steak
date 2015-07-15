package com.tommytao.a5steak.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetFileDescriptor;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Responsible for providing application context, debug mode flag and HTTP get
 * functions for extended Manager
 *
 * @author tommytao
 */
public class Foundation {

    public static interface OnHttpGetJSONListener {

        public void onComplete(JSONObject response);

    }

    public static interface OnHttpGetFileListener {

        public void onDownloaded(File file);

        public void onDownloading(int percentage);

    }

    public static interface OnHttpGetByteArrayListener {

        public void onDownloaded(byte[] ba);

        public void onDownloading(int percentage);

    }

    public static interface OnHttpPostByteArrayListener {

        public void onComplete(boolean succeed);


    }

    public static interface OnHttpPostJSONRecvJSONListener {

        public void onComplete(JSONObject response);


    }

    public static interface OnPlayListener {

        public void onStart();

        public void onComplete(boolean succeed);


    }

    public final int DEFAULT_CONNECT_TIMEOUT_IN_MS = 10000;
    public final int DEFAULT_READ_TIMEOUT_IN_MS = DEFAULT_CONNECT_TIMEOUT_IN_MS;
    public final int BUFFER_SIZE_IN_BYTE = 1024;
    public final String BOUNDARY_OF_HTTP_POST_BYTE_ARRAY = "&&3rewfwefwfewfhufrbewfuweriwefr"; // Not 0xKhTmLbOuNdArY

    protected boolean debugMode = true;

    protected Context appContext;

    public boolean init(Context context) {

        if (isInitialized()) {
            log("base: init rejected: already initialized");
            return false;
        }

        log("base: init");

        this.appContext = context.getApplicationContext();

        return true;

    }


    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isInitialized() {
        return (appContext != null);
    }

    public Foundation() {

        log("base: create");

    }


    // === log ===


    protected void log(String msg) {

        Log.d(Foundation.class.getName(), msg);

    }

    // === http ===

    protected int calculateDownloadPercentage(int finished, int totalSize) {

        double result = (double) finished / totalSize * 100;

        if (result > 100)
            result = 100;

        return (int) result;

    }

    protected void httpGetFile(final String link, final String directory, final String fileName, final OnHttpGetFileListener listener) {

        log("base: file: " + link);

        new AsyncTask<String, Integer, File>() {

            @Override
            protected void onProgressUpdate(Integer... values) {

                log("base: progress: " + values[0] + "% for link: " + link);

                if (listener != null)
                    listener.onDownloading(values[0]);

            }

            @Override
            protected File doInBackground(String... links) {

                if (links.length != 1) {
//                    Log.e(Foundation.class.getName(), "base: ERR: " + "Number of link is not 1 but " + links.length);
                    log("base: ERR: " + "Number of link is not 1 but " + links.length);
                    return null;
                }

                Object[] objs = link2HttpGetConnectionAndInputStream(links[0], 1);

                if (objs == null) {
//                    Log.e(Foundation.class.getName(), "base: ERR: " + "ConnectionAndInputStream err for " + links[0]);
                    log("base: ERR: " + "ConnectionAndInputStream err for " + links[0]);
                    return null;
                }

                HttpURLConnection conn = (HttpURLConnection) objs[0];
                InputStream is = (InputStream) objs[1];

                File file = null;
                file = buildFile(directory, fileName);

                if (file == null) {
//                    Log.e(Foundation.class.getName(), "base: ERR: " + "file init err for " + directory + File.separator + fileName);
                    log("base: ERR: " + "file init err for " + directory + File.separator + fileName);
                    return null;
                }

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);

                    byte[] buffer = new byte[BUFFER_SIZE_IN_BYTE];
                    int readLength = 0;
                    int countOfRead = 0;
                    int connContentLength = conn.getContentLength();

                    while ((readLength = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, readLength);
                        countOfRead++;
                        publishProgress(calculateDownloadPercentage(BUFFER_SIZE_IN_BYTE * countOfRead, connContentLength));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log("base: ERR: " + "read http or save file err");
                    file = null;

                } finally {

                    try {
                        conn.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        is.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                return file;

            }

            @Override
            protected void onPostExecute(File file) {

                if (listener != null)
                    listener.onDownloaded(file);

            }

        }.execute(link);

    }

    protected File buildFile(String directory, String fileName) {

        boolean isSucceed = false;

        File path = new File(directory);
        isSucceed = path.exists();
        if (!isSucceed)
            isSucceed = path.mkdirs();

        if (!isSucceed)
            return null;

        File file = new File(path, fileName);
        if (file.exists())
            isSucceed = file.delete();

        return isSucceed ? file : null;
    }

    protected Object[] link2HttpGetConnectionAndInputStream(final String link, final int maxNoOfRetries) {

        if (link == null || link.isEmpty()) {
//            Log.e(Foundation.class.getName(), "base: ERR: " + "link is null or empty");
            log("base: ERR: " + "link is null or empty");
            return new Object[]{null, null};
        }

        URL url = null;
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (url == null) {
//            Log.e(Foundation.class.getName(), "base: ERR: " + "malformed URL for " + link);
            log("base: ERR: " + "malformed URL for " + link);
            return new Object[]{null, null};
        }

        HttpURLConnection conn = null;
        InputStream is = null;

        boolean hasException = false;
        try {

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // will make httpGetFile not work, reason unknown
            // conn.setDoOutput(true);
            conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_IN_MS);
            conn.setReadTimeout(DEFAULT_READ_TIMEOUT_IN_MS);

            int countOfRetries = 0;

            while (is == null && countOfRetries < maxNoOfRetries) {

                if (countOfRetries >= 1) {
                    log("base: RETRY (" + (countOfRetries + 1) + "): " + link);
                }

                try {

                    is = new BufferedInputStream(conn.getInputStream());

                } catch (Exception e) {
                    e.printStackTrace();
//                    Log.e(Foundation.class.getName(), "base: ERR: " + "get input stream error for " + link);
                    log("base: ERR: " + "get input stream error for " + link);
                }
                countOfRetries++;
            }

        } catch (Exception e) {
            e.printStackTrace();
//            Log.e(Foundation.class.getName(), "base: ERR: " + "setup (not connect) connection error");
            log("base: ERR: " + "setup (not connect) connection error");

            hasException = true;

            try {
                is.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            is = null;

            try {
                conn.disconnect();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            conn = null;

        }

        return hasException ? null : new Object[]{conn, is};

    }


    protected void httpGetJSON(final String link, final int maxNoOfRetries, final OnHttpGetJSONListener listener) {

        log("base: json: " + link);

        new AsyncTask<String, Void, JSONObject>() {

            @Override
            protected JSONObject doInBackground(String... links) {

                if (links.length != 1) {
//                    Log.e(Foundation.class.getName(), "base: ERR: " + "Number of link is not 1 but " + links.length);
                    log("base: ERR: " + "Number of link is not 1 but " + links.length);
                    return null;
                }

                URL url = null;
                try {
                    url = new URL(link);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (url == null) {
//                    Log.e(Foundation.class.getName(), "base: ERR: " + "URL is invalid for " + link);
                    log("base: ERR: " + "URL is invalid for " + link);
                    return null;
                }

                JSONObject jObj = null;
                HttpURLConnection conn = null;

                try {

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_IN_MS);
                    conn.setReadTimeout(DEFAULT_READ_TIMEOUT_IN_MS);

                    int countOfRetries = 0;
                    InputStream in = null;

                    while (in == null && countOfRetries < maxNoOfRetries) {

                        if (countOfRetries >= 1) {
                            log("base: RETRY (" + (countOfRetries + 1) + "): " + link);
                        }

                        try {
                            in = new BufferedInputStream(conn.getInputStream());

                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                        countOfRetries++;
                    }

                    if (in != null) {

                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                        StringBuilder sb = new StringBuilder();
                        String line = "";
                        while ((line = reader.readLine()) != null)
                            sb.append(line);

                        try {

                            jObj = new JSONObject(sb.toString());
                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    if (conn != null)
                        conn.disconnect();
                }

                return jObj;

            }

            @Override
            protected void onPostExecute(JSONObject result) {

                log("base: json_result: " + "for link: " + link + " result: " + result);

                if (listener != null)
                    listener.onComplete(result);

            }

        }.execute(link);

    }

    protected byte[] fileLink2ByteArray(String fileLink) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE_IN_BYTE];
        int sizeOfRead = -1;
        boolean readSucceed = false;
        FileInputStream fis = null;
        readSucceed = false;
        try {
            fis = new FileInputStream(fileLink);
            while ((sizeOfRead = fis.read(buffer)) != -1)
                bos.write(buffer, 0, sizeOfRead);
            readSucceed = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (readSucceed) ? bos.toByteArray() : new byte[0];

    }

    protected void httpGetByteArray(final String link, final OnHttpGetByteArrayListener listener) {

        log("base: byte_array: " + link);

        new AsyncTask<String, Integer, byte[]>() {

            @Override
            protected void onProgressUpdate(Integer... values) {

                log("base: progress: " + values[0] + "% for link: " + link);

                if (listener != null)
                    listener.onDownloading(values[0]);

            }

            @Override
            protected byte[] doInBackground(String... links) {

                if (links.length != 1) {
//                    Log.e(Foundation.class.getName(), "base: ERR: " + "Number of link is not 1 but " + links.length);
                    log("base: ERR: " + "Number of link is not 1 but " + links.length);
                    return null;
                }

                Object[] objs = link2HttpGetConnectionAndInputStream(links[0], 1);

                if (objs == null) {
//                    Log.e(Foundation.class.getName(), "base: ERR: " + "ConnectionAndInputStream err for " + links[0]);
                    log("base: ERR: " + "ConnectionAndInputStream err for " + links[0]);
                    return null;
                }

                HttpURLConnection conn = (HttpURLConnection) objs[0];
                InputStream is = (InputStream) objs[1];

                byte[] ba = new byte[0];

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try {

                    byte[] buffer = new byte[BUFFER_SIZE_IN_BYTE];
                    int readLength = 0;
                    int countOfRead = 0;
                    int connContentLength = conn.getContentLength();

                    while ((readLength = is.read(buffer)) != -1) {
                        bos.write(buffer, 0, readLength);
                        countOfRead++;
                        publishProgress(calculateDownloadPercentage(BUFFER_SIZE_IN_BYTE * countOfRead, connContentLength));

                    }

                    ba = bos.toByteArray();

                } catch (Exception e) {
                    e.printStackTrace();
//                    Log.e(Foundation.class.getName(), "base: ERR: " + "read http or save file err");
                    log("base: ERR: " + "read http or save file err");
                    ba = new byte[0];

                } finally {

                    try {
                        conn.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        is.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        bos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                return ba;

            }

            @Override
            protected void onPostExecute(byte[] ba) {

                if (listener != null)
                    listener.onDownloaded(ba);

            }

        }.execute(link);

    }

    protected void triggerHttpPostByteArrayListener(final OnHttpPostByteArrayListener listener, final boolean succeed) {

        if (listener == null)
            return;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                listener.onComplete(succeed);

            }
        });

    }

    protected void triggerHttpPostJSONListener(final OnHttpPostJSONRecvJSONListener listener, final JSONObject response) {

        if (listener == null)
            return;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                listener.onComplete(response);

            }
        });

    }


    protected void httpPostJSONRecvJSON(final String link, final JSONObject jObj, final OnHttpPostJSONRecvJSONListener listener) {

        final String BOUNDARY = BOUNDARY_OF_HTTP_POST_BYTE_ARRAY;
        final String TWO_HYPHENS = "--";
        final String NEW_LINE = "\r\n";

        new Thread() {
            @Override
            public void run() {
                super.run();

                URL url = null;
                try {
                    url = new URL(link);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (url == null) {
                    triggerHttpPostJSONListener(listener, null);
                    return;
                }

                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) url.openConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (connection == null) {
                    triggerHttpPostJSONListener(listener, null);
                    return;
                }

                JSONObject response = null;
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_IN_MS);
                connection.setReadTimeout(DEFAULT_READ_TIMEOUT_IN_MS);
                boolean succeedOfSettingPost = false;
                try {
                    connection.setRequestMethod("POST");
                    succeedOfSettingPost = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!succeedOfSettingPost) {
                    triggerHttpPostJSONListener(listener, null);
                    return;
                }
                connection.setDoOutput(true);
                connection.setDoInput(true);

                boolean succeedOfConnection = false;
                try {
                    connection.connect();
                    succeedOfConnection = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!succeedOfConnection) {
                    triggerHttpPostJSONListener(listener, null);
                    return;
                }

                DataOutputStream os = null;
                try {

                    os = new DataOutputStream(connection.getOutputStream());
                    os.writeBytes(jObj.toString());
                    os.flush();

                    // Ensure we got the HTTP 200 response code
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream in = null;
                        try {
                            in = new BufferedInputStream(connection.getInputStream());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (in != null) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            StringBuilder sb = new StringBuilder();
                            String line = "";
                            while ((line = reader.readLine()) != null)
                                sb.append(line);
                            try {
                                response = new JSONObject(sb.toString());
                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        os.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                triggerHttpPostJSONListener(listener, response);


            }
        }.start();

    }


    protected void httpPostByteArray(final String link, final Map<String, Object> params, final byte[] imgData, final Map<String, Object> imgDataParams, final OnHttpPostByteArrayListener listener) {

        final String BOUNDARY = BOUNDARY_OF_HTTP_POST_BYTE_ARRAY;
        final String TWO_HYPHENS = "--";
        final String NEW_LINE = "\r\n";

        new Thread() {
            @Override
            public void run() {
                super.run();

                URL url = null;
                try {
                    url = new URL(link);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (url == null) {
                    triggerHttpPostByteArrayListener(listener, false);
                    return;
                }

                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) url.openConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (connection == null) {
                    triggerHttpPostByteArrayListener(listener, false);
                    return;
                }


                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
                connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_IN_MS);
                connection.setReadTimeout(DEFAULT_READ_TIMEOUT_IN_MS);
                boolean succeedOfSettingPost = false;
                try {
                    connection.setRequestMethod("POST");
                    succeedOfSettingPost = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!succeedOfSettingPost) {
                    triggerHttpPostByteArrayListener(listener, false);
                    return;
                }
                connection.setDoOutput(true);
                connection.setDoInput(true);


                StringBuffer sbRequestBody = new StringBuffer();

                // parameters part
                if (params != null && !params.isEmpty()) {
                    for (Map.Entry<String, Object> entity : params.entrySet()) {
                        sbRequestBody.append(TWO_HYPHENS).append(BOUNDARY).append(NEW_LINE);
                        sbRequestBody.append("Content-Disposition: form-data; ");
                        sbRequestBody.append("name=\"" + entity.getKey() + "\"").append(NEW_LINE).append(NEW_LINE);
                        sbRequestBody.append((String) entity.getValue()).append(NEW_LINE);
                    }
                }

                // media part
                sbRequestBody.append(TWO_HYPHENS).append(BOUNDARY).append(NEW_LINE);
//        sbRequestBody.append("Content-Disposition: form-data; name=\"").append(imageKey).append("\"; filename=\"dummy.jpg\"").append(NEW_LINE);
                sbRequestBody.append("Content-Disposition: form-data");
                if (imgDataParams != null || !imgDataParams.isEmpty()) {
                    sbRequestBody.append("; ");
                    int i = 0;
                    for (Map.Entry<String, Object> entity : imgDataParams.entrySet()) {
                        sbRequestBody.append(entity.getKey() + "=\"").append(entity.getValue()).append("\"");
                        if (i != (imgDataParams.size() - 1))
                            sbRequestBody.append("; ");
                        i++;
                    }
                }
                sbRequestBody.append(NEW_LINE).append(NEW_LINE);

//        sbRequestBody.append("Content-Type: image/jpeg").append(NEW_LINE);
//        sbRequestBody.append("Content-Transfer-Encoding: binary").append(NEW_LINE).append(NEW_LINE);

                boolean succeedOfConnection = false;
                try {
                    connection.connect();
                    succeedOfConnection = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!succeedOfConnection) {
                    triggerHttpPostByteArrayListener(listener, false);
                    return;
                }

                DataOutputStream os = null;
                boolean succeedOfOutput = false;
                try {

                    os = new DataOutputStream(connection.getOutputStream());
                    os.writeBytes(sbRequestBody.toString());

                    Log.d("rtemp", "sbRequestBody_t: " + sbRequestBody);

                    // write image data
                    os.write(imgData);
                    os.writeBytes(NEW_LINE);
                    // final boundary
                    os.writeBytes(TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + NEW_LINE);
                    os.flush();

                    // Ensure we got the HTTP 200 response code
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
                        succeedOfOutput = true;

//                    succeedOfOutput = true;

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        os.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                triggerHttpPostByteArrayListener(listener, succeedOfOutput);


            }
        }.start();


    }

    // === MD5 ===
    protected byte[] hexStrToByteArray(String input) {

        int inputLength = input.length();
        byte[] result = new byte[inputLength / 2];
        for (int i = 0; i < inputLength; i += 2)
            result[i / 2] = (byte) ((Character.digit(input.charAt(i), 16) << 4) + Character.digit(input.charAt(i + 1), 16));

        return result;

    }

    protected String byteArrayToHexStr(byte[] bytes) {

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; ++i)
            sb.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100).substring(1, 3));

        return sb.toString();

    }

    protected String genHash(String input, String algorithm) {

        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance(algorithm);
            byte[] array = md.digest(input.getBytes());

            return byteArrayToHexStr(array);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    protected String md5(String input) {

        return genHash(input, "MD5");

    }

    protected String sha1(String input) {
        return genHash(input, "SHA-1");
    }

    // === res id 2 name ===
    protected String resId2Name(int resId, boolean fullName) {

        String idName = appContext.getResources().getResourceName(resId);

        int dividerIndex = -1;
        if (fullName)
            dividerIndex = idName.indexOf(':');
        else
            dividerIndex = idName.indexOf('/');

        if (dividerIndex == -1)
            return "";

        return idName.substring(dividerIndex + 1, idName.length());

    }

    // === file utils ===

    protected boolean deleteFolder(File folder) {

        return deleteFolderExcept(folder, null);

    }

    protected boolean deleteFolderExcept(File folder, File exceptFileOrFolder) {

        if (folder.isDirectory()) {
            String[] children = folder.list();
            for (int i = 0; i < children.length; i++) {
                File fileOrFolder = new File(folder, children[i]);
                if (exceptFileOrFolder != null
                        && fileOrFolder.getAbsolutePath().equals(
                        exceptFileOrFolder.getAbsolutePath()))
                    continue;
                if (fileOrFolder.isDirectory()) {
                    deleteFolderExcept(fileOrFolder, exceptFileOrFolder);
                } else {
                    fileOrFolder.delete();
                }
            }
            folder.delete();
        }

        return (!folder.exists());

    }


    // === Check inside China or not ===


    protected boolean isInChinaWgs84(double lat, double lng) {

        if (Double.isNaN(lat) || Double.isNaN(lng))
            return false;

        if (lng < 72.004 || lng > 137.8347)

            return false;

        if (lat < 0.8293 || lat > 55.8271)

            return false;

        return true;

    }

    protected boolean isInChinaGcj02(double lat, double lng) {

        Location wgs84Location = this.gcj02ToWgs84(lat, lng);

        if (wgs84Location == null)
            return false;

        return this.isInChinaWgs84(wgs84Location.getLatitude(), wgs84Location.getLongitude());

    }

    // === Baidu coordination conversion ===


    protected Location bd09ToGcj02(double bdLat, double bdLng) {


        if (Double.isNaN(bdLat) || Double.isNaN(bdLng))
            return null;

        double d = bdLng - 0.0066499999999999997D;
        double d1 = bdLat - 0.0060000000000000001D;
        double d2 = Math.sqrt(d * d + d1 * d1) - 2.0000000000000002E-05D * Math.sin(52.359877559829883D * d1);
        double d3 = Math.atan2(d1, d) - 3.0000000000000001E-06D * Math.cos(52.359877559829883D * d);
        double d4 = d2 * Math.cos(d3);


        Location result = new Location("");
        result.setLatitude(d2 * Math.sin(d3));
        result.setLongitude(d4);
        return result;


    }

    protected Location gcj02ToBd09(double gcjLat, double gcjLng) {

        if (Double.isNaN(gcjLat) || Double.isNaN(gcjLng))
            return null;

        double d = gcjLng;
        double d1 = gcjLat;
        double d2 = Math.sqrt(d * d + d1 * d1) + 2.0000000000000002E-05D * Math.sin(52.359877559829883D * d1);
        double d3 = Math.atan2(d1, d) + 3.0000000000000001E-06D * Math.cos(52.359877559829883D * d);
        double d4 = 0.0064999999999999997D + d2 * Math.cos(d3);


        Location result = new Location("");
        result.setLatitude(0.0060000000000000001D + d2 * Math.sin(d3));
        result.setLongitude(d4);
        return result;

    }

    protected Location bd09ToWgs84(double bdLat, double bdLng) {

        Location gcj02Location = bd09ToGcj02(bdLat, bdLng);

        if (gcj02Location == null)
            return null;

        return gcj02ToWgs84(gcj02Location.getLatitude(), gcj02Location.getLongitude());

    }

    protected Location wgs84ToBd09(double wgsLat, double wgsLng) {

        Location gcj02Location = wgs84ToGcj02(wgsLat, wgsLng);

        if (gcj02Location == null)
            return null;

        return gcj02ToBd09(gcj02Location.getLatitude(), gcj02Location.getLongitude());


    }


    // === GCJ02 conversion (using U algorithm) ===

    protected double latitudeOffsetForWgs84ToGcj02(double d, double d1) {
        return -100D + 2D * d + 3D * d1 + d1 * (0.20000000000000001D * d1) + d1 * (0.10000000000000001D * d) + 0.20000000000000001D * Math.sqrt(Math.abs(d)) + (2D * (20D * Math.sin(3.1415926535897931D * (6D * d)) + 20D * Math.sin(3.1415926535897931D * (2D * d)))) / 3D + (2D * (20D * Math.sin(3.1415926535897931D * d1) + 40D * Math.sin(3.1415926535897931D * (d1 / 3D)))) / 3D + (2D * (160D * Math.sin(3.1415926535897931D * (d1 / 12D)) + 320D * Math.sin((3.1415926535897931D * d1) / 30D))) / 3D;
    }

    protected double longitudeOffsetForWgs84ToGcj02(double d, double d1) {
        return 300D + d + 2D * d1 + d * (0.10000000000000001D * d) + d1 * (0.10000000000000001D * d) + 0.10000000000000001D * Math.sqrt(Math.abs(d)) + (2D * (20D * Math.sin(3.1415926535897931D * (6D * d)) + 20D * Math.sin(3.1415926535897931D * (2D * d)))) / 3D + (2D * (20D * Math.sin(3.1415926535897931D * d) + 40D * Math.sin(3.1415926535897931D * (d / 3D)))) / 3D + (2D * (150D * Math.sin(3.1415926535897931D * (d / 12D)) + 300D * Math.sin(3.1415926535897931D * (d / 30D)))) / 3D;
    }

    protected Location gcj02ToWgs84(double gcjLat, double gcjLng) {

        Location locationForDeltaCalculation = wgs84ToGcj02(gcjLat, gcjLng);

        if (locationForDeltaCalculation == null)
            return null;

        double d = locationForDeltaCalculation.getLatitude() - gcjLat;
        double d1 = locationForDeltaCalculation.getLongitude() - gcjLng;

        Location result = new Location("");
        result.setLatitude(gcjLat - d);
        result.setLongitude(gcjLng - d1);
        return result;

    }

    protected Location wgs84ToGcj02(double wgsLat, double wgsLng) {

        if (Double.isNaN(wgsLat) || Double.isNaN(wgsLng))
            return null;

        double d = Math.toRadians(wgsLat);
        double d1 = 1.0D - 0.0066934216229659433D * Math.sin(d) * Math.sin(d);
        double d2 = Math.sqrt(d1);
        double d3 = (180D * latitudeOffsetForWgs84ToGcj02(wgsLng - 105D, wgsLat - 35D)) / (3.1415926535897931D * (6335552.7170004258D / (d1 * d2)));
        double d4 = (180D * longitudeOffsetForWgs84ToGcj02(wgsLng - 105D, wgsLat - 35D)) / (3.1415926535897931D * ((6378245D / d2) * Math.cos(d)));

        Location result = new Location("");
        result.setLatitude(d3 + wgsLat);
        result.setLongitude(d4 + wgsLng);
        return result;

    }


//    // ==== GCJ02 conversion ===
//
//
//    protected Location gcj02ToWgs84(double gcjLat, double gcjLng) {
//
//        if (Double.isNaN(gcjLat) || Double.isNaN(gcjLng)) {
//            return null;
//        }
//
//        Location result = new Location("");
//
////        if (!isInChinaWgs84(gcjLat, gcjLng)) {
////            result.setLatitude(gcjLat);
////            result.setLongitude(gcjLng);
////            log("lbsamap: " + "gcj02ToWgs84 outside China - output directly - no conversion");
////            return result;
////        }
//
//        Location d = delta(gcjLat, gcjLng);
//
//        result.setLatitude(gcjLat - d.getLatitude());
//        result.setLongitude(gcjLng - d.getLongitude());
//
//
//        return result;
//
//    }
//
//
//    protected Location wgs84ToGcj02(double lat, double lng) {
//
//        if (Double.isNaN(lat) || Double.isNaN(lng))
//            return null;
//
//        Location result = new Location("");
//
//        double mgLat;
//        double mgLon;
//
////        if (!isInChinaWgs84(lat, lng)) {
////            mgLat = lat;
////            mgLon = lng;
////        }
//
//        double dLat = transformLat(lng - 105.0, lat - 35.0);
//
//        double dLon = transformLon(lng - 105.0, lat - 35.0);
//
//        double radLat = lat / 180.0 * Math.PI;
//
//        double magic = Math.sin(radLat);
//
//        magic = 1 - ee * magic * magic;
//
//        double sqrtMagic = Math.sqrt(magic);
//
//        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * Math.PI);
//
//        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * Math.PI);
//
//        mgLat = lat + dLat;
//
//        mgLon = lng + dLon;
//
//        result.setLatitude(mgLat);
//
//        result.setLongitude(mgLon);
//
//        return result;
//    }
//
//    private double a = 6378245.0;
//
//    private double ee = 0.00669342162296594323;
//
//    private Location delta(double lat, double lng) {
//
//        if (Double.isNaN(lat) || Double.isNaN(lng)) {
//            return null;
//        }
//
//        double dLat = transformLat(lng - 105.0, lat - 35.0);
//        double dLng = transformLon(lng - 105.0, lat - 35.0);
//        double radLat = lat / 180.0 * Math.PI;
//        double magic = Math.sin(radLat);
//        magic = 1 - ee * magic * magic;
//        double sqrtMagic = Math.sqrt(magic);
//        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * Math.PI);
//        dLng = (dLng * 180.0) / (a / sqrtMagic * Math.cos(radLat) * Math.PI);
//
//        Location result = new Location("");
//        result.setLatitude(dLat);
//        result.setLongitude(dLng);
//
//        return result;
//
//    }
//
//    private double transformLat(double x, double y) {
//
//        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
//
//        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
//
//        ret += (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0 * Math.PI)) * 2.0 / 3.0;
//
//        ret += (160.0 * Math.sin(y / 12.0 * Math.PI) + 320 * Math.sin(y * Math.PI / 30.0)) * 2.0 / 3.0;
//
//        return ret;
//
//    }
//
//    private double transformLon(double x, double y) {
//
//        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
//
//        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
//
//        ret += (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0 * Math.PI)) * 2.0 / 3.0;
//
//        ret += (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x / 30.0 * Math.PI)) * 2.0 / 3.0;
//
//        return ret;
//
//    }

    // ==== Media player  ===

    protected MediaPlayer mediaPlayer = new MediaPlayer();

    protected MediaPlayer getMediaPlayer() {

        return mediaPlayer;

    }

    protected MediaPlayer getMediaPlayerFromRaw(int resId) {
        return MediaPlayer.create(appContext, resId);
    }


    protected void releaseMediaPlayer() {

        try {
            mediaPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    protected void playRaw(final int resId, final OnPlayListener listener) {

        releaseMediaPlayer();

        try {
            mediaPlayer = getMediaPlayerFromRaw(resId);

            getMediaPlayer().start();

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onStart();

                }
            });

            getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {

                    try {
                        mediaPlayer.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (listener != null)
                        listener.onComplete(true);


                }
            });

        } catch (Exception e) {
            e.printStackTrace();

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onComplete(false);

                }
            });
        }


    }

    protected void playAssets(final String fileName, final OnPlayListener listener) {

        releaseMediaPlayer();

        try {

            mediaPlayer = new MediaPlayer();
            AssetFileDescriptor descriptor = ((ContextWrapper) appContext).getAssets().openFd(fileName);
            getMediaPlayer().setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();
            getMediaPlayer().prepare();
            getMediaPlayer().start();

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onStart();

                }
            });

            getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    try {
                        mediaPlayer.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (listener != null)
                        listener.onComplete(true);


                }
            });


        } catch (Exception e) {
            e.printStackTrace();

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onComplete(false);

                }
            });
        }

    }


    protected void playUrl(final String url, final OnPlayListener listener) {

        releaseMediaPlayer();

        try {

            mediaPlayer = new MediaPlayer();
            getMediaPlayer().setAudioStreamType(AudioManager.STREAM_MUSIC);
            getMediaPlayer().setDataSource(url);
            getMediaPlayer().setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {

                    try {
                        mediaPlayer.start();
                        if (listener != null)
                            listener.onStart();
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (listener != null)
                            listener.onComplete(false);
                    }

                }
            });
            getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    try {
                        mediaPlayer.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (listener != null)
                        listener.onComplete(true);
                }
            });
            getMediaPlayer().prepareAsync();

        } catch (Exception e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onComplete(false);

                }
            });
        }

    }

    // === Google API for Work ===
    protected static class UrlSigner {

        // Note: Generally, you should store your private key someplace safe
        // and read them into your code

        private static String keyString = "YOUR_PRIVATE_KEY";

        // The URL shown in these examples must be already
        // URL-encoded. In practice, you will likely have code
        // which assembles your URL from user or web service input
        // and plugs those values into its parameters.
        private static String urlString = "YOUR_URL_TO_SIGN";

        // This variable stores the binary key, which is computed from the
        // string
        // (Base64) key
        private static byte[] key;

        public UrlSigner(String keyString) throws IOException {
            // Convert the key from 'web safe' base 64 to binary
            keyString = keyString.replace('-', '+');
            keyString = keyString.replace('_', '/');
            System.out.println("Key: " + keyString);
            key = Base64.decode(keyString, Base64.DEFAULT);

        }

        public String signRequest(String path, String query) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException,
                URISyntaxException {

            // Retrieve the proper URL components to sign
            String resource = path + '?' + query;

            // Get an HMAC-SHA1 signing key from the raw key bytes
            SecretKeySpec sha1Key = new SecretKeySpec(key, "HmacSHA1");

            // Get an HMAC-SHA1 Mac instance and initialize it with the
            // HMAC-SHA1
            // key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(sha1Key);

            // compute the binary signature for the request
            byte[] sigBytes = mac.doFinal(resource.getBytes());

            // base 64 encode the binary signature
            String signature = Base64.encodeToString(sigBytes, Base64.DEFAULT);

            // convert the signature to 'web safe' base 64
            signature = signature.replace('+', '-');
            signature = signature.replace('/', '_');

            return resource + "&signature=" + signature;
        }

    }

    protected static String signToForWork(String link, String apiDomainForWork, String clientIdForWork, String cryptoForWork) {
        String result = link + "&client=" + clientIdForWork;
        UrlSigner signer;
        try {
            URL url = new URL(result);
            signer = new UrlSigner(cryptoForWork);
            String request = signer.signRequest(url.getPath(), url.getQuery());
            result = apiDomainForWork + request;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static final String API_DOMAIN_FOR_WORK = "https://maps.google.com";

    public static final String WORLD_BOUNDS = "-90,-180|90,180";

    protected String clientIdForWork = "";
    protected String cryptoForWork = "";

    protected String getClientIdForWork() {
        return clientIdForWork;
    }

    protected String getCryptoForWork() {
        return cryptoForWork;
    }

    // === Directions API ===
    protected ArrayList<Location> decodePolylinePointsToLocationList(String polylinePoints){
        ArrayList<Location> poly = new ArrayList<>();
        int index = 0, len = polylinePoints.length();
        int lat = 0, lng = 0;

        Location location = null;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = polylinePoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = polylinePoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            location = new Location("");
            location.setLatitude((double) lat / 1E5);
            location.setLongitude((double) lng / 1E5);
            poly.add(location);
        }

        return poly;
    }


}
