package com.tommytao.a5steak.sample;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class MathManagerTest extends ApplicationTestCase<Application> {

    public MathManagerTest() {
        super(Application.class);

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        createApplication();
    }

//    public void testCalculateAngleDerivation_shouldReturnNaNWhenNaNFrom() throws Exception {
//
//        double result = MathManager.getInstance().calculateAngleDerivation(Double.NaN, 10);
//
//        assertTrue(Double.isNaN(result));
//
//    }
//
//    public void testCalculateAngleDerivation_shouldReturnNaNWhenNaNTo() throws Exception {
//
//        double result = MathManager.getInstance().calculateAngleDerivation(10, Double.NaN);
//        assertTrue(Double.isNaN(result));
//
//    }
//
//    public void testCalculateAngleDerivation_shouldReturnDerivationWhenFromOverOneLoop() throws Exception {
//
//        double result = MathManager.getInstance().calculateAngleDerivation(370, 20);
//        assertTrue(result == 10);
//
//    }
//
//    public void testCalculateAngleDerivation_shouldReturnDerivationWhenToOverOneLoop() throws Exception {
//
//        double result = MathManager.getInstance().calculateAngleDerivation(10, 380);
//        assertTrue(result == 10);
//
//    }
//
//    public void testCalculateAngleDerivation_shouldReturnDerivationWhenBothFromAndToOverOneLoop() throws Exception {
//
//        double result = MathManager.getInstance().calculateAngleDerivation(370, 380);
//        assertTrue(result == 10);
//
//    }
//
//    public void testCalculateAngleDerivation_shouldReturnNegativeDerivationWhenDerivationLargerThan180() throws Exception {
//
//        double result = MathManager.getInstance().calculateAngleDerivation(10, 200);
//        assertTrue(result == -170);
//
//    }
//
//    public void testNormalizeToOneLoopBearing_shouldReturnKeepUnchangeWhenAngleLesserThan360() throws Exception {
//
//        double result = MathManager.getInstance().normalizeToOneLoopBearing(70);
//        assertTrue(result == 70);
//
//    }
//
//    public void testNormalizeToOneLoopBearing_shouldReturnNoLoopComponentResultWhenAngleLargerThan360() throws Exception {
//
//        double result = MathManager.getInstance().normalizeToOneLoopBearing(370);
//        assertTrue(result == 10);
//
//
//    }
//
//    public void testNormalizeToOneLoopBearing_shouldReturnKeepUnchangeWhenAngleNegativeAndLargerThanMinus360() throws Exception {
//
//        double result = MathManager.getInstance().normalizeToOneLoopBearing(-20);
//        assertTrue(result == -20);
//
//    }
//
//    public void testNormalizeToOneLoopBearing_shouldReturnNoLoopComponentResultWhenAngleeNegativeAndLesserThanMinus360() throws Exception {
//
//        double result = MathManager.getInstance().normalizeToOneLoopBearing(-370);
//        assertTrue(result == -10);
//
//    }




}