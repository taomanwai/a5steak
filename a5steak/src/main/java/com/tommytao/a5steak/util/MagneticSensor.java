package com.tommytao.a5steak.util;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


/**
 * Responsible for getting magnetic field reading (calibrated)
 *
 * Note:
 * Electrical devices usually emits 15 or 20 uT (<a href="http://www.magneticsciences.com/EMF-health/">Ref</a>)
 * Earth naturally emits 15 or 20 uT (<a href="https://en.wikipedia.org/wiki/Earth%27s_magnetic_field">Ref</a>)
 *
 */
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

    private Sensor getSensor() {

        if (sensor == null)
            sensor = getSensorManager().getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        return sensor;
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

        lastKnownX = sensorEvent.values[0];
        lastKnownY = sensorEvent.values[1];
        lastKnownZ = sensorEvent.values[2];

        for (OnReadingChangeListener onReadingChangeListener : onReadingChangeListenerList){
            onReadingChangeListener.onReadingChanged(lastKnownX, lastKnownY, lastKnownZ);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // do nothing
    }

    @Override
    public float getLastKnownX() {
        return super.getLastKnownX();
    }

    @Override
    public float getLastKnownY() {
        return super.getLastKnownY();
    }

    @Override
    public float getLastKnownZ() {
        return super.getLastKnownZ();
    }

    @Override
    public double getLastKnownMagnitude() {
        return super.getLastKnownMagnitude();
    }


}
