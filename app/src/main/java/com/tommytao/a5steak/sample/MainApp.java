package com.tommytao.a5steak.sample;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.tommytao.a5steak.util.NetworkInfoManager;
import com.tommytao.a5steak.util.google.DirectionsApiManager;
import com.tommytao.a5steak.util.google.GeocodeManager;
import com.tommytao.a5steak.util.google.PlacesApiManager;


/**
 * Created by taomanwai on 5/10/2015.
 */
public class MainApp extends Application {

    public static final String GOOGLE_CLIENT_ID = "gme-easyvanhongkonglimited";
    public static final String GOOGLE_CLIENT_SECRET = "RglSWAR2KO9R2OghAMwyj4WqIXg=";
    public static final String GOOGLE_PLACES_API_KEY = "AIzaSyDho8iArjPHWI7GiY1xGhefeB6LplFucdI";
    public static final String GOOGLE_API_DOMAIN = "https://maps.googleapis.com";

    private static Context context;

    private static RequestQueue requestQueue;

    public static Context getContext() {
        return context;
    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        BasicNetwork basicNetwork = new BasicNetwork(new HurlStack());
        DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 0);

        requestQueue = new RequestQueue(cache, basicNetwork);

        requestQueue.start();

        GeocodeManager.getInstance().init(this, requestQueue, "", ""); // GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET

        PlacesApiManager.getInstance().init(this, requestQueue, GOOGLE_PLACES_API_KEY);

        DirectionsApiManager.getInstance().init(this, requestQueue, "", "");

        NetworkInfoManager.getInstance().init(this, requestQueue);


    }
}
