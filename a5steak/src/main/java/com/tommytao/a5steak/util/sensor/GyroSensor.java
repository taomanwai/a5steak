package com.tommytao.a5steak.util.sensor;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.tommytao.a5steak.util.Foundation;


/**
 * Responsible for getting gyroscope reading (calibrated)
 *
 * Note:
 * Waiting to test
 * Power consumption is around 3-30 times of that of accelerometer
 * Ref: <a href="http://stackoverflow.com/questions/20693547/when-to-use-accelerometer-or-gyroscope-on-android">here</a>
 * Not all phones have gyroscope (e.g. some HTC cheap phones has no gyroscope), please use exists() to check availability of gyroscope
 *
 */
public class GyroSensor extends Foundation implements SensorEventListener {

    private static GyroSensor instance;

    public static GyroSensor getInstance() {

        if (instance == null)
            instance = new GyroSensor();

        return instance;
    }

    private GyroSensor() {

    }

    // --

    public boolean exists(){
        PackageManager packageManager = appContext.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
    }

    private Sensor getSensor() {

        if (sensor == null)
            sensor = getSensorManager().getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        return sensor;
    }

    public void connect() {

        getSensorManager().registerListener(this, getSensor(), SensorManager.SENSOR_DELAY_GAME);

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

    public float getLastKnownDeltaRotationX() {
        return super.getLastKnownX();
    }


    public float getLastKnownDeltaRotationY() {
        return super.getLastKnownY();
    }

    public float getLastKnownDeltaRotationZ() {
        return super.getLastKnownZ();
    }



}
