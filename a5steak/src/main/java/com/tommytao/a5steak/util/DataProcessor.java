package com.tommytao.a5steak.util;

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


    /**
     *
     * Low past filter
     *
     * Ref:
     * http://www.kircherelectronics.com/blog/index.php/11-android/sensors/9-low-pass-filter-optimizing-alpha
     * http://www.kircherelectronics.com/blog/index.php/11-android/sensors/10-low-pass-filter-linear-acceleration
     *
     * @param lowPassHistoryList
     * @param maxHistorySize
     * @param latestValue
     *
     *
     */
    public void lowPassFilter(ArrayList<Double> lowPassHistoryList, int maxHistorySize, double latestValue) {


        if (maxHistorySize == 0) {
            lowPassHistoryList.clear();
            return;
        }

        if (lowPassHistoryList.isEmpty()){
            lowPassHistoryList.add(latestValue);
            return;
        }


        double revisedLatestValue = lowPassHistoryList.get(0) * DEFAULT_STRENGTH_OF_LPF + latestValue * (1 - DEFAULT_STRENGTH_OF_LPF);


        lowPassHistoryList.add(0, revisedLatestValue);

        while (lowPassHistoryList.size() > maxHistorySize)
            lowPassHistoryList.remove(lowPassHistoryList.size() - 1);


    }


    /**
     *
     * Note: Under construction
     *
     * @param lowPassHistoryList
     * @param maxHistorySize
     * @param latestValue
     */
    public void lowPassFilterForAngle(ArrayList<Double> lowPassHistoryList, int maxHistorySize, double latestValue) {


        if (maxHistorySize == 0) {
            lowPassHistoryList.clear();
            return;
        }

        if (lowPassHistoryList.isEmpty()){
            lowPassHistoryList.add(latestValue);
            return;
        }

        double revisedLatestValue = lowPassHistoryList.get(0) * DEFAULT_STRENGTH_OF_LPF + latestValue * (1 - DEFAULT_STRENGTH_OF_LPF);

        lowPassHistoryList.add(0, revisedLatestValue);

        while (lowPassHistoryList.size() > maxHistorySize)
            lowPassHistoryList.remove(lowPassHistoryList.size() - 1);


    }




}
