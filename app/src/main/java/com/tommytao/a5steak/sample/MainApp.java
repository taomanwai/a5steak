package com.tommytao.a5steak.sample;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;


/**
 * Created by taomanwai on 5/10/2015.
 */
public class MainApp extends Application {






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
