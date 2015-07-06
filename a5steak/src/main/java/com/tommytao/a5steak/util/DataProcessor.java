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
     * @param latestValue
     * @param lowPassHistories
     * @param maxHistorySize
     * @return
     */
    public ArrayList<Double> lowPassFilter(double latestValue, ArrayList<Double> lowPassHistories, int maxHistorySize) {


        if (maxHistorySize == 0)
            return new ArrayList<Double>();

        if (lowPassHistories == null || lowPassHistories.isEmpty()) {
            ArrayList<Double> singleResult = new ArrayList<Double>();
            singleResult.add(latestValue);
            return singleResult;

        }
        ArrayList<Double> result = new ArrayList<Double>(lowPassHistories);

        double revisedLatestValue = result.get(0) * DEFAULT_STRENGTH_OF_LPF + latestValue * (1 - DEFAULT_STRENGTH_OF_LPF);


        result.add(0, revisedLatestValue);

        while (result.size() > maxHistorySize)
            result.remove(result.size() - 1);


        return result;

    }




}
