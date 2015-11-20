package com.tommytao.a5steak.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Responsible for providing application context, debug mode flag and HTTP get
 * functions for extended Manager
 *
 * @author tommytao
 */
public class Foundation implements SensorEventListener {

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

    public static interface OnHttpPostStringListener {

        public void onComplete(String responseStr);

    }


    public static final int DEFAULT_CONNECT_TIMEOUT_IN_MS = 2 * 60 * 1000; // 10000
    public static final int DEFAULT_READ_TIMEOUT_IN_MS = DEFAULT_CONNECT_TIMEOUT_IN_MS;
    public static final int DEFAULT_CONNECT_READ_TIMEOUT_IN_MS = DEFAULT_CONNECT_TIMEOUT_IN_MS + DEFAULT_READ_TIMEOUT_IN_MS;
    public static final int BUFFER_SIZE_IN_BYTE = 1024;
    public static final String BOUNDARY_OF_HTTP_POST_BYTE_ARRAY = "&&3rewfwefwfewfhyrjfhdncyuriwefr"; // Not &&3rewfwefwfewfhufrbewfuweriwefr NOT 0xKhTmLbOuNdArY

    protected boolean debugMode = true;

    protected Handler handler = new Handler(Looper.getMainLooper());

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

    // == tag ==
    private Object tag;

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    // == log ==


    protected void log(String msg) {

        Log.d(Foundation.class.getName(), msg);

    }

    // == http ==

    protected int calculateDownloadPercentage(int finished, int totalSize) {

        double result = (double) finished / totalSize * 100;

        if (result > 100)
            result = 100;

        return (int) result;

    }

    protected void httpGetFile(final String link, final int maxNoOfRetries, final String directory, final String fileName, final OnHttpGetFileListener listener) {

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
                    log("base: ERR: " + "Number of link is not 1 but " + links.length);
                    return null;
                }

                Object[] objs = link2HttpGetConnectionAndInputStream(links[0], maxNoOfRetries);

                if (objs == null) {
                    log("base: ERR: " + "ConnectionAndInputStream err for " + links[0]);
                    return null;
                }

                HttpURLConnection conn = (HttpURLConnection) objs[0];
                InputStream is = (InputStream) objs[1];

                File file = null;
                file = buildFile(directory, fileName);

