package com.tommytao.a5steak.util.sensor;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

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
    @Override
    public boolean init(Context context) {
        return super.init(context);
    }

    @Override
    public void addOnReadingChangeListener(OnReadingChangeListener onReadingChangeListener) {
        super.addOnReadingChangeListener(onReadingChangeListener);
    }

    @Override
    public boolean removeOnReadingChangeListener(OnReadingChangeListener onReadingChangeListener) {
        return super.removeOnReadingChangeListener(onReadingChangeListener);
    }

    @Override
    public double getLastKnownMagnitude() {
        return super.getLastKnownMagnitude();
    }

    public void connect() {

        super.connect();

    }

    public void disconnect() {
        super.disconnect();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        super.onSensorChanged(event);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        super.onAccuracyChanged(sensor, accuracy);
    }

    // --

    public boolean exists(){
        return appContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
    }

    protected Sensor getSensor() {
        if (sensor == null)
            sensor = getSensorManager().getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        return sensor;
    }

    // --

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
