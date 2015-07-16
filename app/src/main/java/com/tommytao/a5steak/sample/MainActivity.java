package com.tommytao.a5steak.sample;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;
import com.tommytao.a5steak.customview.GMapAdapter;
import com.tommytao.a5steak.util.LBSManager;
import com.tommytao.a5steak.util.google.DirectionsApiManager;

import java.util.ArrayList;
import java.util.Locale;

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

    GMapAdapter mapAdapter;

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


    }

    @OnClick(R.id.btnSpeak)
    public void speak() {

//        CameraPosition position = new CameraPosition.Builder().target(new LatLng(0,0)).tilt(45).bearing(20).build();
//
//        ((GoogleMap) mapAdapter.getMap()).animateCamera(CameraUpdateFactory.newCameraPosition(position));

        Double lat = LBSManager.getInstance().getLastKnownLocation().getLatitude();
        Double lng = LBSManager.getInstance().getLastKnownLocation().getLongitude();

        mapAdapter.moveCameraByLatLng(lat, lng, 13);

        DirectionsApiManager.getInstance().route(lat, lng, 22.423159, 114.235990, new Locale("zh", "HK"), new DirectionsApiManager.OnRouteListener() {
            @Override
            public void returnStepList(ArrayList<DirectionsApiManager.Step> stepList, ArrayList<Location> overviewPolylineLocationList) {


                mapAdapter.addPolyline(overviewPolylineLocationList, 11, Color.RED);

            }
        });


    }

    @OnClick(R.id.btnReset)
    public void reset(){

        if (LBSManager.getInstance().isAvailable()){
            Toast.makeText(this, "yes", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "no", Toast.LENGTH_SHORT).show();
        }

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
