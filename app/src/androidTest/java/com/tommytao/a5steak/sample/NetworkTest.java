package com.tommytao.a5steak.sample;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.tommytao.a5steak.util.NetworkInfoManager;

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

        assertTrue(NetworkInfoManager.getInstance().isConnected());

    }



    public void testMiscNetwork() throws Exception {
        String link = "http://maps.google.com/maps/api/geocode/json?address=長沙灣政府合署&components=country:HK&language=en-US&client=gme-easyvanhongkonglimited&signature=IN92Tp0yF_fRMBzpo4JUtZUUzcA=";

        TestUtils.startLinkNetworkTestAndAssert(this, link);


    }


}