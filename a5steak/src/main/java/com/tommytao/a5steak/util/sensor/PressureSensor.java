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

    public boolean exists(){
        PackageManager packageManager = appContext.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER);
    }

    private Sensor getSensor() {

        if (sensor == null)
            sensor = getSensorManager().getDefaultSensor(Sensor.TYPE_PRESSURE);


        return sensor;
    }

    public void connect() {

        getSensorManager().registerListener(this, getSensor(), DEFAULT_SENSOR_DELAY_LEVEL);

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

    public float getPressureInMBar(){

        return super.getLastKnownX();

    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // do nothing
    }





}
