package com.tommytao.a5steak.sensor;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.android.volley.RequestQueue;
import com.tommytao.a5steak.common.Foundation;


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
        return appContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
    }

    @Override
    protected Sensor getSensor() {

        if (sensor == null)
            sensor = getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        return sensor;
    }

    // --

    @Override
    public double getLastKnownMagnitude() {
        return super.getLastKnownMagnitude();
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



}