                if (file == null) {
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

        }.executeOnExecutor(Executors.newCachedThreadPool(), link);

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
                    log("base: ERR: " + "get input stream error for " + link);
                }
                countOfRetries++;
            }

        } catch (Exception e) {
            e.printStackTrace();
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

        }.executeOnExecutor(Executors.newCachedThreadPool(), link);

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

    protected void httpGetByteArray(final String link, final int maxNoOfRetries, final OnHttpGetByteArrayListener listener) {

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
                    log("base: ERR: " + "Number of link is not 1 but " + links.length);
                    return null;
                }

                Object[] objs = link2HttpGetConnectionAndInputStream(links[0], maxNoOfRetries);

                if (objs == null) {
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

        }.executeOnExecutor(Executors.newCachedThreadPool(), link);

    }

    protected void triggerHttpPostByteArrayListener(final OnHttpPostByteArrayListener listener, final boolean succeed) {

        if (listener == null)
            return;

        handler.post(new Runnable() {
            @Override
            public void run() {

                listener.onComplete(succeed);

            }
        });

    }

    protected void triggerHttpPostStringListener(final OnHttpPostStringListener listener, final String responseStr) {

        if (listener == null)
            return;

        handler.post(new Runnable() {
            @Override
            public void run() {

                listener.onComplete(responseStr);

            }
        });

    }


    protected void httpPostString(final String link, final String dataStr, final HashMap<String, String> headers, final OnHttpPostStringListener listener) {


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
                    triggerHttpPostStringListener(listener, "");
                    return;
                }

                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) url.openConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (connection == null) {
                    triggerHttpPostStringListener(listener, "");
                    return;
                }

                String responseStr = "";
                // orig


                boolean succeedOfSettingPost = false;
                try {
                    connection.setRequestMethod("POST");
                    succeedOfSettingPost = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!succeedOfSettingPost) {
                    triggerHttpPostStringListener(listener, "");
                    return;
                }

                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json");

                if (headers != null && !headers.isEmpty()) {

                    Set<String> keys = headers.keySet();
                    for (String key : keys) {
                        connection.setRequestProperty(key, headers.get(key));
                    }

                }

                connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_IN_MS);
                connection.setReadTimeout(DEFAULT_READ_TIMEOUT_IN_MS);

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
                    triggerHttpPostStringListener(listener, "");
                    return;
                }

                DataOutputStream os = null;
                try {

                    os = new DataOutputStream(connection.getOutputStream());
                    os.writeBytes(dataStr);
                    os.flush();

                    // Ensure we got the HTTP 200 response code
                    int responseCode = -1;
                    try {
                        responseCode = connection.getResponseCode();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (responseCode == -1)
                        Log.d("rtemp", "failed");
                    else {
                        String msg = connection.getResponseMessage();
                        Log.d("rtemp", "succeed" + " " + connection.getResponseMessage());
                    }

                    if (responseCode == HttpURLConnection.HTTP_OK) {
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
                            responseStr = "" + sb;
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

                triggerHttpPostStringListener(listener, responseStr);


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
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

                connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_IN_MS);
                connection.setReadTimeout(DEFAULT_READ_TIMEOUT_IN_MS);

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

    // == MD5 ==
    protected byte[] hexRepresentationToByteArray(String input) {

        int inputLength = input.length();
        byte[] result = new byte[inputLength / 2];
        for (int i = 0; i < inputLength; i += 2)
            result[i / 2] = (byte) ((Character.digit(input.charAt(i), 16) << 4) + Character.digit(input.charAt(i + 1), 16));

        return result;

    }

    protected String byteArrayToHexRepresentation(byte[] bytes) {

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; ++i)
            sb.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100).substring(1, 3));

        return sb.toString();

    }

    protected String genHash(String input, String algorithm) {

        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance(algorithm);
            byte[] array = md.digest(input.getBytes());

            return byteArrayToHexRepresentation(array);
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


    // == file utils ==

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


    // == Check inside China or not ==

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

    // == Location sensor ==

    protected boolean isLatLngValid(double lat, double lng) {

        if (Double.isNaN(lat) || Double.isNaN(lng))
            return false;

        if (lat < -90 || lat > 90)
            return false;

        if (lng < -180 || lng > 180)
            return false;

        return true;
    }

    protected void goToLocationSourceSettings(Activity activity) {
        activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    protected float calculateDistanceInMeter(double lat1, double lng1, double lat2, double lng2) {

        float[] distance = new float[3];
        Location.distanceBetween(lat1, lng1, lat2, lng2, distance);
        return distance[0];

    }

    protected float calculateBearingInDegree(double lat1, double lng1, double lat2, double lng2) {

        float result = Float.NaN;

        if (Double.isNaN(lat1) ||
                Double.isNaN(lng1) ||
                Double.isNaN(lat2) ||
                Double.isNaN(lng2)) {
            return result;
        }

        if (lat1 == lat2 && lng1 == lng2) {
            return result;
        }


        Location startLocation = new Location("");
        Location endLocation = new Location("");
        startLocation.setLatitude(lat1);
        startLocation.setLongitude(lng1);
        endLocation.setLatitude(lat2);
        endLocation.setLongitude(lng2);

        result = startLocation.bearingTo(endLocation);

        result = (float) wholeToHalfCircleBearing(result);

        return result;

    }

    // == Baidu coordination conversion ==


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


    // == GCJ02 conversion (using simplified algorithm) ==

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


    // == Google API for Work ==
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

    public static final int WHOLE_WORLD_RADIUS_IN_METER = 20000000; // i.e. 20,000 km

    public static final String API_DOMAIN_FOR_WORK = "https://maps.google.com";


    protected String clientIdForWork = "";
    protected String cryptoForWork = "";

    protected String getClientIdForWork() {
        return clientIdForWork;
    }

    protected String getCryptoForWork() {
        return cryptoForWork;
    }

    // == Directions API ==
    protected ArrayList<Location> encodedPolylineToLocations(String encodedPolyline) {
        ArrayList<Location> poly = new ArrayList<>();
        int index = 0, len = encodedPolyline.length();
        int lat = 0, lng = 0;

        Location location = null;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encodedPolyline.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encodedPolyline.charAt(index++) - 63;
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

    // == OnReadingChangeListener ==
    public static interface OnReadingChangeListener {

        public void onReadingChanged(float x, float y, float z);

    }

    public final int DEFAULT_SENSOR_DELAY_LEVEL = SensorManager.SENSOR_DELAY_GAME;

    protected ArrayList<OnReadingChangeListener> onReadingChangeListenerList = new ArrayList<>();

    protected float lastKnownX = Float.NaN;
    protected float lastKnownY = Float.NaN;
    protected float lastKnownZ = Float.NaN;

    protected SensorManager sensorManager;
    protected Sensor sensor;

    protected void addOnReadingChangeListener(OnReadingChangeListener onReadingChangeListener) {

        onReadingChangeListenerList.add(onReadingChangeListener);

    }

    protected boolean removeOnReadingChangeListener(OnReadingChangeListener onReadingChangeListener) {

        return onReadingChangeListenerList.remove(onReadingChangeListener);

    }


    protected SensorManager getSensorManager() {

        if (sensorManager == null)
            sensorManager = (SensorManager) appContext.getSystemService(Context.SENSOR_SERVICE);

        return sensorManager;
    }

    protected float getLastKnownX() {
        return lastKnownX;
    }

    protected float getLastKnownY() {
        return lastKnownY;
    }

    protected float getLastKnownZ() {
        return lastKnownZ;
    }

    protected double getLastKnownMagnitude() {

        if (Float.isNaN(lastKnownX) ||
                Float.isNaN(lastKnownY) ||
                Float.isNaN(lastKnownZ))
            return Double.NaN;

        return Math.sqrt(lastKnownX * lastKnownX + lastKnownY * lastKnownY + lastKnownZ * lastKnownZ);

    }

    protected void connect() {
        getSensorManager().registerListener(this, getSensor(), DEFAULT_SENSOR_DELAY_LEVEL);
    }

    protected void disconnect() {
        getSensorManager().unregisterListener(this);
    }

    protected Sensor getSensor() {
        return sensor;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor != getSensor())
            return;

        lastKnownX = sensorEvent.values[0];
        lastKnownY = sensorEvent.values[1];
        lastKnownZ = sensorEvent.values[2];

        for (OnReadingChangeListener onReadingChangeListener : onReadingChangeListenerList) {
            onReadingChangeListener.onReadingChanged(lastKnownX, lastKnownY, lastKnownZ);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }

    // == Converter ==
    protected String resIdToName(int resId, boolean fullName) {

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

    protected Location latLngToLocation(double latitude, double longitude) {
        Location result = new Location("");

        result.setLatitude(latitude);
        result.setLongitude(longitude);

        return result;
    }

    protected String htmlToText(String html) {

        // TODO slow, should enhance
        return "" + Html.fromHtml(html);

    }

    protected HashMap<Character, Character> tcToScHashMap = new HashMap<>();
    protected HashMap<Character, Character> scToTcHashMap = new HashMap<>();

    /**
     * Ref: http://www.pupuliao.info/2012/09/java的utf-8-繁簡互轉的方法/
     */
    protected void buildChineseConversionHashMap() {

        tcToScHashMap.clear();
        scToTcHashMap.clear();

        final char[] UTF8T = "萬與醜專業叢東絲丟兩嚴喪個爿豐臨為麗舉麼義烏樂喬習鄉書買亂爭於虧雲亙亞產畝親褻嚲億僅從侖倉儀們價眾優夥會傴傘偉傳傷倀倫傖偽佇體餘傭僉俠侶僥偵側僑儈儕儂俁儔儼倆儷儉債傾傯僂僨償儻儐儲儺兒兌兗黨蘭關興茲養獸囅內岡冊寫軍農塚馮衝決況凍淨淒涼淩減湊凜幾鳳鳧憑凱擊氹鑿芻劃劉則剛創刪別剗剄劊劌剴劑剮劍剝劇勸辦務勱動勵勁勞勢勳猛勩勻匭匱區醫華協單賣盧鹵臥衛卻巹廠廳曆厲壓厭厙廁廂厴廈廚廄廝縣參靉靆雙發變敘疊葉號歎嘰籲後嚇呂嗎唚噸聽啟吳嘸囈嘔嚦唄員咼嗆嗚詠哢嚨嚀噝吒噅鹹呱響啞噠嘵嗶噦嘩噲嚌噥喲嘜嗊嘮啢嗩唕喚呼嘖嗇囀齧囉嘽嘯噴嘍嚳囁嗬噯噓嚶囑嚕劈囂謔團園囪圍圇國圖圓聖壙場阪壞塊堅壇壢壩塢墳墜壟壟壚壘墾坰堊墊埡墶壋塏堖塒塤堝墊垵塹墮壪牆壯聲殼壺壼處備複夠頭誇夾奪奩奐奮獎奧妝婦媽嫵嫗媯姍薑婁婭嬈嬌孌娛媧嫻嫿嬰嬋嬸媼嬡嬪嬙嬤孫學孿寧寶實寵審憲宮寬賓寢對尋導壽將爾塵堯尷屍盡層屭屜屆屬屢屨嶼歲豈嶇崗峴嶴嵐島嶺嶽崠巋嶨嶧峽嶢嶠崢巒嶗崍嶮嶄嶸嶔崳嶁脊巔鞏巰幣帥師幃帳簾幟帶幀幫幬幘幗冪襆幹並廣莊慶廬廡庫應廟龐廢廎廩開異棄張彌弳彎彈強歸當錄彠彥徹徑徠禦憶懺憂愾懷態慫憮慪悵愴憐總懟懌戀懇惡慟懨愷惻惱惲悅愨懸慳憫驚懼慘懲憊愜慚憚慣湣慍憤憒願懾憖怵懣懶懍戇戔戲戧戰戩戶紮撲扡執擴捫掃揚擾撫拋摶摳掄搶護報擔擬攏揀擁攔擰撥擇掛摯攣掗撾撻挾撓擋撟掙擠揮撏撈損撿換搗據撚擄摑擲撣摻摜摣攬撳攙擱摟攪攜攝攄擺搖擯攤攖撐攆擷擼攛擻攢敵斂數齋斕鬥斬斷無舊時曠暘曇晝曨顯晉曬曉曄暈暉暫曖劄術樸機殺雜權條來楊榪傑極構樅樞棗櫪梘棖槍楓梟櫃檸檉梔柵標棧櫛櫳棟櫨櫟欄樹棲樣欒棬椏橈楨檔榿橋樺檜槳樁夢檮棶檢欞槨櫝槧欏橢樓欖櫬櫚櫸檟檻檳櫧橫檣櫻櫫櫥櫓櫞簷檁歡歟歐殲歿殤殘殞殮殫殯毆毀轂畢斃氈毿氌氣氫氬氳彙漢汙湯洶遝溝沒灃漚瀝淪滄渢溈滬濔濘淚澩瀧瀘濼瀉潑澤涇潔灑窪浹淺漿澆湞溮濁測澮濟瀏滻渾滸濃潯濜塗湧濤澇淶漣潿渦溳渙滌潤澗漲澀澱淵淥漬瀆漸澠漁瀋滲溫遊灣濕潰濺漵漊潷滾滯灩灄滿瀅濾濫灤濱灘澦濫瀠瀟瀲濰潛瀦瀾瀨瀕灝滅燈靈災燦煬爐燉煒熗點煉熾爍爛烴燭煙煩燒燁燴燙燼熱煥燜燾煆糊溜愛爺牘犛牽犧犢強狀獷獁猶狽麅獮獰獨狹獅獪猙獄猻獫獵獼玀豬貓蝟獻獺璣璵瑒瑪瑋環現瑲璽瑉玨琺瓏璫琿璡璉瑣瓊瑤璦璿瓔瓚甕甌電畫暢佘疇癤療瘧癘瘍鬁瘡瘋皰屙癰痙癢瘂癆瘓癇癡癉瘮瘞瘺癟癱癮癭癩癬癲臒皚皺皸盞鹽監蓋盜盤瞘眥矓著睜睞瞼瞞矚矯磯礬礦碭碼磚硨硯碸礪礱礫礎硜矽碩硤磽磑礄確鹼礙磧磣堿镟滾禮禕禰禎禱禍稟祿禪離禿稈種積稱穢穠穭稅穌穩穡窮竊竅窯竄窩窺竇窶豎競篤筍筆筧箋籠籩築篳篩簹箏籌簽簡籙簀篋籜籮簞簫簣簍籃籬籪籟糴類秈糶糲粵糞糧糝餱緊縶糸糾紆紅紂纖紇約級紈纊紀紉緯紜紘純紕紗綱納紝縱綸紛紙紋紡紵紖紐紓線紺絏紱練組紳細織終縐絆紼絀紹繹經紿綁絨結絝繞絰絎繪給絢絳絡絕絞統綆綃絹繡綌綏絛繼綈績緒綾緓續綺緋綽緔緄繩維綿綬繃綢綯綹綣綜綻綰綠綴緇緙緗緘緬纜緹緲緝縕繢緦綞緞緶線緱縋緩締縷編緡緣縉縛縟縝縫縗縞纏縭縊縑繽縹縵縲纓縮繆繅纈繚繕繒韁繾繰繯繳纘罌網羅罰罷羆羈羥羨翹翽翬耮耬聳恥聶聾職聹聯聵聰肅腸膚膁腎腫脹脅膽勝朧腖臚脛膠脈膾髒臍腦膿臠腳脫腡臉臘醃膕齶膩靦膃騰臏臢輿艤艦艙艫艱豔艸藝節羋薌蕪蘆蓯葦藶莧萇蒼苧蘇檾蘋莖蘢蔦塋煢繭荊薦薘莢蕘蓽蕎薈薺蕩榮葷滎犖熒蕁藎蓀蔭蕒葒葤藥蒞蓧萊蓮蒔萵薟獲蕕瑩鶯蓴蘀蘿螢營縈蕭薩蔥蕆蕢蔣蔞藍薊蘺蕷鎣驀薔蘞藺藹蘄蘊藪槁蘚虜慮虛蟲虯蟣雖蝦蠆蝕蟻螞蠶蠔蜆蠱蠣蟶蠻蟄蛺蟯螄蠐蛻蝸蠟蠅蟈蟬蠍螻蠑螿蟎蠨釁銜補襯袞襖嫋褘襪襲襏裝襠褌褳襝褲襇褸襤繈襴見觀覎規覓視覘覽覺覬覡覿覥覦覯覲覷觴觸觶讋譽謄訁計訂訃認譏訐訌討讓訕訖訓議訊記訒講諱謳詎訝訥許訛論訩訟諷設訪訣證詁訶評詛識詗詐訴診詆謅詞詘詔詖譯詒誆誄試詿詩詰詼誠誅詵話誕詬詮詭詢詣諍該詳詫諢詡譸誡誣語誚誤誥誘誨誑說誦誒請諸諏諾讀諑誹課諉諛誰諗調諂諒諄誶談誼謀諶諜謊諫諧謔謁謂諤諭諼讒諮諳諺諦謎諞諝謨讜謖謝謠謗諡謙謐謹謾謫譾謬譚譖譙讕譜譎讞譴譫讖穀豶貝貞負貟貢財責賢敗賬貨質販貪貧貶購貯貫貳賤賁貰貼貴貺貸貿費賀貽賊贄賈賄貲賃賂贓資賅贐賕賑賚賒賦賭齎贖賞賜贔賙賡賠賧賴賵贅賻賺賽賾贗讚贇贈贍贏贛赬趙趕趨趲躉躍蹌蹠躒踐躂蹺蹕躚躋踴躊蹤躓躑躡蹣躕躥躪躦軀車軋軌軒軑軔轉軛輪軟轟軲軻轤軸軹軼軤軫轢軺輕軾載輊轎輈輇輅較輒輔輛輦輩輝輥輞輬輟輜輳輻輯轀輸轡轅轄輾轆轍轔辭辯辮邊遼達遷過邁運還這進遠違連遲邇逕跡適選遜遞邐邏遺遙鄧鄺鄔郵鄒鄴鄰鬱郤郟鄶鄭鄆酈鄖鄲醞醱醬釅釃釀釋裏钜鑒鑾鏨釓釔針釘釗釙釕釷釺釧釤鈒釩釣鍆釹鍚釵鈃鈣鈈鈦鈍鈔鍾鈉鋇鋼鈑鈐鑰欽鈞鎢鉤鈧鈁鈥鈄鈕鈀鈺錢鉦鉗鈷缽鈳鉕鈽鈸鉞鑽鉬鉭鉀鈿鈾鐵鉑鈴鑠鉛鉚鈰鉉鉈鉍鈹鐸鉶銬銠鉺銪鋏鋣鐃銍鐺銅鋁銱銦鎧鍘銖銑鋌銩銛鏵銓鉿銚鉻銘錚銫鉸銥鏟銃鐋銨銀銣鑄鐒鋪鋙錸鋱鏈鏗銷鎖鋰鋥鋤鍋鋯鋨鏽銼鋝鋒鋅鋶鐦鐧銳銻鋃鋟鋦錒錆鍺錯錨錡錁錕錩錫錮鑼錘錐錦鍁錈錇錟錠鍵鋸錳錙鍥鍈鍇鏘鍶鍔鍤鍬鍾鍛鎪鍠鍰鎄鍍鎂鏤鎡鏌鎮鎛鎘鑷鐫鎳鎿鎦鎬鎊鎰鎔鏢鏜鏍鏰鏞鏡鏑鏃鏇鏐鐔钁鐐鏷鑥鐓鑭鐠鑹鏹鐙鑊鐳鐶鐲鐮鐿鑔鑣鑞鑲長門閂閃閆閈閉問闖閏闈閑閎間閔閌悶閘鬧閨聞闥閩閭闓閥閣閡閫鬮閱閬闍閾閹閶鬩閿閽閻閼闡闌闃闠闊闋闔闐闒闕闞闤隊陽陰陣階際陸隴陳陘陝隉隕險隨隱隸雋難雛讎靂霧霽黴靄靚靜靨韃鞽韉韝韋韌韍韓韙韞韜韻頁頂頃頇項順須頊頑顧頓頎頒頌頏預顱領頗頸頡頰頲頜潁熲頦頤頻頮頹頷頴穎顆題顒顎顓顏額顳顢顛顙顥纇顫顬顰顴風颺颭颮颯颶颸颼颻飀飄飆飆飛饗饜飣饑飥餳飩餼飪飫飭飯飲餞飾飽飼飿飴餌饒餉餄餎餃餏餅餑餖餓餘餒餕餜餛餡館餷饋餶餿饞饁饃餺餾饈饉饅饊饌饢馬馭馱馴馳驅馹駁驢駔駛駟駙駒騶駐駝駑駕驛駘驍罵駰驕驊駱駭駢驫驪騁驗騂駸駿騏騎騍騅騌驌驂騙騭騤騷騖驁騮騫騸驃騾驄驏驟驥驦驤髏髖髕鬢魘魎魚魛魢魷魨魯魴魺鮁鮃鯰鱸鮋鮓鮒鮊鮑鱟鮍鮐鮭鮚鮳鮪鮞鮦鰂鮜鱠鱭鮫鮮鮺鯗鱘鯁鱺鰱鰹鯉鰣鰷鯀鯊鯇鮶鯽鯒鯖鯪鯕鯫鯡鯤鯧鯝鯢鯰鯛鯨鯵鯴鯔鱝鰈鰏鱨鯷鰮鰃鰓鱷鰍鰒鰉鰁鱂鯿鰠鼇鰭鰨鰥鰩鰟鰜鰳鰾鱈鱉鰻鰵鱅鰼鱖鱔鱗鱒鱯鱤鱧鱣鳥鳩雞鳶鳴鳲鷗鴉鶬鴇鴆鴣鶇鸕鴨鴞鴦鴒鴟鴝鴛鴬鴕鷥鷙鴯鴰鵂鴴鵃鴿鸞鴻鵐鵓鸝鵑鵠鵝鵒鷳鵜鵡鵲鶓鵪鶤鵯鵬鵮鶉鶊鵷鷫鶘鶡鶚鶻鶿鶥鶩鷊鷂鶲鶹鶺鷁鶼鶴鷖鸚鷓鷚鷯鷦鷲鷸鷺鸇鷹鸌鸏鸛鸘鹺麥麩黃黌黶黷黲黽黿鼂鼉鞀鼴齇齊齏齒齔齕齗齟齡齙齠齜齦齬齪齲齷龍龔龕龜誌製谘隻裡係範鬆冇嚐嘗鬨麵準鐘彆閒乾儘臟拚".toCharArray();
        final char[] UTF8S = "万与丑专业丛东丝丢两严丧个丬丰临为丽举么义乌乐乔习乡书买乱争于亏云亘亚产亩亲亵亸亿仅从仑仓仪们价众优伙会伛伞伟传伤伥伦伧伪伫体余佣佥侠侣侥侦侧侨侩侪侬俣俦俨俩俪俭债倾偬偻偾偿傥傧储傩儿兑兖党兰关兴兹养兽冁内冈册写军农冢冯冲决况冻净凄凉凌减凑凛几凤凫凭凯击凼凿刍划刘则刚创删别刬刭刽刿剀剂剐剑剥剧劝办务劢动励劲劳势勋勐勚匀匦匮区医华协单卖卢卤卧卫却卺厂厅历厉压厌厍厕厢厣厦厨厩厮县参叆叇双发变叙叠叶号叹叽吁后吓吕吗吣吨听启吴呒呓呕呖呗员呙呛呜咏咔咙咛咝咤咴咸哌响哑哒哓哔哕哗哙哜哝哟唛唝唠唡唢唣唤唿啧啬啭啮啰啴啸喷喽喾嗫呵嗳嘘嘤嘱噜噼嚣嚯团园囱围囵国图圆圣圹场坂坏块坚坛坜坝坞坟坠垄垅垆垒垦垧垩垫垭垯垱垲垴埘埙埚埝埯堑堕塆墙壮声壳壶壸处备复够头夸夹夺奁奂奋奖奥妆妇妈妩妪妫姗姜娄娅娆娇娈娱娲娴婳婴婵婶媪嫒嫔嫱嬷孙学孪宁宝实宠审宪宫宽宾寝对寻导寿将尔尘尧尴尸尽层屃屉届属屡屦屿岁岂岖岗岘岙岚岛岭岳岽岿峃峄峡峣峤峥峦崂崃崄崭嵘嵚嵛嵝嵴巅巩巯币帅师帏帐帘帜带帧帮帱帻帼幂幞干并广庄庆庐庑库应庙庞废庼廪开异弃张弥弪弯弹强归当录彟彦彻径徕御忆忏忧忾怀态怂怃怄怅怆怜总怼怿恋恳恶恸恹恺恻恼恽悦悫悬悭悯惊惧惨惩惫惬惭惮惯愍愠愤愦愿慑慭憷懑懒懔戆戋戏戗战戬户扎扑扦执扩扪扫扬扰抚抛抟抠抡抢护报担拟拢拣拥拦拧拨择挂挚挛挜挝挞挟挠挡挢挣挤挥挦捞损捡换捣据捻掳掴掷掸掺掼揸揽揿搀搁搂搅携摄摅摆摇摈摊撄撑撵撷撸撺擞攒敌敛数斋斓斗斩断无旧时旷旸昙昼昽显晋晒晓晔晕晖暂暧札术朴机杀杂权条来杨杩杰极构枞枢枣枥枧枨枪枫枭柜柠柽栀栅标栈栉栊栋栌栎栏树栖样栾桊桠桡桢档桤桥桦桧桨桩梦梼梾检棂椁椟椠椤椭楼榄榇榈榉槚槛槟槠横樯樱橥橱橹橼檐檩欢欤欧歼殁殇残殒殓殚殡殴毁毂毕毙毡毵氇气氢氩氲汇汉污汤汹沓沟没沣沤沥沦沧沨沩沪沵泞泪泶泷泸泺泻泼泽泾洁洒洼浃浅浆浇浈浉浊测浍济浏浐浑浒浓浔浕涂涌涛涝涞涟涠涡涢涣涤润涧涨涩淀渊渌渍渎渐渑渔渖渗温游湾湿溃溅溆溇滗滚滞滟滠满滢滤滥滦滨滩滪漤潆潇潋潍潜潴澜濑濒灏灭灯灵灾灿炀炉炖炜炝点炼炽烁烂烃烛烟烦烧烨烩烫烬热焕焖焘煅煳熘爱爷牍牦牵牺犊犟状犷犸犹狈狍狝狞独狭狮狯狰狱狲猃猎猕猡猪猫猬献獭玑玙玚玛玮环现玱玺珉珏珐珑珰珲琎琏琐琼瑶瑷璇璎瓒瓮瓯电画畅畲畴疖疗疟疠疡疬疮疯疱疴痈痉痒痖痨痪痫痴瘅瘆瘗瘘瘪瘫瘾瘿癞癣癫癯皑皱皲盏盐监盖盗盘眍眦眬着睁睐睑瞒瞩矫矶矾矿砀码砖砗砚砜砺砻砾础硁硅硕硖硗硙硚确硷碍碛碜碱碹磙礼祎祢祯祷祸禀禄禅离秃秆种积称秽秾稆税稣稳穑穷窃窍窑窜窝窥窦窭竖竞笃笋笔笕笺笼笾筑筚筛筜筝筹签简箓箦箧箨箩箪箫篑篓篮篱簖籁籴类籼粜粝粤粪粮糁糇紧絷纟纠纡红纣纤纥约级纨纩纪纫纬纭纮纯纰纱纲纳纴纵纶纷纸纹纺纻纼纽纾线绀绁绂练组绅细织终绉绊绋绌绍绎经绐绑绒结绔绕绖绗绘给绚绛络绝绞统绠绡绢绣绤绥绦继绨绩绪绫绬续绮绯绰绱绲绳维绵绶绷绸绹绺绻综绽绾绿缀缁缂缃缄缅缆缇缈缉缊缋缌缍缎缏缐缑缒缓缔缕编缗缘缙缚缛缜缝缞缟缠缡缢缣缤缥缦缧缨缩缪缫缬缭缮缯缰缱缲缳缴缵罂网罗罚罢罴羁羟羡翘翙翚耢耧耸耻聂聋职聍联聩聪肃肠肤肷肾肿胀胁胆胜胧胨胪胫胶脉脍脏脐脑脓脔脚脱脶脸腊腌腘腭腻腼腽腾膑臜舆舣舰舱舻艰艳艹艺节芈芗芜芦苁苇苈苋苌苍苎苏苘苹茎茏茑茔茕茧荆荐荙荚荛荜荞荟荠荡荣荤荥荦荧荨荩荪荫荬荭荮药莅莜莱莲莳莴莶获莸莹莺莼萚萝萤营萦萧萨葱蒇蒉蒋蒌蓝蓟蓠蓣蓥蓦蔷蔹蔺蔼蕲蕴薮藁藓虏虑虚虫虬虮虽虾虿蚀蚁蚂蚕蚝蚬蛊蛎蛏蛮蛰蛱蛲蛳蛴蜕蜗蜡蝇蝈蝉蝎蝼蝾螀螨蟏衅衔补衬衮袄袅袆袜袭袯装裆裈裢裣裤裥褛褴襁襕见观觃规觅视觇览觉觊觋觌觍觎觏觐觑觞触觯詟誉誊讠计订讣认讥讦讧讨让讪讫训议讯记讱讲讳讴讵讶讷许讹论讻讼讽设访诀证诂诃评诅识诇诈诉诊诋诌词诎诏诐译诒诓诔试诖诗诘诙诚诛诜话诞诟诠诡询诣诤该详诧诨诩诪诫诬语诮误诰诱诲诳说诵诶请诸诹诺读诼诽课诿谀谁谂调谄谅谆谇谈谊谋谌谍谎谏谐谑谒谓谔谕谖谗谘谙谚谛谜谝谞谟谠谡谢谣谤谥谦谧谨谩谪谫谬谭谮谯谰谱谲谳谴谵谶谷豮贝贞负贠贡财责贤败账货质贩贪贫贬购贮贯贰贱贲贳贴贵贶贷贸费贺贻贼贽贾贿赀赁赂赃资赅赆赇赈赉赊赋赌赍赎赏赐赑赒赓赔赕赖赗赘赙赚赛赜赝赞赟赠赡赢赣赪赵赶趋趱趸跃跄跖跞践跶跷跸跹跻踊踌踪踬踯蹑蹒蹰蹿躏躜躯车轧轨轩轪轫转轭轮软轰轱轲轳轴轵轶轷轸轹轺轻轼载轾轿辀辁辂较辄辅辆辇辈辉辊辋辌辍辎辏辐辑辒输辔辕辖辗辘辙辚辞辩辫边辽达迁过迈运还这进远违连迟迩迳迹适选逊递逦逻遗遥邓邝邬邮邹邺邻郁郄郏郐郑郓郦郧郸酝酦酱酽酾酿释里鉅鉴銮錾钆钇针钉钊钋钌钍钎钏钐钑钒钓钔钕钖钗钘钙钚钛钝钞钟钠钡钢钣钤钥钦钧钨钩钪钫钬钭钮钯钰钱钲钳钴钵钶钷钸钹钺钻钼钽钾钿铀铁铂铃铄铅铆铈铉铊铋铍铎铏铐铑铒铕铗铘铙铚铛铜铝铞铟铠铡铢铣铤铥铦铧铨铪铫铬铭铮铯铰铱铲铳铴铵银铷铸铹铺铻铼铽链铿销锁锂锃锄锅锆锇锈锉锊锋锌锍锎锏锐锑锒锓锔锕锖锗错锚锜锞锟锠锡锢锣锤锥锦锨锩锫锬锭键锯锰锱锲锳锴锵锶锷锸锹锺锻锼锽锾锿镀镁镂镃镆镇镈镉镊镌镍镎镏镐镑镒镕镖镗镙镚镛镜镝镞镟镠镡镢镣镤镥镦镧镨镩镪镫镬镭镮镯镰镱镲镳镴镶长门闩闪闫闬闭问闯闰闱闲闳间闵闶闷闸闹闺闻闼闽闾闿阀阁阂阃阄阅阆阇阈阉阊阋阌阍阎阏阐阑阒阓阔阕阖阗阘阙阚阛队阳阴阵阶际陆陇陈陉陕陧陨险随隐隶隽难雏雠雳雾霁霉霭靓静靥鞑鞒鞯鞴韦韧韨韩韪韫韬韵页顶顷顸项顺须顼顽顾顿颀颁颂颃预颅领颇颈颉颊颋颌颍颎颏颐频颒颓颔颕颖颗题颙颚颛颜额颞颟颠颡颢颣颤颥颦颧风飏飐飑飒飓飔飕飖飗飘飙飚飞飨餍饤饥饦饧饨饩饪饫饬饭饮饯饰饱饲饳饴饵饶饷饸饹饺饻饼饽饾饿馀馁馂馃馄馅馆馇馈馉馊馋馌馍馎馏馐馑馒馓馔馕马驭驮驯驰驱驲驳驴驵驶驷驸驹驺驻驼驽驾驿骀骁骂骃骄骅骆骇骈骉骊骋验骍骎骏骐骑骒骓骔骕骖骗骘骙骚骛骜骝骞骟骠骡骢骣骤骥骦骧髅髋髌鬓魇魉鱼鱽鱾鱿鲀鲁鲂鲄鲅鲆鲇鲈鲉鲊鲋鲌鲍鲎鲏鲐鲑鲒鲓鲔鲕鲖鲗鲘鲙鲚鲛鲜鲝鲞鲟鲠鲡鲢鲣鲤鲥鲦鲧鲨鲩鲪鲫鲬鲭鲮鲯鲰鲱鲲鲳鲴鲵鲶鲷鲸鲹鲺鲻鲼鲽鲾鲿鳀鳁鳂鳃鳄鳅鳆鳇鳈鳉鳊鳋鳌鳍鳎鳏鳐鳑鳒鳓鳔鳕鳖鳗鳘鳙鳛鳜鳝鳞鳟鳠鳡鳢鳣鸟鸠鸡鸢鸣鸤鸥鸦鸧鸨鸩鸪鸫鸬鸭鸮鸯鸰鸱鸲鸳鸴鸵鸶鸷鸸鸹鸺鸻鸼鸽鸾鸿鹀鹁鹂鹃鹄鹅鹆鹇鹈鹉鹊鹋鹌鹍鹎鹏鹐鹑鹒鹓鹔鹕鹖鹗鹘鹚鹛鹜鹝鹞鹟鹠鹡鹢鹣鹤鹥鹦鹧鹨鹩鹪鹫鹬鹭鹯鹰鹱鹲鹳鹴鹾麦麸黄黉黡黩黪黾鼋鼌鼍鼗鼹齄齐齑齿龀龁龂龃龄龅龆龇龈龉龊龋龌龙龚龛龟志制咨只里系范松没尝尝闹面准钟别闲干尽脏拼".toCharArray();

        for (int i = 0, n = Math.min(UTF8T.length, UTF8S.length); i < n; i++) {
            final Character cT = Character.valueOf(UTF8T[i]);
            final Character cS = Character.valueOf(UTF8S[i]);
            tcToScHashMap.put(cT, cS);
            scToTcHashMap.put(cS, cT);
        }
    }

    protected HashMap<Character, Character> getTcToScHashMap() {

        if (tcToScHashMap.isEmpty()) {

            buildChineseConversionHashMap();

        }

        return tcToScHashMap;
    }

    protected HashMap<Character, Character> getScToTcHashMap() {

        if (scToTcHashMap.isEmpty()) {

            buildChineseConversionHashMap();

        }

        return scToTcHashMap;
    }


    protected String chineseConversion(String text, HashMap<Character, Character> chineseConversionHashMap) {
        final char[] chars = text.toCharArray();
        for (int i = 0, n = chars.length; i < n; i++) {
            final Character found = chineseConversionHashMap.get(chars[i]);
            if (null != found)
                chars[i] = found;
        }
        return String.valueOf(chars);
    }


    protected String tcToSc(String text) {
        return chineseConversion(text, getTcToScHashMap());
    }

    protected String scToTc(String text) {
        return chineseConversion(text, getScToTcHashMap());
    }


    /**
     * Ref: http://stackoverflow.com/questions/2220366/get-unicode-value-of-a-character
     *
     * @param text
     * @return
     */
    protected String strToUtfRepresentation(String text) {

        // TODO slow, should enhance
        StringBuffer sbResult = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            sbResult.append("\\u" + Integer.toHexString(text.codePointAt(i) | 0x10000).substring(1));
        }

        return "" + sbResult;

    }


    // == Location ==
    protected long lat2LatE6(double latitude) {
        return (long) (latitude * 1000000);
    }

    protected double latE62Lat(long latitudeE6) {
        return (double) latitudeE6 / 1000000;
    }

    protected long lng2LngE6(double longitude) {
        return lat2LatE6(longitude); // just use back lat2LatE6()
    }

    protected double lngE62Lng(long longitudeE6) {
        return latE62Lat(longitudeE6); // just use back latE62Lat()
    }


    // == MathManager ==

    protected double normalizeToOneLoopBearing(double value) {

        if (Double.isNaN(value))
            return Double.NaN;

        return value % 360;
    }

    protected double halfToWholeCircleBearing(double value) {

        if (Double.isNaN(value))
            return Double.NaN;

        value = normalizeToOneLoopBearing(value);

        if (value < 0) {
            value = 360 + value;
        }

        return value;

    }

    protected double wholeToHalfCircleBearing(double value) {

        if (Double.isNaN(value))
            return Double.NaN;

        value = normalizeToOneLoopBearing(value);

        if (value > 180) {
            value = -(360 - value);
        }

        return value;


    }

    protected double calculateAngleDerivation(double from, double to) {

        if (Double.isNaN(from))
            return Double.NaN;

        if (Double.isNaN(to))
            return Double.NaN;

        from = halfToWholeCircleBearing(from);
        to = halfToWholeCircleBearing(to);

        double choice1 = to - from;
        double choice2 = (to >= from) ? (-(from + 360 - to)) : (360 - from + to);

        return (Math.abs(choice1) <= Math.abs(choice2)) ? choice1 : choice2;


    }


    // == Data processor ==


    protected void lowPassFilter(ArrayList<Double> lowPassHistoryList, int maxHistorySize, double latestValue, double strength) {


        if (maxHistorySize == 0) {
            lowPassHistoryList.clear();
            return;
        }

        if (lowPassHistoryList.isEmpty()) {
            lowPassHistoryList.add(latestValue);
            return;
        }

        if (!Double.isNaN(latestValue)) {
            double revisedLatestValue = lowPassHistoryList.get(0) * strength + latestValue * (1 - strength);
            lowPassHistoryList.add(0, revisedLatestValue);
        }


        while (lowPassHistoryList.size() > maxHistorySize)
            lowPassHistoryList.remove(lowPassHistoryList.size() - 1);


    }


    protected void lowPassFilterForAngle(ArrayList<Double> lowPassHistoryList, int maxHistorySize, double latestValue, double strength) {

        // TODO MVP too much copy
        if (maxHistorySize == 0) {
            lowPassHistoryList.clear();
            return;
        }

        if (lowPassHistoryList.isEmpty()) {
            lowPassHistoryList.add(latestValue);
            return;
        }

        if (!Double.isNaN(latestValue)) {
            double revisedLatestValue = halfToWholeCircleBearing(lowPassHistoryList.get(0)) + calculateAngleDerivation(lowPassHistoryList.get(0), latestValue) * (1 - strength);
            revisedLatestValue = wholeToHalfCircleBearing(revisedLatestValue);
            lowPassHistoryList.add(0, revisedLatestValue);
        }


        while (lowPassHistoryList.size() > maxHistorySize)
            lowPassHistoryList.remove(lowPassHistoryList.size() - 1);


    }

    // == Generate unique ID ==
    protected int genUniqueId() {

        return (int) (Math.random() * Integer.MAX_VALUE);

    }

    // == BitmapManager ==
    protected Bitmap convertBitmapConfig(Bitmap bitmap, Bitmap.Config config) {
//        Bitmap convertedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
//        Canvas canvas = new Canvas(convertedBitmap);
//        Paint paint = new Paint();
//        paint.setColor(Color.BLACK);
//        canvas.drawBitmap(bitmap, 0, 0, paint);
//        return convertedBitmap;

        return bitmap.copy(config, false);

    }

}
