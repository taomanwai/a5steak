package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.MapsInitializer;
import com.tommytao.a5steak.gmapinteractive.NavMapView;
import com.tommytao.a5steak.misc.Encyclopedia;

import java.util.Locale;


public class MainActivity extends Activity {

    NavMapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mapView = (NavMapView) findViewById(R.id.mapView);

        mapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(this);


//        RotationVectorSensor.getInstance().init(this);
//        RotationVectorSensor.getInstance().connect();

        mapView.connectNavigation(new NavMapView.OnConnectListener() {
            @Override
            public void onConnected(boolean succeed) {
                mapView.startNavigation(Encyclopedia.HKSIL_LAT, Encyclopedia.HKSIL_LNG, "", new Locale("zh", "HK"), null);
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mapView.onSaveInstanceState(outState);
    }
}
