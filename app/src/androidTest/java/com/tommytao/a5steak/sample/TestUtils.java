package com.tommytao.a5steak.sample;

import android.test.ApplicationTestCase;

import com.tommytao.a5steak.util.Foundation;
import com.tommytao.a5steak.util.NetworkInfoManager;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by tommytao on 20/11/2015.
 */
public class TestUtils {

    public static void startLinkNetworkTestAndAssert(ApplicationTestCase testCase, String link) throws Exception {


        final CountDownLatch signal = new CountDownLatch(1);
        final ArrayList<Boolean> succeeds = new ArrayList<>();
        succeeds.add(false);
        final ArrayList<String> replies = new ArrayList<>();
        replies.add("");

        if (!NetworkInfoManager.getInstance().isInitialized())
            NetworkInfoManager.getInstance().init(MainApp.getContext());

        NetworkInfoManager.getInstance().isLinkAccessibleInStr(link, new NetworkInfoManager.IsLinkAccessibleInStrListener() {
            @Override
            public void onComplete(boolean accessible, String str) {
                succeeds.set(0, accessible);
                replies.set(0, str);
                signal.countDown();
            }
        });

        testCase.assertTrue("Timeout occurs", signal.await(Foundation.DEFAULT_CONNECT_READ_TIMEOUT_IN_MS, TimeUnit.MILLISECONDS));

        testCase.assertTrue("Not accessible: reply: " + replies.get(0), succeeds.get(0));
    }

}
