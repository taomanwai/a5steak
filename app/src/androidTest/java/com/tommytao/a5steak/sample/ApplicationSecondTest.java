package com.tommytao.a5steak.sample;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.text.TextUtils;
import android.util.Log;

import com.tommytao.a5steak.util.Encyclopedia;
import com.tommytao.a5steak.util.MathManager;
import com.tommytao.a5steak.util.google.GeocodeManager;
import com.tommytao.a5steak.util.google.PlacesApiManager;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationSecondTest extends ApplicationTestCase<Application> {

    public static int AWAIT_TIME_IN_SECOND = 8; // 8

    public ApplicationSecondTest() {
        super(Application.class);


    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        createApplication();
    }


    // == GeocodeManager ==

    public void testGeocodeManager_Get_shouldReturnEnWhenLocaleEnUs() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);


        GeocodeManager.getInstance().get(Encyclopedia.HKSIL_LAT, Encyclopedia.HKSIL_LNG, new Locale("en", "US"), new GeocodeManager.OnGetListener() {
            @Override
            public void returnGeocode(GeocodeManager.Geocode geocode) {

                if (geocode == null)
                    return;

                if (!"25 Yuen Wo Rd, Sha Tin, Hong Kong".equals(geocode.getFormattedAddress()))
                    return;

                signal.countDown();

            }
        });

        assertTrue(signal.await(AWAIT_TIME_IN_SECOND, TimeUnit.SECONDS));

    }

}