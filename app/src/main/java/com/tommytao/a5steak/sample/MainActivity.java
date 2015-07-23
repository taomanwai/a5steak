package com.tommytao.a5steak.sample;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.tommytao.a5steak.customview.GMapAdapter;
import com.tommytao.a5steak.customview.ScrollBarListView;
import com.tommytao.a5steak.util.DataProcessor;
import com.tommytao.a5steak.util.FineOrientationManager;
import com.tommytao.a5steak.util.Foundation;
import com.tommytao.a5steak.util.GSensor;
import com.tommytao.a5steak.util.LBSManager;
import com.tommytao.a5steak.util.MagneticSensor;
import com.tommytao.a5steak.util.UxManager;
import com.tommytao.a5steak.util.google.ActivitySensor;
import com.tommytao.a5steak.util.google.ActivitySensor.OnConnectListener;
import com.tommytao.a5steak.util.google.DirectionsApiManager;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {


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

    @InjectView(R.id.rightBar)
    View rightBar;

    @InjectView(R.id.bottomBar)
    View bottomBar;

    @InjectView(R.id.flFrag)
    FrameLayout flFrag;

    @InjectView(R.id.tvWhole)
    TextView tvWhole;


    Handler h = new Handler(Looper.getMainLooper());

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

        tvWhole.setText(this.getString(R.string.test_str, 3, 5));


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);


        getSupportFragmentManager().beginTransaction().replace(R.id.flFrag, OneFragment.newInstance()).commit();


        UxManager.getInstance().init(this);


        MapView mapView = new MapView(this);
        flMap.addView(mapView);
        mapAdapter = new GMapAdapter(mapView);
        mapAdapter.onCreate(savedInstanceState);

        LBSManager.getInstance().init(this);
//        LBSManager.getInstance().connect(LBSManager.DEFAULT_UPDATE_INTERVAL_IN_MS, new LBSManager.OnConnectListener() {
//
//            @Override
//            public void onConnected(boolean succeed) {
//                Log.d("", "");
//            }
//
//        });

        h = new Handler(Looper.getMainLooper());

        DirectionsApiManager.getInstance().init(this, "gme-easyvanhongkonglimited", "RglSWAR2KO9R2OghAMwyj4WqIXg=");

        FineOrientationManager.getInstance().init(this);

        ActivitySensor.getInstance().init(this);

        ActivitySensor.getInstance().connect(new OnConnectListener() {
            @Override
            public void onConnected(boolean succeed) {
                if (!succeed) {
                    return;
                }

                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            String s = "" +
                                    ActivitySensor.getInstance().getLastKnownDetectedActivityFromGoogle().toString() + " "
                                    + ActivitySensor.getInstance().getLastKnownDetectedActivityFromGoogle().getConfidence();

                            Log.d("", "a_sense_t: " + s);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        h.postDelayed(this, 1000);
                    }
                }, 1000);

            }
        });


        MagneticSensor.getInstance().init(this);

//        MagneticSensor.getInstance().connect();

        GSensor.getInstance().init(this);

//        GSensor.getInstance().connect();

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
                Log.d("", "change_t: mag: " + x + " " + y + " " + z);
//                updateMap();
            }
        });

        h.postDelayed(new Runnable() {
            @Override
            public void run() {

//                updateMap();

                h.postDelayed(this, 30);

            }
        }, 30);

        ArrayList<String> strList = new ArrayList<String>();
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        strList.add("abc");
        strList.add("efg");
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strList);

        listView.setAdapter(itemsAdapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {

                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                    Log.d("", "test_t: idle");
//                    UxManager.getInstance().fadeOutView(tvMsg, listView.getScrollBarDefaultDelayBeforeFade() + listView.getScrollBarFadeDuration(), null);


                } else if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {

                    Log.d("", "test_t: touch");
//                    tvMsg.clearAnimation();

//                    UxManager.getInstance().fadeInView(tvMsg, listView.getScrollBarDefaultDelayBeforeFade() + listView.getScrollBarFadeDuration(), null);

                    UxManager.getInstance().clearAnimationTo(tvMsg, true);

                } else if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    Log.d("", "test_t: fling");

                }

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                tvMsg.setText("" + listView.getFirstVisiblePosition() + "/" + listView.getAdapter().getCount() + " "
                        + listView.computeVerticalScrollOffset() + " " + listView.computeVerticalScrollExtent() + " " + listView.computeVerticalScrollRange());

                double ratioOfListView = (double) (listView.computeVerticalScrollOffset() + listView.computeVerticalScrollExtent() / 2) / listView.computeVerticalScrollRange();

                int offset = (int) (listView.getHeight() * ratioOfListView);

                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvMsg.getLayoutParams();
                lp.topMargin = offset - tvMsg.getHeight() / 2;
                tvMsg.setLayoutParams(lp);


            }
        });


    }


    private void updateMap() {

        double bear = 0;

        try {
            bear = FineOrientationManager.getInstance().calculateYawPitchRoll(
                    GSensor.getInstance().getLastKnownX(), GSensor.getInstance().getLastKnownY(), GSensor.getInstance().getLastKnownZ(),
                    MagneticSensor.getInstance().getLastKnownX(), MagneticSensor.getInstance().getLastKnownY(), MagneticSensor.getInstance().getLastKnownZ()).getYaw();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            DataProcessor.getInstance().lowPassFilterForAngle(bearList, 40, bear);

            bear = bearList.get(0);

            bear = Math.round(bear * 10000) / 10000;


            double lat = LBSManager.getInstance().getLastKnownLocation().getLatitude();
            double lng = LBSManager.getInstance().getLastKnownLocation().getLongitude();


            ((GoogleMap) mapAdapter.getMap()).moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(17).bearing((float) bear).build()));
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
