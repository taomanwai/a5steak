package com.tommytao.a5steak.sample;

import android.app.Application;
import android.content.Context;

/**
 * Created by tommytao on 30/9/15.
 */
public class MainApp extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

    }
}
