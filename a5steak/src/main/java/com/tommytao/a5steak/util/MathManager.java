package com.tommytao.a5steak.util;

import android.content.Context;

public class MathManager extends Foundation {

    private static MathManager instance;

    public static MathManager getInstance() {

        if (instance == null)
            instance = new MathManager();

        return instance;
    }

    private MathManager() {

    }


    // --

    @Deprecated
    public boolean init(Context context) {
        return super.init(context);
    }

    @Deprecated
    public boolean isInitialized() {
        return super.isInitialized();
    }

    @Override
    public double calculateAngleDerivation(double from, double to){
        return super.calculateAngleDerivation(from, to);
    }

    @Override
    public double normalizeToOneLoopBearing(double value) {
        return super.normalizeToOneLoopBearing(value);
    }

    @Override
    public double halfToWholeCircleBearing(double value) {
        return super.halfToWholeCircleBearing(value);
    }

    @Override
    public double wholeToHalfCircleBearing(double value) {
        return super.wholeToHalfCircleBearing(value);
    }


}
