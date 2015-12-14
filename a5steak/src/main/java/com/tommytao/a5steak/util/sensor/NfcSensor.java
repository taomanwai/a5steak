package com.tommytao.a5steak.util.sensor;

import android.hardware.SensorEventListener;

import com.tommytao.a5steak.common.util.Foundation;


/**
 * Responsible for getting NFC reading
 *
 * Note: Under construction
 *
 */
public class NfcSensor extends Foundation implements SensorEventListener {

    private static NfcSensor instance;

    public static NfcSensor getInstance() {

        if (instance == null)
            instance = new NfcSensor();

        return instance;
    }

    private NfcSensor() {

    }

    // --


}
