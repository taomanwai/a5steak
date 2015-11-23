package com.tommytao.a5steak.sample;

import android.test.ApplicationTestCase;
import android.text.TextUtils;

import com.tommytao.a5steak.util.Foundation;
import com.tommytao.a5steak.util.NetworkInfoManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

//    public void testNetwork() throws Exception {
//
//        assertTrue(NetworkInfoManager.getInstance().isConnected());
//
//    }
//
//    public void testGoogleGeocodeNetwork() throws Exception {
//
//        String link = "http://maps.google.com/maps/api/geocode/json?address=長沙灣政府合署&components=country:HK&language=en-US";
//
//        TestUtils.startLinkNetworkTestAndAssert(this, link);
//
//    }
//
//    public void testGoogleNetwork() throws Exception {
//
//        String link = "http://google.com";
//
//        TestUtils.startLinkNetworkTestAndAssert(this, link);
//
//    }
//
//    public void testYahooNetwork() throws Exception {
//        String link = "http://yahoo.com";
//
//        TestUtils.startLinkNetworkTestAndAssert(this, link);
//
//    }
//
//    public void testHotmailNetwork() throws Exception {
//        String link = "http://hotmail.com";
//
//        TestUtils.startLinkNetworkTestAndAssert(this, link);
//
//    }

    public void testPost() throws Exception {

        String link = "http://api.bosonnlp.com/tag/analysis?oov_level=4";

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        headers.put("X-Token", "Se9DNydp.3665.8FudbgZo3UGG");

        String rawStr = "\"\\u7c89\\u4e1d\"";


        Foundation f = new Foundation();
        f.init(MainApp.getContext(), MainApp.getRequestQueue());


        final CountDownLatch signal = new CountDownLatch(1);
        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);
        final ArrayList<String> replies = new ArrayList<>();
        replies.add("");
        f.httpPostString(link, rawStr, headers, new Foundation.OnHttpPostStringListener() {
            @Override
            public void onComplete(String responseStr) {
                succeeds.set(0, !TextUtils.isEmpty(responseStr));
                replies.set(0, responseStr);
                signal.countDown();
            }
        });

        assertTrue("Timeout occurs", signal.await(100, TimeUnit.SECONDS));

        assertTrue("Not accessible: reply: " + replies.get(0), succeeds.get(0));




    }


}