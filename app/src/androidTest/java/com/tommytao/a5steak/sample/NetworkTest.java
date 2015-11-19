package com.tommytao.a5steak.sample;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.tommytao.a5steak.util.NetworkInfoManager;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class NetworkTest extends ApplicationTestCase<Application> {

    public NetworkTest() {
        super(Application.class);

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        createApplication();
    }



    public void testNetwork() throws Exception {

        final CountDownLatch signal = new CountDownLatch(1);

        NetworkInfoManager.getInstance().isGoogleAccessible(new NetworkInfoManager.Listener() {
            @Override
            public void onComplete(boolean accessible) {

                if (accessible)
                    signal.countDown();

            }
        });


        assertTrue(signal.await(10, TimeUnit.SECONDS));

    }


}