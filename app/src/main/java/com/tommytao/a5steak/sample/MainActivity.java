package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.tommytao.a5steak.util.google.DirectionsApiManager;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DirectionsApiManager.getInstance().route(22.337154, 114.147422, 22.336023, 114.155276, "", Locale.US, new DirectionsApiManager.OnRouteListener() {
            @Override
            public void returnSteps(ArrayList<DirectionsApiManager.Step> steps, DirectionsApiManager.Polyline polyline) {
                Log.d("", "");
            }
        });


    }




}
