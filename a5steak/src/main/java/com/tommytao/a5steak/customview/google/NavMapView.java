package com.tommytao.a5steak.customview.google;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.tommytao.a5steak.util.google.DirectionsApiManager;

/**
 * Created by tommytao on 12/8/15.
 */
public class NavMapView extends MapView {

    public NavMapView(Context context) {
        super(context);
        init();
    }

    public NavMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NavMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public NavMapView(Context context, GoogleMapOptions options) {
        super(context, options);
        init();
    }

    private void init(){

        DirectionsApiManager.getInstance().init()

    }




    public void startNavigation(){

    }



}
