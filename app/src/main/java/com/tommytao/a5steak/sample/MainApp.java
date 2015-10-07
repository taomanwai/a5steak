package com.tommytao.a5steak.sample;

import android.app.Application;
import android.content.Context;


/**
 * Created by taomanwai on 5/10/2015.
 */
public class MainApp extends Application{


    private static Context context;

    public static Context getContext(){
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;


    }
}
