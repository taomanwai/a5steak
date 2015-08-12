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

    @Override
    public void addOnReadingChangeListener(OnReadingChangeListener onReadingChangeListener) {
        super.addOnReadingChangeListener(onReadingChangeListener);
    }

    @Override
    public boolean removeOnReadingChangeListener(OnReadingChangeListener onReadingChangeListener) {
        return super.removeOnReadingChangeListener(onReadingChangeListener);
    }

    private Sensor getSensor() {

        if (sensor == null)
            sensor = getSensorManager().getDefaultSensor(Sensor.TYPE_ORIENTATION);



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


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // do nothing
    }


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
