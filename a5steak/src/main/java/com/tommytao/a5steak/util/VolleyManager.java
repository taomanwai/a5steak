package com.tommytao.a5steak.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;

/**
 * Created by tommytao on 24/11/2015.
 */
public class VolleyManager extends Foundation {
    private static VolleyManager ourInstance = new VolleyManager();

    public static VolleyManager getInstance() {
        return ourInstance;
    }

    private VolleyManager() {
    }

    // --


    @Deprecated
    public boolean init(Context context) {
        return super.init(context);
    }

    @Deprecated
    public boolean init(Context context, RequestQueue requestQueue) {
        return super.init(context, requestQueue);
    }

    @Deprecated
    public boolean isInitialized() {
        return super.isInitialized();
    }

//    public RequestQueue getDefaultRequestQueue(Context context){
//        return Volley.newRequestQueue(context);
//    }
//
//    public RequestQueue getCustomRequestQueue(Context context, HttpStack httpStack, int cacheSizeInByte){
//        BasicNetwork basicNetwork = new BasicNetwork(httpStack);
//        DiskBasedCache cache = new DiskBasedCache(context.getCacheDir(), cacheSizeInByte);
//
//        RequestQueue requestQueue = new RequestQueue(cache, basicNetwork);
//
//        requestQueue.start();
//
//        return requestQueue;
//    }


    @Override
    public RequestQueue getDefaultRequestQueue(Context context) {
        return super.getDefaultRequestQueue(context);
    }

    @Override
    protected RequestQueue getOkHttpRequestQueue(Context context) {
        return super.getOkHttpRequestQueue(context);
    }

    @Override
    public RequestQueue getCustomRequestQueue(Context context, HttpStack httpStack, int cacheSizeInByte) {
        return super.getCustomRequestQueue(context, httpStack, cacheSizeInByte);
    }
}
