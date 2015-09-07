package com.tommytao.a5steak.util.sensor;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.tommytao.a5steak.util.Foundation;


/**
 * Responsible for reading pressure in mBar unit
 *
 */
public class PressureSensor extends Foundation implements SensorEventListener {

    private static PressureSensor instance;

    public static PressureSensor getInstance() {

        if (instance == null)
            instance = new PressureSensor();

        return instance;
    }

    private PressureSensor() {
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
        return appContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER);
    }

    protected Sensor getSensor() {
        if (sensor == null)
            sensor = getSensorManager().getDefaultSensor(Sensor.TYPE_PRESSURE);

        return sensor;
    }

    // --

    public float getPressureInMBar(){
        return super.getLastKnownX();
    }


}
