package com.tommytao.a5steak.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.android.volley.RequestQueue;
import com.tommytao.a5steak.common.Foundation;


/**
 * Responsible for getting gravity field reading
 */
public class RotationVectorSensor extends Foundation implements SensorEventListener {

    private static RotationVectorSensor instance;

    public static RotationVectorSensor getInstance() {

        if (instance == null)
            instance = new RotationVectorSensor();

        return instance;
    }

    private RotationVectorSensor() {

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

//    public void connect() {
//        super.connect();
//    }

    public void disconnect() {
        super.disconnect();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        super.onAccuracyChanged(sensor, accuracy);
    }

    // --

//    public boolean exists() {
//
//        PackageManager pkgManager = appContext.getPackageManager();
//        return pkgManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER) &&
//                pkgManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
//
//    }

    protected Sensor getSensor() {
        if (sensor == null)
            sensor = getSensorManager().getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        return sensor;
    }

    // --
    @Override
    public void connect() {
        getSensorManager().registerListener(this, getSensor(), 16000);
    }

    // --

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor != getSensor())
            return;

//        lastKnownX = sensorEvent.values[0];
//        lastKnownY = sensorEvent.values[1];
//        lastKnownZ = sensorEvent.values[2];

        float[] rotationMatrix = new float[16];
        float[] values = new float[3];

        SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);
        SensorManager.getOrientation(rotationMatrix, values);

        lastKnownX = (float) Math.toDegrees(values[0]);
        lastKnownY = (float) Math.toDegrees(values[1]);
        lastKnownZ = (float) Math.toDegrees(values[2]);

        for (OnReadingChangeListener onReadingChangeListener : onReadingChangeListenerList) {
            onReadingChangeListener.onReadingChanged(lastKnownX, lastKnownY, lastKnownZ);
        }
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
