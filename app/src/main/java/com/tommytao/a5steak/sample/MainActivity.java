package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.tommytao.a5steak.customview.google.GMapAdapter;
import com.tommytao.a5steak.customview.google.NavMapView;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends Activity {

    @Bind(R.id.flMap)
    FrameLayout flMap;

    private NavMapView navMapView;

    private GMapAdapter mapAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        navMapView = new NavMapView(this);
        flMap.addView(navMapView);
        mapAdapter = new GMapAdapter(navMapView);



        mapAdapter.onCreate(savedInstanceState);
        mapAdapter.init(this, new GMapAdapter.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

            }
        });





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
