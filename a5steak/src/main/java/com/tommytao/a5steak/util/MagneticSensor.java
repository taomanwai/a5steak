package com.tommytao.a5steak.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MagneticSensor extends Foundation implements SensorEventListener {

    private static MagneticSensor instance;

    public static MagneticSensor getInstance() {

        if (instance == null)
            instance = new MagneticSensor();

        return instance;
    }

    private MagneticSensor() {

    }


    // --

    private float lastKnownFluxX = Float.NaN;
    private float lastKnownFluxY = Float.NaN;
    private float lastKnownFluxZ = Float.NaN;


    private SensorManager sensorManager;
    private Sensor sensor;

    public SensorManager getSensorManager() {

        if (sensorManager == null)
            sensorManager = (SensorManager) appContext.getSystemService(Context.SENSOR_SERVICE);

        return sensorManager;
    }

    public Sensor getSensor() {

        if (sensor == null)
            sensor = getSensorManager().getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        return sensor;
    }

    public float getLastKnownFluxX() {
        return lastKnownFluxX;
    }

    public float getLastKnownFluxY() {
        return lastKnownFluxY;
    }

    public float getLastKnownFluxZ() {
        return lastKnownFluxZ;
    }

    public double getLastKnownFluxMagnitude() {

        if (Float.isNaN(lastKnownFluxX) ||
                Float.isNaN(lastKnownFluxY) ||
                Float.isNaN(lastKnownFluxZ))
            return Double.NaN;

        return  Math.sqrt(lastKnownFluxX*lastKnownFluxX + lastKnownFluxY*lastKnownFluxY + lastKnownFluxZ*lastKnownFluxZ);


    }

    public void connect() {

        getSensorManager().registerListener(this, getSensor(), SensorManager.SENSOR_DELAY_FASTEST);

    }

    public void disconnect() {
        getSensorManager().unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor != getSensor())
            return;

        lastKnownFluxX = sensorEvent.values[0];
        lastKnownFluxY = sensorEvent.values[1];
        lastKnownFluxZ = sensorEvent.values[2];


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}
