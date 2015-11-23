package com.tommytao.a5steak.sample;

import android.test.ApplicationTestCase;

import com.tommytao.a5steak.util.NetworkInfoManager;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class NetworkTest extends ApplicationTestCase<MainApp> {

    public NetworkTest() {
        super(MainApp.class);

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        NetworkInfoManager.getInstance().removeContextB4UnitTest();

        createApplication();

    }

    public void testNetwork() throws Exception {

        assertTrue(NetworkInfoManager.getInstance().isConnected());

    }

    public void testGoogleGeocodeNetwork() throws Exception {

        String link = "http://maps.google.com/maps/api/geocode/json?address=長沙灣政府合署&components=country:HK&language=en-US";

        TestUtils.startLinkNetworkTestAndAssert(this, link);

    }

    public void testGoogleNetwork() throws Exception {

        String link = "http://google.com";

        TestUtils.startLinkNetworkTestAndAssert(this, link);

    }

    public void testYahooNetwork() throws Exception {
        String link = "http://yahoo.com";

        TestUtils.startLinkNetworkTestAndAssert(this, link);

    }

    public void testHotmailNetwork() throws Exception {
        String link = "http://hotmail.com";

        TestUtils.startLinkNetworkTestAndAssert(this, link);

    }




}