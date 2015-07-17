package com.tommytao.a5steak.sample;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.tommytao.a5steak.customview.GMapAdapter;
import com.tommytao.a5steak.util.DataProcessor;
import com.tommytao.a5steak.util.FineOrientationManager;
import com.tommytao.a5steak.util.Foundation;
import com.tommytao.a5steak.util.GSensor;
import com.tommytao.a5steak.util.LBSManager;
import com.tommytao.a5steak.util.MagneticSensor;
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

    ArrayList<Double> bearList = new ArrayList<Double>();

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
        LBSManager.getInstance().connect(LBSManager.DEFAULT_UPDATE_INTERVAL_IN_MS, new LBSManager.OnConnectListener() {

            @Override
            public void onConnected(boolean succeed) {
                Log.d("", "");
            }

        });

        h = new Handler(Looper.getMainLooper());

        DirectionsApiManager.getInstance().init(this, "gme-easyvanhongkonglimited", "RglSWAR2KO9R2OghAMwyj4WqIXg=");

        FineOrientationManager.getInstance().init(this);

        MagneticSensor.getInstance().init(this);

        MagneticSensor.getInstance().connect();

        GSensor.getInstance().init(this);

        GSensor.getInstance().connect();

        LBSManager.getInstance().addOnLocationChangeListener(new LBSManager.OnLocationChangeListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("", "change_t: loc: " + location.getLatitude() + " " + location.getLongitude());
//                updateMap();
            }
        });

        GSensor.getInstance().addOnReadingChangeListener(new Foundation.OnReadingChangeListener() {
            @Override
            public void onReadingChanged(float x, float y, float z) {
                Log.d("", "change_t: g: " + x + " " + y + " " + z);
//                updateMap();
            }
        });

        MagneticSensor.getInstance().addOnReadingChangeListener(new Foundation.OnReadingChangeListener() {
            @Override
            public void onReadingChanged(float x, float y, float z) {
                Log.d("", "change_t: mag: " + x + " " + y + " "+ z);
//                updateMap();
            }
        });


    }

    @OnClick(R.id.btnSpeak)
    public void speak() {


        h.postDelayed(new Runnable() {
            @Override
            public void run() {


                updateMap();

                h.postDelayed(this, 30);

            }
        }, 30);

        updateMap();


    }

    private void updateMap(){

        double bear = 0;

        try {
            bear = FineOrientationManager.getInstance().calculateYawPitchRoll(
                    GSensor.getInstance().getLastKnownX(), GSensor.getInstance().getLastKnownY(), GSensor.getInstance().getLastKnownZ(),
                    MagneticSensor.getInstance().getLastKnownX(), MagneticSensor.getInstance().getLastKnownY(), MagneticSensor.getInstance().getLastKnownZ()).getYaw();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DataProcessor.getInstance().lowPassFilterForAngle(bearList, 40, bear);

        bear = bearList.get(0);

        bear = Math.round(bear * 10000) / 10000;



//        double lat = LBSManager.getInstance().getLastKnownLocation().getLatitude();
//        double lng = LBSManager.getInstance().getLastKnownLocation().getLongitude();
                double lat = 22.421318;
                double lng = 114.226747;

        ((GoogleMap) mapAdapter.getMap()).moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(17).bearing((float) bear).build()));


    }

    @OnClick(R.id.btnReset)
    public void reset() {


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
