package com.tommytao.a5steak.util;

public class MagneticSensor extends Foundation {

    private static MagneticSensor instance;

    public static MagneticSensor getInstance() {

        if (instance == null)
            instance = new MagneticSensor();

        return instance;
    }

    private MagneticSensor() {

    }

    // --






}
