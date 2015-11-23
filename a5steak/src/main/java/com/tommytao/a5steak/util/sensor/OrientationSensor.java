package com.tommytao.a5steak.util.sensor;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.android.volley.RequestQueue;
import com.tommytao.a5steak.util.Foundation;


/**
 * Responsible for getting gravity field reading
 */
public class OrientationSensor extends Foundation implements SensorEventListener {

    private static OrientationSensor instance;

    public static OrientationSensor getInstance() {

        if (instance == null)
            instance = new OrientationSensor();

        return instance;
    }

    private OrientationSensor() {

    }

    // --

    @Override
    public boolean init(Context context) {
        return super.init(context);
    }

    @Deprecated
    public boolean init(Context context, RequestQueue requestQueue) {
        return super.init(context, requestQueue);
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

    public boolean exists() {

        PackageManager pkgManager = appContext.getPackageManager();
        return pkgManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER) &&
                pkgManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
    }

    protected Sensor getSensor() {
        if (sensor == null)
            sensor = getSensorManager().getDefaultSensor(Sensor.TYPE_ORIENTATION);

        return sensor;
    }

    // --


    public float getLastKnownYaw() {
        return super.getLastKnownX();
    }


    public float getLastKnownPitch() {
        return super.getLastKnownY();
    }


    public float getLastKnownRoll() {
        return super.getLastKnownZ();
    }


}
