package com.tommytao.a5steak.util.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.tommytao.a5steak.util.Foundation;


/**
 * Responsible for getting gravity field reading
 *
 */
public class LightSensor extends Foundation implements SensorEventListener {

    private static LightSensor instance;

    public static LightSensor getInstance() {

        if (instance == null)
            instance = new LightSensor();

        return instance;
    }

    private LightSensor() {

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



    protected Sensor getSensor() {
        if (sensor == null)
            sensor = getSensorManager().getDefaultSensor(Sensor.TYPE_LIGHT);

        return sensor;
    }

    // --


    public float getLastKnownLux() {
        return super.getLastKnownX();
    }


}
