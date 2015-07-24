package com.tommytao.a5steak.util.sensor.support;

import com.tommytao.a5steak.util.Foundation;

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



    private double calculateAngleDerivation(double from, double to){


        from = halfToWholeCircleBearing(normalizeToOneLoopBearing(from));
        to = halfToWholeCircleBearing(normalizeToOneLoopBearing(to));

        double choice1 = to - from;
        double choice2 = (to >= from) ? (- (from + 360 -to)) : (360 - from + to);


        return (Math.abs(choice1) <= Math.abs(choice2)) ? choice1 : choice2;


    }


    /**
     *
     * Note: Waiting to test
     *
     * @param lowPassHistoryList
     * @param maxHistorySize
     * @param latestValue
     *
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

        double revisedLatestValue = halfToWholeCircleBearing(normalizeToOneLoopBearing(lowPassHistoryList.get(0))) + calculateAngleDerivation(lowPassHistoryList.get(0), latestValue) * (1-DEFAULT_STRENGTH_OF_LPF);
        revisedLatestValue = normalizeToOneLoopBearing(revisedLatestValue);
        revisedLatestValue = wholeToHalfCircleBearing(revisedLatestValue);


        lowPassHistoryList.add(0, revisedLatestValue);

        while (lowPassHistoryList.size() > maxHistorySize)
            lowPassHistoryList.remove(lowPassHistoryList.size() - 1);


    }




}
