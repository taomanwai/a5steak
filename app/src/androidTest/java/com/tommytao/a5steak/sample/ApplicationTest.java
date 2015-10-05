package com.tommytao.a5steak.sample;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testIt(){

        Context context = getContext();
//        String s = context.getString(R.string.app_name);

        assertEquals("test_it", "A5Steak", getSystemContext().getString(R.string.app_name));

    }

}