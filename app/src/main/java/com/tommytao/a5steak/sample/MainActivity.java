package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.gms.maps.MapsInitializer;
import com.tommytao.a5steak.customview.google.NavMapView;
import com.tommytao.a5steak.util.Encyclopedia;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {


    @Bind(R.id.navMapView)
    NavMapView navMapView;

    @Bind(R.id.btnGo)
    Button btnGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        navMapView.startNavigation(Encyclopedia.HKSIL_LAT, Encyclopedia.HKSIL_LNG, "", Locale.US, null);

        navMapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(this);

        navMapView.connectNavigation(null);



    }

    @OnClick(R.id.btnGo)
    public void go(){

        navMapView.startNavigation(
                Encyclopedia.HKSIL_LAT,
                Encyclopedia.HKSIL_LNG,
                "",
                Locale.US,
                null
        );

    }

    @Override
    protected void onResume() {
        super.onResume();
        navMapView.onResume();
    }

    @Override
    protected void onPause() {
        navMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        navMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        navMapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        navMapView.onSaveInstanceState(outState);
    }
}
