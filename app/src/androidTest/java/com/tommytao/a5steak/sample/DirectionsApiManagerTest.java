package com.tommytao.a5steak.sample;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.tommytao.a5steak.util.Foundation;
import com.tommytao.a5steak.util.google.DirectionsApiManager;
import com.tommytao.a5steak.util.google.PlacesApiManager;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class DirectionsApiManagerTest extends ApplicationTestCase<Application> {

    public final static int AWAIT_TIME_IN_MS = Foundation.DEFAULT_CONNECT_READ_TIMEOUT_IN_MS * PlacesApiManager.DEFAULT_MAX_NUM_OF_RETRIES;

    public DirectionsApiManagerTest() {
        super(Application.class);


    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        DirectionsApiManager.getInstance().removeContextB4UnitTest();

        createApplication();
    }




    public void testRoute_shouldReturnEnWhenLocaleEnglish() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);


        DirectionsApiManager.getInstance().route(22.337154, 114.147422, 22.336023, 114.155276, "", Locale.US, new DirectionsApiManager.OnRouteListener() {
            @Override
            public void returnSteps(ArrayList<DirectionsApiManager.Step> steps, DirectionsApiManager.Polyline polyline) {

                if ("0.7 km".equals(steps.get(0).getDistanceInText())
                        && "Head east on Cheung Sha Wan Rd toward Tai Nan W St".equals(steps.get(0).getInstructionsInText())
                        && "avigCmluwTYgAkA{E_@{AK_@iAwECK}@cECc@Ag@Hw@Rc@p@cA^g@~A_C".equals("" + steps.get(0).getPolyline())
                        )
                    succeeds.set(0, true);

                signal.countDown();
            }
        });

        TestUtils.assertResult(this, signal, succeeds, AWAIT_TIME_IN_MS);

    }


}