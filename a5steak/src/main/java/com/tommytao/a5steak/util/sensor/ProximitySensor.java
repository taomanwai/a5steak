package com.tommytao.a5steak.util.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.android.volley.RequestQueue;
import com.tommytao.a5steak.util.Foundation;


/**
 * Responsible for getting gravity field reading
 */
public class ProximitySensor extends Foundation implements SensorEventListener {

    private static ProximitySensor instance;

    public static ProximitySensor getInstance() {

        if (instance == null)
            instance = new ProximitySensor();

        return instance;
    }

    private ProximitySensor() {

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

    public final float NEAR_PROXIMITY_BOUNDARY_IN_CM = 5;


    protected Sensor getSensor() {
        if (sensor == null)
            sensor = getSensorManager().getDefaultSensor(Sensor.TYPE_PROXIMITY);

        return sensor;
    }

    // --


    public boolean getLastKnownProximity() {
        float proximity = super.getLastKnownX();

        return proximity < NEAR_PROXIMITY_BOUNDARY_IN_CM;
    }


}
