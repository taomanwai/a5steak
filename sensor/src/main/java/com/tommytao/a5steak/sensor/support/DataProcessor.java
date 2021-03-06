package com.tommytao.a5steak.sensor.support;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.tommytao.a5steak.common.Foundation;

import java.util.ArrayList;

public class DataProcessor extends Foundation {

    private static DataProcessor instance;

    public static DataProcessor getInstance() {

        if (instance == null)
            instance = new DataProcessor();

        return instance;
    }

    private DataProcessor() {

    }

    // --

    public static final double DEFAULT_STRENGTH_OF_LPF = 0.9f;

    @Deprecated
    public boolean init(Context context) {
        return super.init(context);
    }


    @Deprecated
    public boolean init(Context context, RequestQueue requestQueue) {
        return super.init(context, requestQueue);
    }

    @Deprecated
    public boolean isInitialized() {
        return super.isInitialized();
    }

    /**
     *
     * Low pass filter
     *
     * Ref:
     * http://www.kircherelectronics.com/blog/index.php/11-android/sensors/9-low-pass-filter-optimizing-alpha
     * http://www.kircherelectronics.com/blog/index.php/11-android/sensors/10-low-pass-filter-linear-acceleration
     *
     * @param lowPassHistoryList
     * @param maxHistorySize
     * @param latestValue
     * @param strength
     *
     *
     */
    @Override
    public void lowPassFilter(ArrayList<Double> lowPassHistoryList, int maxHistorySize, double latestValue, double strength) {
        super.lowPassFilter(lowPassHistoryList, maxHistorySize, latestValue, strength);
    }

    /**
     *
     * Low pass filter (suitable for angle)
     *
     * @param lowPassHistoryList
     * @param maxHistorySize
     * @param latestValue
     * @param strength
     *
     */
    public void lowPassFilterForAngle(ArrayList<Double> lowPassHistoryList, int maxHistorySize, double latestValue, double strength) {

      super.lowPassFilterForAngle(lowPassHistoryList, maxHistorySize, latestValue, strength);


    }




}
