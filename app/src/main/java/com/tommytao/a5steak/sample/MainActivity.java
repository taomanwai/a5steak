package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

import com.tommytao.a5steak.util.Encyclopedia;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {

    @Bind(R.id.osmMapView)
    MapView osmMapView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);



    }


    @OnClick(R.id.btnGo)
    public void go() {

//        public static final double HKSIL_LAT = 22.394190;
//        public static final double HKSIL_LNG = 114.202048;

        Location location = new Location("");
        location.setLatitude(Encyclopedia.HK_SPACE_MUSEUM_LAT);
        location.setLongitude(Encyclopedia.HK_SPACE_MUSEUM_LNG);


        osmMapView.getController().setZoom(15);
        osmMapView.getController().setCenter(new GeoPoint(location));





    }

    @OnClick(R.id.btnGet)
    public void get() {


    }

    @OnClick(R.id.btnShare)
    public void share() {




    }


}
