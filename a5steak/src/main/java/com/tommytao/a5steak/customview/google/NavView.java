package com.tommytao.a5steak.customview.google;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;

/**
 * Created by tommytao on 12/8/15.
 */
public class NavView extends MapView {

    public NavView(Context context) {
        super(context);
    }

    public NavView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public NavView(Context context, GoogleMapOptions options) {
        super(context, options);
    }



}
