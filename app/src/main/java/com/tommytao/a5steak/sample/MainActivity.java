package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;

import com.tommytao.a5steak.customview.google.GMapAdapter;
import com.tommytao.a5steak.customview.google.NavMapView;
import com.tommytao.a5steak.util.google.DirectionsApiManager;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {

    @Bind(R.id.flMap)
    FrameLayout flMap;

    @Bind(R.id.btnGo)
    Button btnGo;


    private NavMapView navMapView;

    private GMapAdapter mapAdapter;

    public static final String CLIENT_ID_FOR_BUSINESS = "gme-easyvanhongkonglimited";
    public static final String CRYPTO_FOR_BUSINESS = "RglSWAR2KO9R2OghAMwyj4WqIXg=";
    public static final String PLACES_API_KEY = "AIzaSyDho8iArjPHWI7GiY1xGhefeB6LplFucdI";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        DirectionsApiManager.getInstance().init(this, CLIENT_ID_FOR_BUSINESS, CRYPTO_FOR_BUSINESS);

        navMapView = new NavMapView(this);
        flMap.addView(navMapView);
        mapAdapter = new GMapAdapter(navMapView);



        mapAdapter.onCreate(savedInstanceState);
        mapAdapter.init(this, null);



        navMapView.connectNavigation(null);


    }



    @OnClick(R.id.btnGo)
    public void go(){

        navMapView.startNavigation(22.339662, 114.154811, new Locale("zh", "HK"), null);




    }

    @OnClick(R.id.btnStop)
    public void stop(){

        navMapView.stopNavigation(null);

    }

    @Override
    protected void onResume() {
        super.onResume();

        mapAdapter.onResume();

    }

    @Override
    protected void onPause() {

        mapAdapter.onPause();
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
