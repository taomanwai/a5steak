package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.tommytao.a5steak.util.BitmapManager;
import com.tommytao.a5steak.util.FbManager;
import com.tommytao.a5steak.util.google.BarcodeSensor;
import com.tommytao.a5steak.util.google.FaceSensor;
import com.tommytao.a5steak.util.sensor.ProximitySensor;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {


    @Bind(R.id.shimmer)
    ShimmerFrameLayout shimmer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        BitmapManager.getInstance().init(this);

        FbManager.getInstance().init(this);

        FaceSensor.getInstance().init(this);

        BarcodeSensor.getInstance().init(this);

        ProximitySensor.getInstance().init(this);

        ProximitySensor.getInstance().connect();

        shimmer.startShimmerAnimation();




    }


    @OnClick(R.id.btnGo)
    public void go() {

        boolean b = ProximitySensor.getInstance().getLastKnownProximity();


        String s = "proximity_t: " + b;

        Toast.makeText(this, s , Toast.LENGTH_LONG).show();

    }

    @OnClick(R.id.btnGet)
    public void get() {




    }

    @OnClick(R.id.btnShare)
    public void share() {




    }


}
