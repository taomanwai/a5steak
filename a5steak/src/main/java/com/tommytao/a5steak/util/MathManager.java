package com.tommytao.a5steak.util;

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

    public double calculateAngleDerivation(double from, double to){
        return super.calculateAngleDerivation(from, to);
    }


}
