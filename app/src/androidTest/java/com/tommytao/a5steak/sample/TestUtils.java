package com.tommytao.a5steak.sample;

import android.test.ApplicationTestCase;

import com.tommytao.a5steak.util.Foundation;
import com.tommytao.a5steak.util.NetworkInfoManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by tommytao on 20/11/2015.
 */
public class TestUtils {

    public static void startLinkNetworkTestAndAssert(ApplicationTestCase testCase, String link) throws Exception {

//         link = "http://maps.google.com/maps/api/geocode/json?address=長沙灣政府合署&components=country:HK&language=en-US";

//         link = "http://hotmail.com";
//         link = "http://google.com";
//         link = "http://yahoo.com";


        final CountDownLatch signal = new CountDownLatch(1);
        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);

        if (!NetworkInfoManager.getInstance().isInitialized())
            NetworkInfoManager.getInstance().init(MainApp.getContext());

        NetworkInfoManager.getInstance().isLinkAccessibleInJSON(link, new NetworkInfoManager.IsLinkAccessibleInJSONListener() {
            @Override
            public void onComplete(boolean accessible, JSONObject jObj) {
                succeeds.set(0, accessible);
                signal.countDown();
            }
        });


        testCase.assertTrue("Timeout occurs", signal.await(Foundation.DEFAULT_CONNECT_READ_TIMEOUT_IN_MS, TimeUnit.MILLISECONDS));

        testCase.assertTrue("Not accessible", succeeds.get(0));
    }

}
