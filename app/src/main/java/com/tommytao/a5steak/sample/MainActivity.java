package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.tommytao.a5steak.customview.google.GMapAdapter;
import com.tommytao.a5steak.customview.google.NavMapView;
import com.tommytao.a5steak.util.AppManager;
import com.tommytao.a5steak.util.google.DirectionsApiManager;
import com.tommytao.a5steak.util.google.LocationFusedSensor;
import com.tommytao.a5steak.util.google.TextSpeaker;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {

    @Bind(R.id.navMapView)
    NavMapView navMapView;

    private GMapAdapter mapAdapter;

    public static final String CLIENT_ID_FOR_BUSINESS = "gme-easyvanhongkonglimited";
    public static final String CRYPTO_FOR_BUSINESS = "RglSWAR2KO9R2OghAMwyj4WqIXg=";
    public static final String PLACES_API_KEY = "AIzaSyDho8iArjPHWI7GiY1xGhefeB6LplFucdI";

    Handler handler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        TextSpeaker.getInstance().init(this);
        TextSpeaker.getInstance().connect(null);


        mapAdapter = new GMapAdapter(navMapView);


        mapAdapter.onCreate(savedInstanceState);
        mapAdapter.init(this, null);

        mapAdapter = new GMapAdapter(navMapView);

        AppManager.getInstance().init(this);

        ArrayList<String> sl = AppManager.getInstance().getInstalledApps();

        double a = (int) Math.ceil(1/ Double.NaN);

        Log.d("", "" + a);

        String s = "" + Float.NaN;

        float f = Float.valueOf(s);

        Log.d("", "" + a);

        LocationFusedSensor.getInstance().init(this);
        LocationFusedSensor.getInstance().connect(LocationFusedSensor.PRIORITY_HIGH_ACCURACY, 16, new LocationFusedSensor.OnConnectListener() {
            @Override
            public void onConnected(boolean succeed) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        float speed = LocationFusedSensor.getInstance().getLastKnownSpeed();

                        Log.d("", "speed_t: " + speed);

                        handler.postDelayed(this, 16);

                    }
                }, 16);
            }

            @Override
            public void onIgnored() {

            }
        });












    }


    @OnClick(R.id.btnGo)
    public void go() {


        TextSpeaker.getInstance().connect(new TextSpeaker.OnConnectListener() {
            @Override
            public void onConnected(boolean succeed) {

                if (!succeed)
                    return;

                TextSpeaker.getInstance().speak("Welcome", new TextSpeaker.OnSpeakListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(boolean succeed) {
                        if (!succeed)
                            return;

                        navMapView.connectNavigation(new NavMapView.OnConnectListener() {
                            @Override
                            public void onConnected(boolean succeed) {

                                if (!succeed) {
                                    Toast.makeText(MainActivity.this, "cannot connect", Toast.LENGTH_LONG).show();

                                    return;
                                }

                                navMapView.startNavigation(22.381245, 114.189324, DirectionsApiManager.AVOID_FERRIES, new Locale("zh", "HK"), new NavMapView.OnStartListener() {
                                    @Override
                                    public void onStarted(boolean succeed) {

                                    }

                                    @Override
                                    public void onIgnored() {

                                    }

                                    @Override
                                    public void onIgnoredByInvalidLatLng() {

                                        Toast.makeText(MainActivity.this, "lat lng not found", Toast.LENGTH_LONG).show();

                                    }
                                });
                            }
                        });


                    }
                });
            }
        });






    }

    @OnClick(R.id.btnStop)
    public void stop() {

        navMapView.disconnectNavigation(true);

    }

    @OnClick(R.id.btnResume)
    public void resume() {
//        navMapView.resumeNavigation();

        TextSpeaker.getInstance().setLocale(new Locale("zh", "HK"));
        TextSpeaker.getInstance().speak("啟動啟動啟動啟動啟動", new TextSpeaker.OnSpeakListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(boolean succeed) {

                Log.d("rtemp", "tts_t: " + "start_end: " + succeed);


            }
        });


    }

    @OnClick(R.id.btnPause)
    public void pause() {
//        navMapView.pauseNavigation();

        TextSpeaker.getInstance().setLocale(new Locale("zh", "HK"));
        TextSpeaker.getInstance().speak("暫停", new TextSpeaker.OnSpeakListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(boolean succeed) {

                Log.d("rtemp", "tts_t: " + "pause_end: " + succeed);

            }
        });
    }





    @Override
    protected void onResume() {
        super.onResume();

        mapAdapter.onResume();
        navMapView.resumeNavigation();

    }

    @Override
    protected void onPause() {

        mapAdapter.onPause();
        navMapView.pauseNavigation();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mapAdapter.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapAdapter.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        mapAdapter.onDestroy();
        super.onDestroy();
    }
}
