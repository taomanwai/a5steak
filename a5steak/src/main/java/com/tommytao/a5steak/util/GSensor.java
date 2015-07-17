package com.tommytao.a5steak.util;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


/**
 * Responsible for getting gravity field reading
 *
 */
public class GSensor extends Foundation implements SensorEventListener {

    private static GSensor instance;

    public static GSensor getInstance() {

        if (instance == null)
            instance = new GSensor();

        return instance;
    }

    private GSensor() {

    }

    // --


    @Override
    public void addOnReadingChangeListener(OnReadingChangeListener onReadingChangeListener) {
        super.addOnReadingChangeListener(onReadingChangeListener);
    }

    @Override
    public boolean removeOnReadingChangeListener(OnReadingChangeListener onReadingChangeListener) {
        return super.removeOnReadingChangeListener(onReadingChangeListener);
    }

    private Sensor getSensor() {

        if (sensor == null)
            sensor = getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER);



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
