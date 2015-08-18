package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tommytao.a5steak.customview.google.GMapAdapter;
import com.tommytao.a5steak.customview.google.NavMapView;
import com.tommytao.a5steak.util.google.DirectionsApiManager;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);


        mapAdapter = new GMapAdapter(navMapView);


        mapAdapter.onCreate(savedInstanceState);
        mapAdapter.init(this, null);

        mapAdapter = new GMapAdapter(navMapView);










    }



    @OnClick(R.id.btnGo)
    public void go(){

        navMapView.connectNavigation(new NavMapView.OnConnectListener() {
            @Override
            public void onConnected(boolean succeed) {
                navMapView.startNavigation(22.339662, 114.154811, DirectionsApiManager.AVOID_HIGHWAYS, new Locale("zh", "HK"), new NavMapView.OnStartListener() {
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

        navMapView.addOnUpdateListener(new NavMapView.OnUpdateListener() {
            @Override
            public void onUpdate(int maneuver, double distanceFromEndOfStep, String instructionsInHtml, String instructionsInText, long eta, NavMapView.Route route) {
                Log.d("", "update_t: " + maneuver + " " + distanceFromEndOfStep +  " " + eta);
            }
        });



    }

    @OnClick(R.id.btnStop)
    public void stop(){

        navMapView.disconnectNavigation();

    }

    @OnClick(R.id.btnResume)
    public void resume(){
        navMapView.resumeNavigation();
    }

    @OnClick(R.id.btnPause)
    public void pause(){
        navMapView.pauseNavigation();
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
