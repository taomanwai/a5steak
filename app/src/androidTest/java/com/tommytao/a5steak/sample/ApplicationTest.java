package com.tommytao.a5steak.sample;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;

import com.tommytao.a5steak.util.Encyclopedia;
import com.tommytao.a5steak.util.google.GeocodeManager;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    public ApplicationTest() {
        super(Application.class);


    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        createApplication();
    }


    // == GeocodeManager ==

    public void testGeocodeManager_Get_shouldAssumeAsLocaleUsWhenLocaleIsNull() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        GeocodeManager.getInstance().get(Encyclopedia.HKSIL_LAT, Encyclopedia.HKSIL_LNG, null, new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode == null)
                    return;

                if (!"25 Yuen Wo Rd, Sha Tin, Hong Kong".equals(geocode.getFormattedAddress()))
                    return;

                signal.countDown();

            }
        });

        assertTrue(signal.await(8, TimeUnit.SECONDS));


    }

    public void testGeocodeManager_Get_shouldReturnNullWhenOneOfLatIsNaN() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        GeocodeManager.getInstance().get(Double.NaN, Encyclopedia.HKSIL_LNG, null, new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode == null)
                    signal.countDown();
                else {
                    // strange
                }

            }
        });

        assertTrue(signal.await(8, TimeUnit.SECONDS));

    }

    public void testGeocodeManager_Get_shouldReturnNullWhenOneOfLngIsNaN() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        GeocodeManager.getInstance().get(Encyclopedia.HKSIL_LAT, Double.NaN, null, new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode == null)
                    signal.countDown();
                else {
                    // strange
                }

            }
        });

        assertTrue(signal.await(8, TimeUnit.SECONDS));

    }

    public void testGeocodeManager_Get_shouldReturnNullWhenBothLatLngIsNaN() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        GeocodeManager.getInstance().get(Double.NaN, Double.NaN, null, new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode == null)
                    signal.countDown();
                else {
                    // strange
                }

            }
        });

        assertTrue(signal.await(8, TimeUnit.SECONDS));

    }

    public void testGeocodeManager_Get_shouldReturnNullWhenLatIsOutOfEarth() {

    }

    public void testGeocodeManager_Get_shouldReturnNullWhenLngIsOutOfEarth() {

    }

    // == PlacesApiManager ==


}