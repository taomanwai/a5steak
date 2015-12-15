package com.tommytao.a5steak.sample;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;


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



    }
}
