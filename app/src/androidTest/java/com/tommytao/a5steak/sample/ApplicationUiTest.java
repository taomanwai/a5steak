package com.tommytao.a5steak.sample;

import android.test.ActivityInstrumentationTestCase2;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationUiTest extends ActivityInstrumentationTestCase2<MainActivity> {


    public ApplicationUiTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();


    }

    public void testUiDevice(){

        onView(withId(R.id.ediInput)).perform(typeText("abc"));
        onView(withId(R.id.btnGo)).perform(click());

        onView(withId(R.id.tvMsg)).check(matches(withText("abc")));


    }






}