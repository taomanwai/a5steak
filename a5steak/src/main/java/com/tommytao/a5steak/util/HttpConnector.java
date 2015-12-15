package com.tommytao.a5steak.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.tommytao.a5steak.common.util.Foundation;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for getting gravity field reading
 */
public class HttpConnector extends Foundation {

    private static HttpConnector instance;

    public static HttpConnector getInstance() {

        if (instance == null)
            instance = new HttpConnector();

        return instance;
    }

    private HttpConnector() {

    }

    // --

    @Deprecated
    public boolean init(Context context) {
        return super.init(context);
    }

    @Override
    public boolean init(Context context, RequestQueue requestQueue) {
        return super.init(context, requestQueue);
    }

    @Override
    public boolean isInitialized() {
        return super.isInitialized();
    }

    @Override
    public void httpGetJSON(String link, int maxNoOfRetries, OnHttpGetJSONListener listener) {

        if (requestQueue == null)
            super.httpGetJSON(link, maxNoOfRetries, listener);
        else
            super.httpGetJSONByVolley(link, maxNoOfRetries, listener);

    }

    @Override
    protected void httpGetString(String link, int maxNoOfRetries, OnHttpGetStringListener listener) {

        if (requestQueue == null)
            super.httpGetString(link, maxNoOfRetries, listener);
        else
            super.httpGetStringByVolley(link, maxNoOfRetries, listener);

    }

    @Override
    protected void httpGetByteArray(String link, int maxNoOfRetries, OnHttpGetByteArrayListener listener) {
        super.httpGetByteArray(link, maxNoOfRetries, listener);
    }

    @Override
    protected void httpGetFile(String link, int maxNoOfRetries, String directory, String fileName, OnHttpGetFileListener listener) {
        super.httpGetFile(link, maxNoOfRetries, directory, fileName, listener);
    }


    @Override
    protected boolean httpPostString(String link, String dataStr, HashMap<String, String> headers, OnHttpPostStringListener listener) {

        if (requestQueue == null)
            return super.httpPostString(link, dataStr, headers, listener);
        else
            return super.httpPostStringByVolley(link, dataStr, headers, listener);

    }

    @Override
    protected void httpPostByteArray(String link, Map<String, Object> params, byte[] imgData, Map<String, Object> imgDataParams, OnHttpPostByteArrayListener listener) {
        super.httpPostByteArray(link, params, imgData, imgDataParams, listener);
    }

    @Override
    protected void httpPostJSONRecvJSON(String link, JSONObject jObj, HashMap<String, String> headers, OnHttpPostJSONRecvJSONListener listener) {
        super.httpPostJSONRecvJSON(link, jObj, headers, listener);
    }


}
