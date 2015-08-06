package com.tommytao.a5steak.sample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.tommytao.a5steak.customview.ScrollBarListView;
import com.tommytao.a5steak.customview.google.AnimMapView;
import com.tommytao.a5steak.customview.google.GMapAdapter;
import com.tommytao.a5steak.util.MusicManager;
import com.tommytao.a5steak.util.UxManager;
import com.tommytao.a5steak.util.google.PlacesApiManager;
import com.tommytao.a5steak.util.sensor.GSensor;
import com.tommytao.a5steak.util.sensor.LocationSensor;
import com.tommytao.a5steak.util.sensor.MagneticSensor;
import com.tommytao.a5steak.util.sensor.OrientationSensor;
import com.tommytao.a5steak.util.sensor.SoundSensor;
import com.tommytao.a5steak.util.sensor.analyzer.OrientationAnalyzer;
import com.tommytao.a5steak.util.sensor.support.DataProcessor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {

    public final static int DEFAULT_ZOOM = 14;


    @InjectView(R.id.flMap)
    FrameLayout flMap;

    @InjectView(R.id.listView)
    ScrollBarListView listView;

    @InjectView(R.id.tvMsg)
    TextView tvMsg;

    @InjectView(R.id.tvMiddle)
    TextView tvMiddle;

    @InjectView(R.id.topBar)
    View topBar;

    @InjectView(R.id.btnTwo)
    Button btnTwo;


    @InjectView(R.id.rightBar)
    View rightBar;

    @InjectView(R.id.bottomBar)
    View bottomBar;

    @InjectView(R.id.flFrag)
    FrameLayout flFrag;

    @InjectView(R.id.tvWhole)
    TextView tvWhole;

    Marker marker;

    AnimMapView animMapView;


    Handler handler = new Handler(Looper.getMainLooper());

    ArrayList<Double> bearList = new ArrayList<>();

    GMapAdapter mapAdapter;

    @OnClick(R.id.btnOne)
    public void oneClicked() {
//        UxManager.getInstance().fadeOutView(tvMiddle, 3000, null);

//        UxManager.getInstance().slideLeftShowView(rightBar, 3000, null);


    }

    @OnClick(R.id.btnTwo)
    public void twoClicked() {
//        UxManager.getInstance().fadeInView(tvMiddle, 3000, null);

//        UxManager.getInstance().slideRightHideView(rightBar, 3000, null);
    }

    @OnClick(R.id.btnThree)
    public void threeClicked() {

//        UxManager.getInstance().clearAnimationTo(tvMiddle, true);

//        UxManager.getInstance().spinViewInfinitely(tvMiddle, null);

//        getSupportFragmentManager().beginTransaction()
//                .setCustomAnimations(R.anim.fade_in_300, R.anim.fade_out_300, R.anim.fade_in_300, R.anim.fade_out_300)
//                .replace(R.id.flFrag, TwoFragment.newInstance()).addToBackStack(null).commit();

//        tvWhole.setText(this.getString(R.string.test_str, 3, 5));

//        MapViewAnimator.getInstance().slideAndRotateMarker(marker, 22.335453, 114.156343,  90, 2000, new MapViewAnimator.LinearLocationInterpolator(), null);
//
//
//        MapViewAnimator.getInstance().rotateMarker(marker, 90, 2000, null);

//        DirectionsApiManager.getInstance().route(
//                22.337080, 114.147331,
//                22.335453, 114.156343,
//                new Locale("zh", "HK"),
//                new DirectionsApiManager.OnRouteListener() {
//                    @Override
//                    public void returnSteps(ArrayList<DirectionsApiManager.Step> stepList, ArrayList<Location> overviewPolylineLocationList) {
//
//                        animMapView.guideAnimMarker(0,stepList , 3000, false, null);
//
//
//
//                    }
//                }
//        );

//        animMapView.flyAnimMarker(0, 22.335453, 114.156343, 90, 3000, null);

        // 22.337080, 114.147331, 70
        animMapView.driveAnimMarker("1", 22.336956, 114.147377, 70, 3000, false, null); // 22.335453, 114.156343

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.flFrag, OneFragment.newInstance()).commit();

        UxManager.getInstance().init(this);

        JSONObject jo = null;
        try {
            jo = new JSONObject("null");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        animMapView = new AnimMapView(this);
        flMap.addView(animMapView);
        mapAdapter = new GMapAdapter(animMapView);
        mapAdapter.onCreate(savedInstanceState);


        MusicManager.getInstance().init(this);

        OrientationSensor.getInstance().init(this);
        OrientationSensor.getInstance().connect();


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(this, 1000);

            }
        }, 1000);

        PlacesApiManager.getInstance().init(this, "AIzaSyDho8iArjPHWI7GiY1xGhefeB6LplFucdI");
        PlacesApiManager.getInstance().autoComplete("soi", new Locale("th", "TH"), new PlacesApiManager.OnAutoCompleteListener() {
            @Override
            public void returnAutoCompletes(ArrayList<PlacesApiManager.AutoComplete> autoCompletes, String input, JSONObject response) {
                Log.d("", "");
            }
        });


        ArrayList<String> strList = new ArrayList<>();
        strList.add("abc");
        strList.add("efg");

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strList);

        listView.setAdapter(itemsAdapter);

//        marker = (Marker) mapAdapter.addMarker(22.337080, 114.147331,R.drawable.ic_plusone_small_off_client, "", "");

        animMapView.addAnimMarker("1", 22.337080, 114.147331, 70, R.drawable.ic_plusone_small_off_client);

        mapAdapter.animateCamera(22.337080, 114.147331, DEFAULT_ZOOM, 300, null);


    }


    private void updateMap() {

        double bear = 0;

        try {
            bear = OrientationAnalyzer.getInstance().calculateYawPitchRoll(
                    GSensor.getInstance().getLastKnownX(), GSensor.getInstance().getLastKnownY(), GSensor.getInstance().getLastKnownZ(),
                    MagneticSensor.getInstance().getLastKnownXInuT(), MagneticSensor.getInstance().getLastKnownYInuT(), MagneticSensor.getInstance().getLastKnownZInuT()).getYaw();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            DataProcessor.getInstance().lowPassFilterForAngle(bearList, 40, bear, DataProcessor.DEFAULT_STRENGTH_OF_LPF);

            bear = bearList.get(0);

            bear = Math.round(bear * 10000) / 10000;


            double lat = LocationSensor.getInstance().getLastKnownLocation().getLatitude();
            double lng = LocationSensor.getInstance().getLastKnownLocation().getLongitude();


            ((GoogleMap) mapAdapter.getMap()).moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(DEFAULT_ZOOM).bearing((float) bear).build()));
        } catch (Exception e) {
            e.printStackTrace();
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

        SoundSensor.getInstance().disconnect();

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
