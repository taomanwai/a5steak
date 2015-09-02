package com.tommytao.a5steak.sample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.FaceDetector;
import android.os.Bundle;
import android.util.Log;

import com.tommytao.a5steak.util.BitmapManager;
import com.tommytao.a5steak.util.Encyclopedia;
import com.tommytao.a5steak.util.google.gapiclient.VisionGApiAnalyzer;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

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

        VisionGApiAnalyzer.getInstance().init(this);

        VisionGApiAnalyzer.getInstance().connect(null);

        BitmapManager.getInstance().init(this);


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

        osmMapView.setBuiltInZoomControls(true);








    }

    @OnClick(R.id.btnGet)
    public void get() {

        Bitmap bm = BitmapManager.getInstance().loadResId(R.drawable.face3, -1, -1, false);

        ArrayList<FaceDetector.Face> faces = VisionGApiAnalyzer.getInstance().findFacesFromBitmap(bm, 3);

        Log.d("", "");


    }

    @OnClick(R.id.btnShare)
    public void share() {




    }


}
