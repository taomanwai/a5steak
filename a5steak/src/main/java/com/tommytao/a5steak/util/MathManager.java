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
    public boolean init(Context appContext) {
        return super.init(appContext);
    }

    public double calculateAngleDerivation(double from, double to){
        return super.calculateAngleDerivation(from, to);
    }


}
