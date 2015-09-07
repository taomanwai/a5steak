package com.tommytao.a5steak.util.sensor;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.tommytao.a5steak.util.Foundation;


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
        return appContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
    }

    protected Sensor getSensor() {

        if (sensor == null)
            sensor = getSensorManager().getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        return sensor;
    }

    // --

    public float getLastKnownXInuT() {
        return super.getLastKnownX();
    }

    public float getLastKnownYInuT() {
        return super.getLastKnownY();
    }

    public float getLastKnownZInuT() {
        return super.getLastKnownZ();
    }

    public double getLastKnownMagnitudeInuT() {
        return super.getLastKnownMagnitude();
    }


}
