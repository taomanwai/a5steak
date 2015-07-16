package com.tommytao.a5steak.sample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;
import com.tommytao.a5steak.customview.GMapAdapter;
import com.tommytao.a5steak.util.FineOrientationManager;
import com.tommytao.a5steak.util.GSensor;
import com.tommytao.a5steak.util.LBSManager;
import com.tommytao.a5steak.util.MagneticSensor;
import com.tommytao.a5steak.util.UxManager;
import com.tommytao.a5steak.util.google.DirectionsApiManager;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.flMap)
    FrameLayout flMap;

    @InjectView(R.id.btnSpeak)
    Button btnSpeak;

    @InjectView(R.id.btnReset)
    Button btnReset;

    @InjectView(R.id.tvMsg)
    TextView tvMsg;

    @InjectView(R.id.flBar)
    FrameLayout flBar;

    @InjectView(R.id.flHBar)
    FrameLayout flHBar;


    Handler h;

    GMapAdapter mapAdapter;

    ArrayList<Double> yawHistories = new ArrayList<Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        MapView mapView = new MapView(this);
        flMap.addView(mapView);
        mapAdapter = new GMapAdapter(mapView);
        mapAdapter.onCreate(savedInstanceState);

        LBSManager.getInstance().init(this);
        LBSManager.getInstance().connect(new LBSManager.Listener() {
            @Override
            public void onConnected() {
                Log.d("", "");
            }

            @Override
            public void onError() {
                Log.d("", "");

            }
        });
        DirectionsApiManager.getInstance().init(this, "gme-easyvanhongkonglimited", "RglSWAR2KO9R2OghAMwyj4WqIXg=");

        FineOrientationManager.getInstance().init(this);

        MagneticSensor.getInstance().init(this);

        MagneticSensor.getInstance().connect();

        GSensor.getInstance().init(this);

        GSensor.getInstance().connect();



    }


    @OnClick(R.id.btnSpeak)
    public void speak() {

        UxManager.getInstance().slideRightHideView(flHBar, 300, new UxManager.Listener() {
            @Override
            public void onComplete() {
                flHBar.setVisibility(View.GONE);
            }
        });




    }

    @OnClick(R.id.btnReset)
    public void reset() {

        UxManager.getInstance().slideLeftShowView(flHBar, 300, null);

    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();

        mapAdapter.onLowMemory();
    }

    @Override
    protected void onPause() {

        mapAdapter.onPause();

        super.onPause();
    }

    @Override
    protected void onDestroy() {

        mapAdapter.onDestroy();

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mapAdapter.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mapAdapter.onSaveInstanceState(outState);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }


}
